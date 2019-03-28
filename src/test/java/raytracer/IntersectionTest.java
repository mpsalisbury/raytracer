package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static raytracer.TupleSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Intersections
public class IntersectionTest {

  static final double EPSILON = 1.0e-5;

  @Test
  // Scenario: An intersection encapsulates t and shape
  // Scenario: Precomputing the state of an intersection
  public void constructIntersection() {
    Ray r = Ray.create(Tuple.createPoint(0, 0, -5), Tuple.createVector(0, 0, 1));
    Sphere s = new Sphere();
    Intersection i = Intersection.create(r, 4, s);
    assertThat(i.t()).isWithin(EPSILON).of(4);
    assertThat(i.shape()).isEqualTo(s);
    assertThat(i.point()).isApproximatelyEqualTo(Tuple.createPoint(0, 0, -1));
    assertThat(i.eyev()).isApproximatelyEqualTo(Tuple.createVector(0, 0, -1));
    assertThat(i.normalv()).isApproximatelyEqualTo(Tuple.createVector(0, 0, -1));
  }

  @Test
  // Scenario: Aggregating intersections
  public void aggregateIntersections() {
    Sphere s = new Sphere();
    Intersection i1 = Intersection.create(1);
    Intersection i2 = Intersection.create(2);
    Intersections xs = new Intersections(i1, i2);
    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isEqualTo(1.0);
    assertThat(xs.get(1).t()).isEqualTo(2.0);
  }

  @Test
  // Scenario: Intersect sets the shape on the intersection
  public void intersectSetsShape() {
    Ray r = Ray.create(Tuple.createPoint(0, 0, -5), Tuple.createVector(0, 0, 1));
    Sphere s = new Sphere();
    Intersections xs = s.intersect(r);
    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).shape()).isEqualTo(s);
    assertThat(xs.get(1).shape()).isEqualTo(s);
  }

  @Test
  // Scenario: The hit, when all intersections have positive t
  public void hitAllPositive() {
    Sphere s = new Sphere();
    Intersection i1 = Intersection.create(1);
    Intersection i2 = Intersection.create(2);
    Intersections xs = new Intersections(i1, i2);
    assertThat(xs.hit()).hasValue(i1);
  }

  @Test
  // Scenario: The hit, when some intersections have negative t
  public void hitSomeNegative() {
    Sphere s = new Sphere();
    Intersection i1 = Intersection.create(-1);
    Intersection i2 = Intersection.create(1);
    Intersections xs = new Intersections(i2, i1);
    assertThat(xs.hit()).hasValue(i2);
  }

  @Test
  // Scenario: The hit, when all intersections have negative t
  public void hitAllNegative() {
    Sphere s = new Sphere();
    Intersection i1 = Intersection.create(-2);
    Intersection i2 = Intersection.create(-1);
    Intersections xs = new Intersections(i2, i1);
    assertThat(xs.hit()).isEmpty();
  }

  @Test
  // Scenario: The hit is always the lowest nonnegative intersection
  public void hitLowestNonNegative() {
    Sphere s = new Sphere();
    Intersection i1 = Intersection.create(5);
    Intersection i2 = Intersection.create(7);
    Intersection i3 = Intersection.create(-3);
    Intersection i4 = Intersection.create(2);
    Intersections xs = new Intersections(i1, i2, i3, i4);
    assertThat(xs.hit()).hasValue(i4);

    Intersection i5 = Intersection.create(0);
    Intersections xs2 = new Intersections(i1, i2, i3, i4, i5);
    assertThat(xs2.hit()).hasValue(i5);
  }

  @Test
  // Scenario: The hit, when an intersection occurs on the outside
  public void hitIntersectOutside() {
    Ray r = Ray.create(Tuple.createPoint(0, 0, -5), Tuple.createVector(0, 0, 1));
    Sphere shape = new Sphere();
    Intersection i = shape.intersect(r).get(0);
    assertThat(i.t()).isWithin(EPSILON).of(4);
    assertThat(i.inside()).isFalse();
  }

  @Test
  // Scenario: The hit, when an intersection occurs on the inside
  public void hitIntersectInside() {
    Ray r = Ray.create(Tuple.createPoint(0, 0, 0), Tuple.createVector(0, 0, 1));
    Sphere shape = new Sphere();
    Intersection i = shape.intersect(r).get(1);
    assertThat(i.t()).isWithin(EPSILON).of(1);
    assertThat(i.point()).isApproximatelyEqualTo(Tuple.createPoint(0, 0, 1));
    assertThat(i.eyev()).isApproximatelyEqualTo(Tuple.createVector(0, 0, -1));
    // Normal would have been (0,0,1), but is inverted on the inside.
    assertThat(i.normalv()).isApproximatelyEqualTo(Tuple.createVector(0, 0, -1));
    assertThat(i.inside()).isTrue();
  }

  @Test
  // Scenario: Precomputing the reflection vector
  public void computeReflectionVector() {
    Shape shape = new Plane();
    Ray r =
        Ray.create(
            Tuple.createPoint(0, 1, -1),
            Tuple.createVector(0, -1 / Math.sqrt(2), 1 / Math.sqrt(2)));
    Intersection i = shape.intersect(r).get(0);
    assertThat(i.reflectv())
        .isApproximatelyEqualTo(Tuple.createVector(0, 1 / Math.sqrt(2), 1 / Math.sqrt(2)));
  }

  @Test
  // Scenario Outline: Finding n1 and n2 at various intersections
  public void computeRefractiveIndices() {
    World w = new World();

    Shape a = new Sphere();
    a.setTransform(Matrix.scaling(2, 2, 2));
    a.setMaterial(Material.builder().setTransparency(1.0).setRefractiveIndex(1.5).build());
    w.addShape(a);

    Shape b = new Sphere();
    b.setTransform(Matrix.translation(0, 0, -0.25));
    b.setMaterial(Material.builder().setTransparency(1.0).setRefractiveIndex(2.0).build());
    w.addShape(b);

    Shape c = new Sphere();
    c.setTransform(Matrix.translation(0, 0, 0.25));
    c.setMaterial(Material.builder().setTransparency(1.0).setRefractiveIndex(2.5).build());
    w.addShape(c);

    Ray r = Ray.create(Tuple.createPoint(0, 0, -4), Tuple.createVector(0, 0, 1));
    Intersections xs = w.intersect(r);

    assertThat(xs.length()).isEqualTo(6);

    assertThat(xs.get(0).t()).isWithin(EPSILON).of(2);
    assertThat(xs.get(0).n1()).isWithin(EPSILON).of(1.0);
    assertThat(xs.get(0).n2()).isWithin(EPSILON).of(1.5);

    assertThat(xs.get(1).t()).isWithin(EPSILON).of(2.75);
    assertThat(xs.get(1).n1()).isWithin(EPSILON).of(1.5);
    assertThat(xs.get(1).n2()).isWithin(EPSILON).of(2.0);

    assertThat(xs.get(2).t()).isWithin(EPSILON).of(3.25);
    assertThat(xs.get(2).n1()).isWithin(EPSILON).of(2.0);
    assertThat(xs.get(2).n2()).isWithin(EPSILON).of(2.5);

    assertThat(xs.get(3).t()).isWithin(EPSILON).of(4.75);
    assertThat(xs.get(3).n1()).isWithin(EPSILON).of(2.0); // ?? 2.5?
    assertThat(xs.get(3).n2()).isWithin(EPSILON).of(2.5);

    assertThat(xs.get(4).t()).isWithin(EPSILON).of(5.25);
    assertThat(xs.get(4).n1()).isWithin(EPSILON).of(2.5);
    assertThat(xs.get(4).n2()).isWithin(EPSILON).of(1.5);

    assertThat(xs.get(5).t()).isWithin(EPSILON).of(6);
    assertThat(xs.get(5).n1()).isWithin(EPSILON).of(1.5);
    assertThat(xs.get(5).n2()).isWithin(EPSILON).of(1.0);
  }

  @Test
  // Scenario: The Schlick approximation under total internal reflection
  public void schlickWithTotalInternalReflection() {
    Shape shape = Sphere.createGlass();
    Ray r = Ray.create(Tuple.createPoint(0, 0, 1 / Math.sqrt(2)), Tuple.createVector(0, 1, 0));
    Intersection i = shape.intersect(r).get(1);
    assertThat(i.t()).isWithin(EPSILON).of(1 / Math.sqrt(2));
    assertThat(i.schlickReflectance()).isWithin(EPSILON).of(1.0);
  }

  @Test
  // Scenario: The Schlick approximation with a perpendicular viewing angle
  public void schlickWithPerpendicularViewingAngle() {
    Shape shape = Sphere.createGlass();
    Ray r = Ray.create(Tuple.createPoint(0, 0, 0), Tuple.createVector(0, 1, 0));
    Intersection i = shape.intersect(r).get(1);
    assertThat(i.t()).isWithin(EPSILON).of(1);
    assertThat(i.schlickReflectance()).isWithin(EPSILON).of(0.04258);
  }

  @Test
  // Scenario: The Schlick approximation with small angle and n2 > n1
  public void schlickWithSmallAngle() {
    Shape shape = Sphere.createGlass();
    Ray r = Ray.create(Tuple.createPoint(0, 0.99, -2), Tuple.createVector(0, 0, 1));
    Intersection i = shape.intersect(r).get(0);
    assertThat(i.t()).isWithin(EPSILON).of(1.85893);
    assertThat(i.schlickReflectance()).isWithin(EPSILON).of(0.49019);
  }
}

/*
Scenario: An intersection can encapsulate `u` and `v`
  Given s ← triangle(point(0, 1, 0), point(-1, 0, 0), point(1, 0, 0))
  When i ← intersection_with_uv(3.5, s, 0.2, 0.4)
  Then i.u = 0.2
    And i.v = 0.4
*/
