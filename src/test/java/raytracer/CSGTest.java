package raytracer;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Constructive Solid Geometry (CSG)
public class CSGTest {

  private Shape leftShape() {
    Shape left = Sphere.create();
    left.setTransform(Matrix.translation(-0.5, 0, 0));
    return left;
  }

  private Shape rightShape() {
    Shape right = Sphere.create();
    right.setTransform(Matrix.translation(+0.5, 0, 0));
    return right;
  }

  @Test
  // Scenario Outline: Filtering a list of intersections
  public void chooseIntersections() {
    Ray ray = Ray.create(Tuple.point(0, 0, 0), Tuple.vector(1, 0, 0));

    Shape union = CSG.createUnion(leftShape(), rightShape());
    assertIntersections(ray, union, -1.5, 1.5);

    Shape intersection = CSG.createIntersection(leftShape(), rightShape());
    assertIntersections(ray, intersection, -0.5, 0.5);

    Shape difference = CSG.createDifference(leftShape(), rightShape());
    assertIntersections(ray, difference, -1.5, -0.5);
  }

  private void assertIntersections(Ray ray, Shape csg, double t1, double t2) {
    Intersections xs = csg.intersect(ray);
    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isEqualTo(t1);
    assertThat(xs.get(1).t()).isEqualTo(t2);
  }
}
