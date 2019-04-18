package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.Testing.EPSILON;
import static raytracer.TupleSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Triangles
public class TriangleTest {

  // Returns a canonical test triangle.
  private Shape testTriangle() {
    Tuple p1 = Tuple.point(0, 1, 0);
    Tuple p2 = Tuple.point(-1, 0, 0);
    Tuple p3 = Tuple.point(1, 0, 0);
    return Triangle.create(p1, p2, p3);
  }

  private Shape testSmoothTriangle() {
    Tuple p1 = Tuple.point(0, 1, 0);
    Tuple p2 = Tuple.point(-1, 0, 0);
    Tuple p3 = Tuple.point(1, 0, 0);
    Tuple n1 = Tuple.vector(0, 1, 0);
    Tuple n2 = Tuple.vector(-1, 0, 0);
    Tuple n3 = Tuple.vector(1, 0, 0);
    return Triangle.create(p1, p2, p3, n1, n2, n3);
  }

  @Test
  // Scenario: Constructing a triangle
  public void construct() {
    Tuple p1 = Tuple.point(0, 1, 0);
    Tuple p2 = Tuple.point(-1, 0, 0);
    Tuple p3 = Tuple.point(1, 0, 0);
    Triangle t = Triangle.createRaw(p1, p2, p3);
    assertThat(t.p1()).isEqualTo(p1);
    assertThat(t.p2()).isEqualTo(p2);
    assertThat(t.p3()).isEqualTo(p3);
    assertThat(t.e1()).isEqualTo(Tuple.vector(-1, -1, 0));
    assertThat(t.e2()).isEqualTo(Tuple.vector(1, -1, 0));
    assertThat(t.n1()).isApproximatelyEqualTo(Tuple.vector(0, 0, -1));
  }

  /*
    @Test
    // Scenario: Finding the normal on a triangle
    public void normalAt() {
      Tuple p1 = Tuple.point(0, 1, 0);
      Tuple p2 = Tuple.point(-1, 0, 0);
      Tuple p3 = Tuple.point(1, 0, 0);
      Triangle t = Triangle.createRaw(p1, p2, p3);
      assertThat(t.normalAt(Tuple.point(0, 0.5, 0))).isEqualTo(t.n1());
      assertThat(t.normalAt(Tuple.point(-0.5, 0.75, 0))).isEqualTo(t.n1());
      assertThat(t.normalAt(Tuple.point(0.5, 0.25, 0))).isEqualTo(t.n1());
    }
  */

  @Test
  // Scenario: Intersecting a ray parallel to the triangle
  public void intersectParallel() {
    Shape t = testTriangle();
    Ray r = Ray.create(Tuple.point(0, -1, -2), Tuple.vector(0, 1, 0));
    Intersections xs = t.intersect(r);
    assertThat(xs.length()).isEqualTo(0);
  }

  @Test
  // Scenario: A ray misses the p1-p3 edge
  public void rayMissesp1p3() {
    Shape t = testTriangle();
    Ray r = Ray.create(Tuple.point(1, 1, -2), Tuple.vector(0, 0, 1));
    Intersections xs = t.intersect(r);
    assertThat(xs.length()).isEqualTo(0);
  }

  @Test
  // Scenario: A ray misses the p1-p2 edge
  public void rayMissesp1p2() {
    Shape t = testTriangle();
    Ray r = Ray.create(Tuple.point(-1, 1, -2), Tuple.vector(0, 0, 1));
    Intersections xs = t.intersect(r);
    assertThat(xs.length()).isEqualTo(0);
  }

  @Test
  // Scenario: A ray misses the p2-p3 edge
  public void rayMissesp2p3() {
    Shape t = testTriangle();
    Ray r = Ray.create(Tuple.point(0, -1, -2), Tuple.vector(0, 0, 1));
    Intersections xs = t.intersect(r);
    assertThat(xs.length()).isEqualTo(0);
  }

  @Test
  // Scenario: A ray strikes a triangle
  public void rayHits() {
    Shape t = testTriangle();
    Ray r = Ray.create(Tuple.point(0, 0.5, -2), Tuple.vector(0, 0, 1));
    Intersections xs = t.intersect(r);
    assertThat(xs.length()).isEqualTo(1);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(2);
  }

  @Test
  // Scenario: Preparing the normal on a smooth triangle
  public void smoothNormal() {
    Shape t = testSmoothTriangle();
    Ray r = Ray.create(Tuple.point(-0.2, 0.3, -2), Tuple.vector(0, 0, 1));
    Intersections xs = t.intersect(r);
    assertThat(xs.length()).isEqualTo(1);
    assertThat(xs.get(0).normalv()).isApproximatelyEqualTo(Tuple.vector(-0.5547, 0.83205, 0));
  }
}
