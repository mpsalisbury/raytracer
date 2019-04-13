package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.ColorSubject.assertThat;
import static raytracer.Testing.EPSILON;
import static raytracer.TupleSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Camera
public class CameraTest {

  @Test
  // Scenario: Constructing a camera
  public void construct() {
    Camera c = Camera.create(160, 120, Math.PI / 2);
    assertThat(c.hPixels()).isEqualTo(160);
    assertThat(c.vPixels()).isEqualTo(120);
    assertThat(c.fieldOfView()).isWithin(EPSILON).of(Math.PI / 2);
    assertThat(c.transform()).isEqualTo(Matrix.identity());
  }

  @Test
  public void filmPointThroughCenter() {
    Camera c = Camera.create(201, 101, Math.PI / 2);
    Tuple p = c.filmPointForPixel(100, 50);
    assertThat(p).isApproximatelyEqualTo(Tuple.point(0, 0, -1));
  }

  @Test
  public void filmPointThroughCorner() {
    Camera c = Camera.create(201, 101, Math.PI / 2);
    Tuple p = c.filmPointForPixel(0, 0);
    assertThat(p).isApproximatelyEqualTo(Tuple.point(-1, 0.5, -1));
  }

  @Test
  // Scenario: Constructing a ray through the center of the canvas
  public void rayThroughCenter() {
    Camera c = Camera.create(201, 101, Math.PI / 2);
    Ray r = c.rayForPixel(100, 50);
    assertThat(r.origin()).isApproximatelyEqualTo(Tuple.point(0, 0, 0));
    assertThat(r.direction()).isApproximatelyEqualTo(Tuple.vector(0, 0, -1));
  }

  @Test
  // Scenario: Constructing a ray through a corner of the canvas
  public void rayThroughCorner() {
    Camera c = Camera.create(201, 101, Math.PI / 2);
    Ray r = c.rayForPixel(0, 0);
    assertThat(r.origin()).isApproximatelyEqualTo(Tuple.point(0, 0, 0));
    assertThat(r.direction())
        .isApproximatelyEqualTo(Tuple.vector(-0.66666, 0.33333, -0.66666));
  }

  @Test
  // Scenario: Constructing a ray when the camera is transformed
  public void rayWithTransformedCamera() {
    Matrix transform = Matrix.translation(0, -2, 5).rotateY(Math.PI / 4);
    Camera c = Camera.create(201, 101, Math.PI / 2, transform);
    Ray r = c.rayForPixel(100, 50);
    assertThat(r.origin()).isApproximatelyEqualTo(Tuple.point(0, 2, -5));
    assertThat(r.direction())
        .isApproximatelyEqualTo(Tuple.vector(1 / Math.sqrt(2), 0, -1 / Math.sqrt(2)));
  }

  @Test
  // Scenario: Rendering a world with a camera
  public void renderWorldPixel() {
    World w = WorldTest.createDefaultWorld();
    Tuple from = Tuple.point(0, 0, -5);
    Tuple to = Tuple.point(0, 0, 0);
    Tuple up = Tuple.vector(0, 1, 0);
    Camera camera = Camera.create(11, 11, Math.PI / 2, from, to, up);
    Canvas image = camera.render(w);
    assertThat(image.pixel(5, 5)).isApproximatelyEqualTo(Color.create(0.38066, 0.47583, 0.2855));
  }
}
