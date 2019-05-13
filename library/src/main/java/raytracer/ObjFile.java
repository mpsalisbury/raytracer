package raytracer;

import com.google.auto.value.AutoValue;
import com.google.common.base.Joiner;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.common.flogger.FluentLogger;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ObjFile {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  public static ObjFile parse(Reader rawReader) throws IOException, ParsingException {
    return new FileParser(rawReader).parse();
  }

  public static ObjFile parseFile(String filename) throws IOException, ParsingException {
    return parse(new FileReader(filename));
  }

  public static ObjFile parseResource(String resourceFilename)
      throws IOException, ParsingException {
    InputStream inputStream = ObjFile.class.getResourceAsStream(resourceFilename);
    if (inputStream == null) {
      throw new FileNotFoundException("Can't load resource " + resourceFilename);
    }
    return parse(new InputStreamReader(inputStream));
  }

  public static ObjFile parseContent(String content) throws IOException, ParsingException {
    return parse(new StringReader(content));
  }

  private int ignoredCommandCount = 0;
  private List<Tuple> vertices = new ArrayList<>();
  private List<Tuple> normals = new ArrayList<>();
  private Map<String, TriangleGroup> groups = new HashMap<>();

  public Shape asShape() {
    Group group = Group.create();
    for (TriangleGroup subgroup : groups.values()) {
      if (subgroup.getTriangleCount() > 0) {
        group.add(subgroup.asShape());
      }
    }
    return group;
  }

  // Returns cube-oid bounding box containing all triangles.
  public Shape boundingBox() {
    Range<Double> xRange = getRange(p -> p.x());
    Range<Double> yRange = getRange(p -> p.y());
    Range<Double> zRange = getRange(p -> p.z());
    return makeBoundingBox(xRange, yRange, zRange);
  }

  private Range<Double> getRange(ToDoubleFunction<Tuple> getCoord) {
    double min = forEachTriangleVertex().mapToDouble(getCoord).min().orElse(0);
    double max = forEachTriangleVertex().mapToDouble(getCoord).max().orElse(0);
    return Range.closed(min, max);
  }

  private Stream<Tuple> forEachTriangleVertex() {
    return groups
        .values()
        .stream()
        .flatMap(g -> g.triangles())
        .flatMap(t -> Stream.of(t.p1(), t.p2(), t.p3()));
  }

  // Returns a cube-oid with the given dimension ranges.
  private Shape makeBoundingBox(Range<Double> xRange, Range<Double> yRange, Range<Double> zRange) {
    double xSize = xRange.upperEndpoint() - xRange.lowerEndpoint();
    double xMiddle = (xRange.upperEndpoint() + xRange.lowerEndpoint()) / 2;
    double ySize = yRange.upperEndpoint() - yRange.lowerEndpoint();
    double yMiddle = (yRange.upperEndpoint() + yRange.lowerEndpoint()) / 2;
    double zSize = zRange.upperEndpoint() - zRange.lowerEndpoint();
    double zMiddle = (zRange.upperEndpoint() + zRange.lowerEndpoint()) / 2;
    Matrix transform =
        Matrix.scaling(xSize / 2, ySize / 2, zSize / 2).translate(xMiddle, yMiddle, zMiddle);

    Shape cube = Cube.create();
    cube.setTransform(transform);
    Group group = Group.create();
    group.add(cube);
    return group;
  }

  public int ignoredCommandCount() {
    return ignoredCommandCount;
  }

  public int getGroupCount() {
    return groups.size();
  }

  public static final String DEFAULT_GROUP_NAME = "default";

  // Returns name-based group, or null if no such group.
  public TriangleGroup getGroup(String name) {
    return groups.get(name);
  }

  public TriangleGroup getDefaultGroup() {
    return getGroup(DEFAULT_GROUP_NAME);
  }

  // Create group if it doesn't already exist.
  private TriangleGroup ensureGroup(String name) {
    if (!groups.containsKey(name)) {
      groups.put(name, new TriangleGroup());
    }
    return getGroup(name);
  }

  public int getVertexCount() {
    return vertices.size();
  }

  // Returns 1-based vertex (1,2,3,...)
  public Tuple getVertex(int i) {
    return vertices.get(i - 1);
  }

  public int getNormalCount() {
    return normals.size();
  }

  // Returns 1-based normal (1,2,3,...)
  public Tuple getNormal(int i) {
    return normals.get(i - 1);
  }

  // Holds a group of triangles.
  public class TriangleGroup {
    private List<Triangle> triangles = new ArrayList<>();

    public Shape asShape() {
      Group group = Group.create();
      for (Triangle triangle : triangles) {
        group.add(triangle.asShape());
      }
      return group;
    }

    public int getTriangleCount() {
      return triangles.size();
    }

    // Returns 1-based triangle (1,2,3,...)
    public Triangle getTriangle(int i) {
      return triangles.get(i - 1);
    }

    public Stream<Triangle> triangles() {
      return triangles.stream();
    }
  }

  public static class ParsingException extends Exception {
    public ParsingException(String message) {
      super(message);
    }

    public ParsingException(Throwable cause) {
      super(cause);
    }

    public ParsingException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private static class FileParser {
    private BufferedReader reader;
    // The ObjFile being constructed.
    private ObjFile objfile;
    private TriangleGroup currentGroup;
    private List<CommandParser> commandParsers;

    private static final Pattern VERTEX_NORMAL_PATTERN = Pattern.compile("(\\d*)/\\d*/(\\d*)");

    public FileParser(Reader rawReader) {
      this.reader = new BufferedReader(rawReader);
      this.objfile = new ObjFile();
      this.currentGroup = objfile.ensureGroup(DEFAULT_GROUP_NAME);
      this.commandParsers =
          ImmutableList.of(
              new VertexParser(),
              new VertexNormalParser(),
              new FaceParser(),
              new GroupParser(),
              new EmptyParser());
    }

    public ObjFile parse() throws IOException, ParsingException {
      while (true) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        String[] terms = line.split("\\s+");
        // Note: even split of empty string produces one term.
        Verify.verify(terms.length != 0);
        getParser(terms[0]).parse(terms);
      }
      return objfile;
    }

    public CommandParser getParser(String token) {
      for (CommandParser parser : commandParsers) {
        if (parser.handlesToken(token)) {
          return parser;
        }
      }
      return new UnknownCommandParser();
    }

    private String joinTerms(String[] terms) {
      return Joiner.on(' ').join(terms);
    }

    private abstract class CommandParser {
      // Returns the token that this command parser handles.
      public abstract String getToken();
      // Parses the given command terms.
      public abstract void parse(String[] terms) throws ParsingException;

      // Returns true iff this parser handles the given command token.
      public boolean handlesToken(String token) {
        return getToken().equals(token);
      }

      protected int parseInt(String term) throws ParsingException {
        try {
          return Integer.parseInt(term);
        } catch (NumberFormatException e) {
          throw new ParsingException(e);
        }
      }

      protected double parseDouble(String term) throws ParsingException {
        try {
          return Double.parseDouble(term);
        } catch (NumberFormatException e) {
          throw new ParsingException(e);
        }
      }
    }

    // Record count of commands we don't understand.
    private class EmptyParser extends CommandParser {
      @Override
      public String getToken() {
        return "";
      }

      @Override
      public void parse(String[] terms) {}
    }

    // Record count of commands we don't understand.
    private class UnknownCommandParser extends CommandParser {
      @Override
      public String getToken() {
        // not used.
        return "!!!";
      }

      @Override
      public void parse(String[] terms) {
        logger.atInfo().log("Ignoring command: %s", Joiner.on(' ').join(terms));
        ++objfile.ignoredCommandCount;
      }
    }

    // Parses a vertex command.
    private class VertexParser extends CommandParser {
      @Override
      public String getToken() {
        return "v";
      }

      @Override
      public void parse(String[] terms) throws ParsingException {
        if (terms.length != 4) {
          throw new ParsingException(
              String.format(
                  "Vertex command must be of form 'v [x] [y] [z] (was %s)", joinTerms(terms)));
        }
        double x = parseDouble(terms[1]);
        double y = parseDouble(terms[2]);
        double z = parseDouble(terms[3]);
        objfile.vertices.add(Tuple.point(x, y, z));
      }
    }

    // Parses a vertex normal command.
    private class VertexNormalParser extends CommandParser {
      @Override
      public String getToken() {
        return "vn";
      }

      @Override
      public void parse(String[] terms) throws ParsingException {
        if (terms.length != 4) {
          throw new ParsingException("VertexNormal command must be of form 'vn [x] [y] [z]");
        }
        double x = parseDouble(terms[1]);
        double y = parseDouble(terms[2]);
        double z = parseDouble(terms[3]);
        objfile.normals.add(Tuple.vector(x, y, z));
      }
    }
    // Parses a face command.
    private class FaceParser extends CommandParser {
      @Override
      public String getToken() {
        return "f";
      }

      @Override
      public void parse(String[] terms) throws ParsingException {
        if (terms.length < 4) {
          throw new ParsingException(
              "Face command must be of form 'f [i1] [i2] [i3] ...' or 'f 1/2/3 4/5/6 7/8/9 ...'");
        }
        VertexNormal p1 = parseCorner(terms[1]);
        for (int i = 2; i < terms.length - 1; ++i) {
          VertexNormal p2 = parseCorner(terms[i]);
          VertexNormal p3 = parseCorner(terms[i + 1]);
          Triangle triangle = createTriangle(p1, p2, p3);
          currentGroup.triangles.add(triangle);
        }
      }

      private VertexNormal parseCorner(String term) throws ParsingException {
        Matcher matcher = VERTEX_NORMAL_PATTERN.matcher(term);
        if (matcher.matches()) {
          int vertexIndex = parseInt(matcher.group(1));
          Tuple vertex = objfile.getVertex(vertexIndex);
          int normalIndex = parseInt(matcher.group(2));
          Tuple normal = objfile.getNormal(normalIndex);
          return VertexNormal.create(vertex, normal);
        } else {
          int vertexIndex = parseInt(term);
          Tuple vertex = objfile.getVertex(vertexIndex);
          return VertexNormal.create(vertex, null);
        }
      }

      private Triangle createTriangle(VertexNormal p1, VertexNormal p2, VertexNormal p3) {
        if (p1.normal() == null || p2.normal() == null || p3.normal() == null) {
          return Triangle.createRaw(p1.vertex(), p2.vertex(), p3.vertex());
        } else {
          return Triangle.createRaw(
              p1.vertex(), p2.vertex(), p3.vertex(), p1.normal(), p2.normal(), p3.normal());
        }
      }
    }

    // Parses a group command.
    private class GroupParser extends CommandParser {
      @Override
      public String getToken() {
        return "g";
      }

      @Override
      public void parse(String[] terms) throws ParsingException {
        if (terms.length != 2) {
          throw new ParsingException("Group command must be of form 'g [groupName]");
        }
        String newGroupName = terms[1];
        currentGroup = objfile.ensureGroup(newGroupName);
      }
    }
  }

  // Used by FaceParser
  @AutoValue
  public abstract static class VertexNormal {
    public static VertexNormal create(Tuple vertex, Tuple normal) {
      return new AutoValue_ObjFile_VertexNormal(vertex, normal);
    }

    public abstract Tuple vertex();

    @Nullable
    public abstract Tuple normal();
  }
}
