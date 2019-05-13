package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.MatrixSubject.assertThat;
import static raytracer.TupleSubject.assertThat;

import java.util.stream.DoubleStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: AbstractShapes
public class ShapeTest {

  private Shape createTestShape() {
    return new GeometryShape(new TestGeometry());
  }

  private static class TestGeometry extends Geometry {
    @Override
    public Range3 getRange() {
      return Range3.createEmpty();
    }

    @Override
    public DoubleStream intersect(Ray localRay) {
      return DoubleStream.empty();
    }

    @Override
    public Tuple normalAt(Tuple point) {
      return Tuple.vector(point.x(), point.y(), point.z());
    }
  }

  @Test
  // Scenario: The default transformation
  public void defaultTransform() {
    Shape s = createTestShape();
    assertThat(s.transform()).isApproximatelyEqualTo(Matrix.identity());
  }

  @Test
  // Scenario: Assigning a transformation
  public void assignTransform() {
    Shape s = createTestShape();
    s.setTransform(Matrix.translation(2, 3, 4));
    assertThat(s.transform()).isApproximatelyEqualTo(Matrix.translation(2, 3, 4));
  }

  @Test
  // Scenario: The default material
  public void defaultMaterial() {
    Shape s = createTestShape();
    assertThat(s.material()).isEqualTo(Material.create());
  }

  @Test
  // Scenario: Assigning a material
  public void assignMaterial() {
    Shape s = createTestShape();
    Material m = Material.builder().setAmbient(1).build();
    s.setMaterial(m);
    assertThat(s.material()).isEqualTo(m);
  }
}

/*
Scenario: Intersecting a scaled shape with a ray
  Given r ← ray(point(0, 0, -5), vector(0, 0, 1))
    And s ← test_shape()
  When set_transform(s, scaling(2, 2, 2))
    And xs ← intersect(s, r)
  Then s.saved_ray.origin = point(0, 0, -2.5)
    And s.saved_ray.direction = vector(0, 0, 0.5)

Scenario: Intersecting a translated shape with a ray
  Given r ← ray(point(0, 0, -5), vector(0, 0, 1))
    And s ← test_shape()
  When set_transform(s, translation(5, 0, 0))
    And xs ← intersect(s, r)
  Then s.saved_ray.origin = point(-5, 0, -5)
    And s.saved_ray.direction = vector(0, 0, 1)

Scenario: Computing the normal on a translated shape
  Given s ← test_shape()
  When set_transform(s, translation(0, 1, 0))
    And n ← normal_at(s, point(0, 1.70711, -0.70711))
  Then n = vector(0, 0.70711, -0.70711)

Scenario: Computing the normal on a transformed shape
  Given s ← test_shape()
    And m ← scaling(1, 0.5, 1) * rotation_z(π/5)
  When set_transform(s, m)
    And n ← normal_at(s, point(0, √2/2, -√2/2))
  Then n = vector(0, 0.97014, -0.24254)

Scenario: A shape has a parent attribute
  Given s ← test_shape()
  Then s.parent is nothing

Scenario: Converting a point from world to object space
  Given g1 ← group()
    And set_transform(g1, rotation_y(π/2))
    And g2 ← group()
    And set_transform(g2, scaling(2, 2, 2))
    And add_child(g1, g2)
    And s ← sphere()
    And set_transform(s, translation(5, 0, 0))
    And add_child(g2, s)
  When p ← world_to_object(s, point(-2, 0, -10))
  Then p = point(0, 0, -1)


*/
