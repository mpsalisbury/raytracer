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
    Tuple origin = Tuple.point(1, 2, 3);
    Tuple direction = Tuple.vector(4, 5, 6);
    Ray r = Ray.create(origin, direction);
    assertThat(r.origin()).isEqualTo(origin);
    assertThat(r.direction()).isEqualTo(direction);
  }

  @Test
  // Scenario: Computing a point from a distance
  public void pointFromDistance() {
    Ray r = Ray.create(Tuple.point(2, 3, 4), Tuple.vector(1, 0, 0));
    assertThat(r.position(0)).isEqualTo(Tuple.point(2, 3, 4));
    assertThat(r.position(1)).isEqualTo(Tuple.point(3, 3, 4));
    assertThat(r.position(-1)).isEqualTo(Tuple.point(1, 3, 4));
    assertThat(r.position(2.5)).isEqualTo(Tuple.point(4.5, 3, 4));
  }

  @Test
  // Scenario: Translating a ray
  public void translate() {
    Ray r = Ray.create(Tuple.point(1, 2, 3), Tuple.vector(0, 1, 0));
    Matrix m = Matrix.translation(3, 4, 5);
    Ray r2 = r.transform(m);
    assertThat(r2.origin()).isEqualTo(Tuple.point(4, 6, 8));
    assertThat(r2.direction()).isEqualTo(Tuple.vector(0, 1, 0));
  }

  @Test
  // Scenario: Scaling a ray
  public void scale() {
    Ray r = Ray.create(Tuple.point(1, 2, 3), Tuple.vector(0, 1, 0));
    Matrix m = Matrix.scaling(2, 3, 4);
    Ray r2 = r.transform(m);
    assertThat(r2.origin()).isEqualTo(Tuple.point(2, 6, 12));
    assertThat(r2.direction()).isEqualTo(Tuple.vector(0, 3, 0));
  }
}
