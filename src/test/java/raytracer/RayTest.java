package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.ColorSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Rays
public class RayTest {

  @Test
  // Scenario: Creating and querying a ray
  public void constructRay() {
    Tuple origin = Tuple.createPoint(1, 2, 3);
    Tuple direction = Tuple.createVector(4, 5, 6);
    Ray r = Ray.create(origin, direction);
    assertThat(r.origin()).isEqualTo(origin);
    assertThat(r.direction()).isEqualTo(direction);
  }

  @Test
  // Scenario: Computing a point from a distance
  public void pointFromDistance() {
    Ray r = Ray.create(Tuple.createPoint(2, 3, 4), Tuple.createVector(1, 0, 0));
    assertThat(r.position(0)).isEqualTo(Tuple.createPoint(2, 3, 4));
    assertThat(r.position(1)).isEqualTo(Tuple.createPoint(3, 3, 4));
    assertThat(r.position(-1)).isEqualTo(Tuple.createPoint(1, 3, 4));
    assertThat(r.position(2.5)).isEqualTo(Tuple.createPoint(4.5, 3, 4));
  }

  @Test
  // Scenario: Translating a ray
  public void translate() {
    Ray r = Ray.create(Tuple.createPoint(1, 2, 3), Tuple.createVector(0, 1, 0));
    Matrix m = Matrix.translation(3, 4, 5);
    Ray r2 = r.transform(m);
    assertThat(r2.origin()).isEqualTo(Tuple.createPoint(4, 6, 8));
    assertThat(r2.direction()).isEqualTo(Tuple.createVector(0, 1, 0));
  }

  @Test
  // Scenario: Scaling a ray
  public void scale() {
    Ray r = Ray.create(Tuple.createPoint(1, 2, 3), Tuple.createVector(0, 1, 0));
    Matrix m = Matrix.scaling(2, 3, 4);
    Ray r2 = r.transform(m);
    assertThat(r2.origin()).isEqualTo(Tuple.createPoint(2, 6, 12));
    assertThat(r2.direction()).isEqualTo(Tuple.createVector(0, 3, 0));
  }
}
