package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.Testing.EPSILON;

import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: OBJ File Parser
public class ObjFileTest {

  @Test
  // Scenario: Ignoring unrecognized lines
  public void ignoreUnrecognizedLines() throws IOException, ObjFile.ParsingException {
    String input =
        String.join(
            "\n",
            "There was a young lady named Bright",
            "who traveled much faster than light.",
            "She set out one day",
            "in a relative way,",
            "and came back the previous night.");
    ObjFile objFile = ObjFile.parseContent(input);
    assertThat(objFile.ignoredCommandCount()).isEqualTo(5);
  }

  @Test
  // Scenario: Vertex records
  public void vertexRecords() throws Exception {
    String input = String.join("\n", "v -1 1 0", "v -1.0000 0.5000 0.0000", "v 1 0 0", "v 1 1 0");
    ObjFile objFile = ObjFile.parseContent(input);
    assertThat(objFile.ignoredCommandCount()).isEqualTo(0);
    assertThat(objFile.getVertexCount()).isEqualTo(4);
    assertThat(objFile.getVertex(1)).isEqualTo(Tuple.point(-1, 1, 0));
    assertThat(objFile.getVertex(2)).isEqualTo(Tuple.point(-1, 0.5, 0));
    assertThat(objFile.getVertex(3)).isEqualTo(Tuple.point(1, 0, 0));
    assertThat(objFile.getVertex(4)).isEqualTo(Tuple.point(1, 1, 0));
  }

  @Test
  // Scenario: Parsing triangle faces
  public void triangleFaces() throws Exception {
    String input =
        String.join("\n", "v -1 1 0", "v -1 0 0", "v 1 0 0", "v 1 1 0", "", "f 1 2 3", "f 1 3 4");
    ObjFile objFile = ObjFile.parseContent(input);
    assertThat(objFile.ignoredCommandCount()).isEqualTo(0);
    ObjFile.TriangleGroup group = objFile.getDefaultGroup();
    assertThat(group.getTriangleCount()).isEqualTo(2);
    Triangle triangle1 = group.getTriangle(1);
    Triangle triangle2 = group.getTriangle(2);
    assertThat(triangle1.p1()).isEqualTo(objFile.getVertex(1));
    assertThat(triangle1.p2()).isEqualTo(objFile.getVertex(2));
    assertThat(triangle1.p3()).isEqualTo(objFile.getVertex(3));
    assertThat(triangle2.p1()).isEqualTo(objFile.getVertex(1));
    assertThat(triangle2.p2()).isEqualTo(objFile.getVertex(3));
    assertThat(triangle2.p3()).isEqualTo(objFile.getVertex(4));
  }

  @Test
  // Scenario: Triangulating polygons
  public void trianglulatePolygon() throws Exception {
    String input =
        String.join(
            "\n", "v -1 1 0", "v -1 0 0", "v 1 0 0", "v 1 1 0", "v 0 2 0", "", "f 1 2 3 4 5");
    ObjFile objFile = ObjFile.parseContent(input);
    assertThat(objFile.ignoredCommandCount()).isEqualTo(0);
    ObjFile.TriangleGroup group = objFile.getDefaultGroup();
    assertThat(group.getTriangleCount()).isEqualTo(3);

    Triangle triangle1 = group.getTriangle(1);
    assertThat(triangle1.p1()).isEqualTo(objFile.getVertex(1));
    assertThat(triangle1.p2()).isEqualTo(objFile.getVertex(2));
    assertThat(triangle1.p3()).isEqualTo(objFile.getVertex(3));

    Triangle triangle2 = group.getTriangle(2);
    assertThat(triangle2.p1()).isEqualTo(objFile.getVertex(1));
    assertThat(triangle2.p2()).isEqualTo(objFile.getVertex(3));
    assertThat(triangle2.p3()).isEqualTo(objFile.getVertex(4));

    Triangle triangle3 = group.getTriangle(3);
    assertThat(triangle3.p1()).isEqualTo(objFile.getVertex(1));
    assertThat(triangle3.p2()).isEqualTo(objFile.getVertex(4));
    assertThat(triangle3.p3()).isEqualTo(objFile.getVertex(5));
  }

  @Test
  // Scenario: Triangles in groups
  public void groups() throws Exception {
    ObjFile objFile = ObjFile.parseResource("/triangles.obj");
    assertThat(objFile.ignoredCommandCount()).isEqualTo(0);

    ObjFile.TriangleGroup group1 = objFile.getGroup("FirstGroup");
    assertThat(group1).isNotNull();
    assertThat(group1.getTriangleCount()).isEqualTo(1);
    Triangle triangle1 = group1.getTriangle(1);
    assertThat(triangle1.p1()).isEqualTo(objFile.getVertex(1));
    assertThat(triangle1.p2()).isEqualTo(objFile.getVertex(2));
    assertThat(triangle1.p3()).isEqualTo(objFile.getVertex(3));

    ObjFile.TriangleGroup group2 = objFile.getGroup("SecondGroup");
    assertThat(group2).isNotNull();
    assertThat(group2.getTriangleCount()).isEqualTo(1);
    Triangle triangle2 = group2.getTriangle(1);
    assertThat(triangle2.p1()).isEqualTo(objFile.getVertex(1));
    assertThat(triangle2.p2()).isEqualTo(objFile.getVertex(3));
    assertThat(triangle2.p3()).isEqualTo(objFile.getVertex(4));
  }

  @Test
  // Scenario: Converting an OBJ file to a group
  public void asShape() throws Exception {
    ObjFile objFile = ObjFile.parseResource("/triangles.obj");
    assertThat(objFile.ignoredCommandCount()).isEqualTo(0);
    Shape topShape = objFile.asShape();
    // peek inside topShape
    assertThat(topShape).isInstanceOf(Group.class);
    List<Shape> innerShapes = ((Group) topShape).shapes();
    assertThat(innerShapes.size()).isEqualTo(2);
    assertSingleTriangleGroup(innerShapes.get(0));
    assertSingleTriangleGroup(innerShapes.get(1));
  }

  public void assertSingleTriangleGroup(Shape shape) {
    assertThat(shape).isInstanceOf(Group.class);
    List<Shape> innerShapes = ((Group) shape).shapes();
    assertThat(innerShapes.size()).isEqualTo(1);
    assertThat(innerShapes.get(0)).isInstanceOf(GeometryShape.class);
    GeometryShape geometryShape = (GeometryShape) innerShapes.get(0);
    assertThat(geometryShape.geometry()).isInstanceOf(Triangle.class);
  }

  @Test
  // Scenario: Vertex normal records
  public void vertexNormal() throws Exception {
    String input = String.join("\n", "vn 0 0 1", "vn 0.707 0 -0.707", "vn 1 2 3");
    ObjFile objFile = ObjFile.parseContent(input);
    assertThat(objFile.ignoredCommandCount()).isEqualTo(0);
    assertThat(objFile.getNormalCount()).isEqualTo(3);
    assertThat(objFile.getNormal(1)).isEqualTo(Tuple.vector(0, 0, 1));
    assertThat(objFile.getNormal(2)).isEqualTo(Tuple.vector(0.707, 0, -0.707));
    assertThat(objFile.getNormal(3)).isEqualTo(Tuple.vector(1, 2, 3));
  }

  @Test
  // Scenario: Faces with normals
  public void faceNormal() throws Exception {
    String input =
        String.join(
            "\n",
            "v 0 1 0",
            "v -1 0 0",
            "v 1 0 0",
            "",
            "vn -1 0 0",
            "vn 1 0 0",
            "vn 0 1 0",
            "",
            "f 1//3 2//1 3//2",
            "f 1/0/3 2/102/1 3/14/2");
    ObjFile objFile = ObjFile.parseContent(input);
    assertThat(objFile.ignoredCommandCount()).isEqualTo(0);
    ObjFile.TriangleGroup group = objFile.getDefaultGroup();
    assertThat(group.getTriangleCount()).isEqualTo(2);

    Triangle triangle1 = group.getTriangle(1);
    assertThat(triangle1.p1()).isEqualTo(objFile.getVertex(1));
    assertThat(triangle1.p2()).isEqualTo(objFile.getVertex(2));
    assertThat(triangle1.p3()).isEqualTo(objFile.getVertex(3));
    assertThat(triangle1.n1()).isEqualTo(objFile.getNormal(3));
    assertThat(triangle1.n2()).isEqualTo(objFile.getNormal(1));
    assertThat(triangle1.n3()).isEqualTo(objFile.getNormal(2));

    Triangle triangle2 = group.getTriangle(2);
    assertThat(triangle2.p1()).isEqualTo(objFile.getVertex(1));
    assertThat(triangle2.p2()).isEqualTo(objFile.getVertex(2));
    assertThat(triangle2.p3()).isEqualTo(objFile.getVertex(3));
    assertThat(triangle2.n1()).isEqualTo(objFile.getNormal(3));
    assertThat(triangle2.n2()).isEqualTo(objFile.getNormal(1));
    assertThat(triangle2.n3()).isEqualTo(objFile.getNormal(2));
  }

  @Test
  // Scenario: Bounding box
  public void boundingBox() throws Exception {
    String input =
        String.join("\n", "v 0 0 0", "v 0 0 1", "v 1 0 1", "v 1 1 0", "f 1 2 3", "f 2 3 4");
    ObjFile objFile = ObjFile.parseContent(input);
    Shape box = objFile.boundingBox();

    Ray xRay = Ray.create(Tuple.point(-1, 0.5, 0.5), Tuple.vector(1, 0, 0));
    Intersections xxs = box.intersect(xRay);
    assertThat(xxs.length()).isEqualTo(2);
    assertThat(xxs.get(0).t()).isWithin(EPSILON).of(1);
    assertThat(xxs.get(1).t()).isWithin(EPSILON).of(2);

    Ray yRay = Ray.create(Tuple.point(0.5, -1, 0.5), Tuple.vector(0, 1, 0));
    Intersections yxs = box.intersect(yRay);
    assertThat(yxs.length()).isEqualTo(2);
    assertThat(yxs.get(0).t()).isWithin(EPSILON).of(1);
    assertThat(yxs.get(1).t()).isWithin(EPSILON).of(2);

    Ray zRay = Ray.create(Tuple.point(0.5, 0.5, -1), Tuple.vector(0, 0, 1));
    Intersections zxs = box.intersect(zRay);
    assertThat(zxs.length()).isEqualTo(2);
    assertThat(zxs.get(0).t()).isWithin(EPSILON).of(1);
    assertThat(zxs.get(1).t()).isWithin(EPSILON).of(2);
  }
}
