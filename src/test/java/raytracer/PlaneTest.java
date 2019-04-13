package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.MatrixSubject.assertThat;
import static raytracer.Testing.EPSILON;
import static raytracer.TupleSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Planes
public class PlaneTest {

  @Test
  // Scenario: The normal of a plane is constant everywhere
  public void normalIsConstant() {
    Geometry p = new Plane();
    assertThat(p.normalAt(Tuple.point(0, 0, 0))).isEqualTo(Tuple.vector(0, 1, 0));
    assertThat(p.normalAt(Tuple.point(10, 0, -10))).isEqualTo(Tuple.vector(0, 1, 0));
    assertThat(p.normalAt(Tuple.point(-5, 0, 150))).isEqualTo(Tuple.vector(0, 1, 0));
  }

  @Test
  // Scenario: Intersect with a ray parallel to the plane
  public void intersectParallel() {
    Shape p = Plane.create();
    Ray r = Ray.create(Tuple.point(0, 10, 0), Tuple.vector(0, 0, 1));
    assertThat(p.intersect(r).length()).isEqualTo(0);
  }

  @Test
  // Scenario: Intersect with a coplanar ray
  public void intersectCoplanar() {
    Shape p = Plane.create();
    Ray r = Ray.create(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
    assertThat(p.intersect(r).length()).isEqualTo(0);
  }

  @Test
  // Scenario: A ray intersecting a plane from above
  public void intersectFromAbove() {
    Shape p = Plane.create();
    Ray r = Ray.create(Tuple.point(0, 1, 0), Tuple.vector(0, -1, 0));
    Intersections xs = p.intersect(r);
    assertThat(xs.length()).isEqualTo(1);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(1);
    //    assertThat(xs.get(0).shape()).isEqualTo(p);
  }

  @Test
  // Scenario: A ray intersecting a plane from below
  public void intersectFromBelow() {
    Shape p = Plane.create();
    Ray r = Ray.create(Tuple.point(0, -1, 0), Tuple.vector(0, 1, 0));
    Intersections xs = p.intersect(r);
    assertThat(xs.length()).isEqualTo(1);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(1);
    //    assertThat(xs.get(0).shape()).isEqualTo(p);
  }
}
