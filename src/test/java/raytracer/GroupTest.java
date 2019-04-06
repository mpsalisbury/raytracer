package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static raytracer.MatrixSubject.assertThat;
import static raytracer.TupleSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Groups
public class GroupTest {

  static final double EPSILON = 1.0e-5;

  @Test
  // Scenario: Creating a new group
  public void construct() {
    Group g = new Group();
    assertThat(g.transform()).isApproximatelyEqualTo(Matrix.identity());
    assertThat(g.shapes()).isEmpty();
  }

  @Test
  // Scenario: Adding a child to a group
  public void addChild() {
    Group g = new Group();
    Shape s = Shape.createTest();
    g.add(s);
    assertThat(g.shapes()).contains(s);
  }

  @Test
  // Scenario: Intersecting a ray with an empty group
  public void intersectEmpty() {
    Group g = new Group();
    Ray r = Ray.create(Tuple.createPoint(0, 0, 0), Tuple.createVector(0, 0, 1));
    assertThat(g.intersect(r).all()).isEmpty();
  }

  @Test
  // Scenario: Intersecting a ray with a nonempty group
  public void intersect() {
    Shape s1 = new Sphere();
    Shape s2 = new Sphere();
    s2.setTransform(Matrix.translation(0, 0, -3));
    Shape s3 = new Sphere();
    s3.setTransform(Matrix.translation(5, 0, 0));

    Group g = new Group();
    g.add(s1);
    g.add(s2);
    g.add(s3);

    Ray r = Ray.create(Tuple.createPoint(0, 0, -5), Tuple.createVector(0, 0, 1));
    Intersections xs = g.intersect(r);

    assertThat(xs.length()).isEqualTo(4);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(1);
    assertThat(xs.get(0).shape()).isEqualTo(s2);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(3);
    assertThat(xs.get(1).shape()).isEqualTo(s2);
    assertThat(xs.get(2).t()).isWithin(EPSILON).of(4);
    assertThat(xs.get(2).shape()).isEqualTo(s1);
    assertThat(xs.get(3).t()).isWithin(EPSILON).of(6);
    assertThat(xs.get(3).shape()).isEqualTo(s1);
  }

  @Test
  // Scenario: Intersecting a transformed group
  public void intersectTransformedGroup() {
    Group g = new Group();
    g.setTransform(Matrix.scaling(2, 2, 2));

    Shape s = new Sphere();
    s.setTransform(Matrix.translation(5, 0, 0));
    g.add(s);

    Ray r = Ray.create(Tuple.createPoint(10, 0, -10), Tuple.createVector(0, 0, 1));
    Intersections xs = g.intersect(r);

    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(8);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(12);
  }

  @Test
  // Scenario: Finding the normal on a child object
  public void normalTransformedGroup() {
    Group g = new Group();
    g.setTransform(Matrix.scaling(2, 2, 2));

    Shape s = new Sphere();
    s.setTransform(Matrix.translation(5, 0, 0));
    g.add(s);

    Ray r = Ray.create(Tuple.createPoint(9, 0, -10), Tuple.createVector(0, 0, 1));
    Intersections xs = g.intersect(r);

    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).normalv())
        .isApproximatelyEqualTo(Tuple.createVector(-1, 0, -Math.sqrt(3)).normalize());
  }
}
