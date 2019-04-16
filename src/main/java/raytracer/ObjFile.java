package raytracer;

// import com.google.common.flogger.FluentLogger;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import java.io.BufferedReader;
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

public class ObjFile {
  // TODO: fix
  //  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  public static ObjFile parse(Reader rawReader) throws IOException, ParsingException {
    return new FileParser(rawReader).parse();
  }

  public static ObjFile parseFile(String filename) throws IOException, ParsingException {
    return parse(new FileReader(filename));
  }

  public static ObjFile parseResource(String resourceFilename)
      throws IOException, ParsingException {
    InputStream inputStream = ObjFile.class.getResourceAsStream(resourceFilename);
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
        String[] terms = line.split(" ");
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
        // TODO: fix
        //        logger.atInfo().log("Ignoring command: %s", Joiner.on(' ').join(terms));
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
          throw new ParsingException("Vertex command must be of form 'v [x] [y] [z]");
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
          throw new ParsingException("Face command must be of form 'f [i1] [i2] [i3] ...");
        }
        Tuple p1 = objfile.getVertex(parseInt(terms[1]));
        for (int i = 2; i < terms.length - 1; ++i) {
          Tuple p2 = objfile.getVertex(parseInt(terms[i]));
          Tuple p3 = objfile.getVertex(parseInt(terms[i + 1]));
          currentGroup.triangles.add(Triangle.createRaw(p1, p2, p3));
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
}
