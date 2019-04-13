package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.MatrixSubject.assertThat;
import static raytracer.Testing.EPSILON;
import static raytracer.TupleSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Spheres
public class SphereTest {

  @Test
  // Scenario: A ray intersects a sphere at two points
  public void intersectRay2Points() {
    Ray r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
    Shape s = Sphere.create();
    Intersections xs = s.intersect(r);
    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(4);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(6);
  }

  @Test
  // Scenario: A ray intersects a sphere at a tangent
  public void intersectRayTangent() {
    Ray r = Ray.create(Tuple.point(0, 1, -5), Tuple.vector(0, 0, 1));
    Shape s = Sphere.create();
    Intersections xs = s.intersect(r);
    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(5);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(5);
  }

  @Test
  // Scenario: A ray misses a sphere
  public void intersectRayMiss() {
    Ray r = Ray.create(Tuple.point(0, 2, -5), Tuple.vector(0, 0, 1));
    Shape s = Sphere.create();
    Intersections xs = s.intersect(r);
    assertThat(xs.length()).isEqualTo(0);
  }

  @Test
  // Scenario: A ray originates inside a sphere
  public void intersectRayStartInside() {
    Ray r = Ray.create(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
    Shape s = Sphere.create();
    Intersections xs = s.intersect(r);
    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(-1);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(1);
  }

  @Test
  // Scenario: A sphere is behind a ray
  public void intersectRayStartPast() {
    Ray r = Ray.create(Tuple.point(0, 0, 5), Tuple.vector(0, 0, 1));
    Shape s = Sphere.create();
    Intersections xs = s.intersect(r);
    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(-6);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(-4);
  }

  @Test
  // Scenario: Intersect sets the object on the intersection
  public void intersectSetsShape() {
    Ray r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
    Shape s = Sphere.create();
    Intersections xs = s.intersect(r);
    assertThat(xs.length()).isEqualTo(2);
    //    assertThat(xs.get(0).shape()).isEqualTo(s);
    //    assertThat(xs.get(1).shape()).isEqualTo(s);
  }

  @Test
  // Scenario: A sphere's default transformation
  public void defaultTransformIsIdentity() {
    Shape s = Sphere.create();
    assertThat(s.transform()).isApproximatelyEqualTo(Matrix.identity());
  }

  @Test
  // Scenario: Changing a sphere's transformation
  public void changeTransform() {
    Shape s = Sphere.create();
    Matrix t = Matrix.translation(2, 3, 4);
    s.setTransform(t);
    assertThat(s.transform()).isApproximatelyEqualTo(t);
  }

  @Test
  // Scenario: Intersecting a scaled sphere with a ray
  public void intersectScaled() {
    Ray r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
    Shape s = Sphere.create();
    s.setTransform(Matrix.scaling(2, 2, 2));
    Intersections xs = s.intersect(r);
    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(3);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(7);
  }

  @Test
  // Scenario: Intersecting a translated sphere with a ray
  public void intersectTranslated() {
    Ray r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
    Shape s = Sphere.create();
    s.setTransform(Matrix.translation(5, 0, 0));
    Intersections xs = s.intersect(r);
    assertThat(xs.length()).isEqualTo(0);
  }

  @Test
  // Scenario: The normal on a sphere at a point on the x axis
  public void normalAtX() {
    Geometry s = new Sphere();
    Tuple n = s.normalAt(Tuple.point(1, 0, 0));
    assertThat(n).isEqualTo(Tuple.vector(1, 0, 0));
  }

  @Test
  // Scenario: The normal on a sphere at a point on the y axis
  public void normalAtY() {
    Geometry s = new Sphere();
    Tuple n = s.normalAt(Tuple.point(0, 1, 0));
    assertThat(n).isEqualTo(Tuple.vector(0, 1, 0));
  }

  @Test
  // Scenario: The normal on a sphere at a point on the z axis
  public void normalAtZ() {
    Geometry s = new Sphere();
    Tuple n = s.normalAt(Tuple.point(0, 0, 1));
    assertThat(n).isEqualTo(Tuple.vector(0, 0, 1));
  }

  @Test
  // Scenario: The normal on a sphere at a nonaxial point
  public void normalAtNonaxial() {
    Geometry s = new Sphere();
    Tuple n = s.normalAt(Tuple.point(1 / Math.sqrt(3), 1 / Math.sqrt(3), 1 / Math.sqrt(3)));
    assertThat(n)
        .isEqualTo(Tuple.vector(1 / Math.sqrt(3), 1 / Math.sqrt(3), 1 / Math.sqrt(3)));
  }

  @Test
  // Scenario: The normal is a normalized vector
  public void normalIsNormalized() {
    Geometry s = new Sphere();
    Tuple n = s.normalAt(Tuple.point(1 / Math.sqrt(3), 1 / Math.sqrt(3), 1 / Math.sqrt(3)));
    assertThat(n).isEqualTo(n.normalize());
  }

  /*
  // TODO reinstate
    @Test
    // Scenario: Computing the normal on a translated sphere
    public void normalOnTranslated() {
      Shape s = Sphere.create();
      s.setTransform(Matrix.translation(0, 1, 0));
      Tuple n = s.normalAt(Tuple.point(0, 1.70711, -0.70711));
      assertThat(n).isApproximatelyEqualTo(Tuple.vector(0, 0.70711, -0.70711));
    }

    @Test
    // Scenario: Computing the normal on a transformed sphere
    public void normalOnTransformed() {
      Shape s = Sphere.create();
      s.setTransform(Matrix.rotationZ(Math.PI / 5).scale(1, 0.5, 1));
      Tuple n = s.normalAt(Tuple.point(0, 1 / Math.sqrt(2), -1 / Math.sqrt(2)));
      assertThat(n).isApproximatelyEqualTo(Tuple.vector(0, 0.97014, -0.24254));
    }
  */

  @Test
  // Scenario: A sphere has a default material
  public void hasDefaultMaterial() {
    Shape s = Sphere.create();
    assertThat(s.material()).isEqualTo(Material.create());
  }

  @Test
  // Scenario: A sphere may be assigned a material
  public void assignMaterial() {
    Shape s = Sphere.create();
    Material m = Material.builder().setAmbient(1.0).build();
    s.setMaterial(m);
    assertThat(s.material()).isEqualTo(m);
  }

  @Test
  // Scenario: A helper for producing a sphere with a glassy material
  public void glassSphere() {
    Shape s = Sphere.createGlass();
    Material m = s.material();
    assertThat(m.transparency()).isEqualTo(1.0);
    assertThat(m.refractiveIndex()).isEqualTo(1.52);
  }
}
