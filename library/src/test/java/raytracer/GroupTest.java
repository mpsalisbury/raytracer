package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static raytracer.MatrixSubject.assertThat;
import static raytracer.Range3Subject.assertThat;
import static raytracer.Testing.EPSILON;
import static raytracer.TupleSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Groups
public class GroupTest {

  @Test
  // Scenario: Creating a new group
  public void construct() {
    Group g = Group.create();
    assertThat(g.transform()).isApproximatelyEqualTo(Matrix.identity());
    assertThat(g.shapes()).isEmpty();
  }

  @Test
  // Scenario: Adding a child to a group
  public void addChild() {
    Group g = Group.create();
    Shape s = Sphere.create();
    g.add(s);
    assertThat(g.shapes()).contains(s);
  }

  @Test
  // Scenario: Appropriate bounding box coverage.
  public void boundingBox() {
    Group g = Group.create();
    Shape s = Sphere.create();
    g.add(s);
    g.add(s); // BoundingBox only kicks in after 5 objects within group.
    g.add(s);
    g.add(s);
    g.add(s);
    assertThat(g.boundingBox().getRange())
        .isApproximatelyEqualTo(Range3.create(-1, 1, -1, 1, -1, 1));

    s.setTransform(Matrix.translation(5, 0, 0));
    g.resetBoundingBox();
    assertThat(g.boundingBox().getRange())
        .isApproximatelyEqualTo(Range3.create(4, 6, -1, 1, -1, 1));

    g.setTransform(Matrix.scaling(2, 2, 2));
    assertThat(g.boundingBox().getRange())
        .isApproximatelyEqualTo(Range3.create(4, 6, -1, 1, -1, 1));
  }

  @Test
  // Scenario: Intersecting a ray with an empty group
  public void intersectEmpty() {
    Group g = Group.create();
    Ray r = Ray.create(Tuple.point(0, 0, 0), Tuple.vector(0, 0, 1));
    assertThat(g.intersect(r).all()).isEmpty();
  }

  @Test
  // Scenario: Intersecting a ray with a nonempty group
  public void intersect() {
    Shape s1 = Sphere.create();
    Shape s2 = Sphere.create();
    s2.setTransform(Matrix.translation(0, 0, -3));
    Shape s3 = Sphere.create();
    s3.setTransform(Matrix.translation(5, 0, 0));

    Group g = Group.create();
    g.add(s1);
    g.add(s2);
    g.add(s3);

    Ray r = Ray.create(Tuple.point(0, 0, -5), Tuple.vector(0, 0, 1));
    Intersections xs = g.intersect(r);

    assertThat(xs.length()).isEqualTo(4);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(1);
    //  assertThat(xs.get(0).shape()).isEqualTo(s2);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(3);
    //  assertThat(xs.get(1).shape()).isEqualTo(s2);
    assertThat(xs.get(2).t()).isWithin(EPSILON).of(4);
    //  assertThat(xs.get(2).shape()).isEqualTo(s1);
    assertThat(xs.get(3).t()).isWithin(EPSILON).of(6);
    //  assertThat(xs.get(3).shape()).isEqualTo(s1);
  }

  @Test
  // Scenario: Intersecting a transformed group
  public void intersectTransformedGroup() {
    Group g = Group.create();
    g.setTransform(Matrix.scaling(2, 2, 2));

    Shape s = Sphere.create();
    s.setTransform(Matrix.translation(5, 0, 0));
    g.add(s);

    Ray r = Ray.create(Tuple.point(10, 0, -10), Tuple.vector(0, 0, 1));
    Intersections xs = g.intersect(r);

    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(8);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(12);
  }

  @Test
  // Scenario: Finding the normal on a child object
  public void normalTransformedGroup() {
    Group g = Group.create();
    g.setTransform(Matrix.scaling(2, 2, 2));

    Shape s = Sphere.create();
    s.setTransform(Matrix.translation(5, 0, 0));
    g.add(s);

    Ray r = Ray.create(Tuple.point(9, 0, -10), Tuple.vector(0, 0, 1));
    Intersections xs = g.intersect(r);

    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).normalv())
        .isApproximatelyEqualTo(Tuple.vector(-1, 0, -Math.sqrt(3)).normalize());
  }
}
