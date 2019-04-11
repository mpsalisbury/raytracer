package raytracer;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Cubes
public class CubeTest {

  @Test
  // Scenario Outline: A ray intersects a cube
  public void rayIntersects() {
    assertRayIntersection(5, 0.5, 0, -1, 0, 0, 4, 6); // +x
    assertRayIntersection(-5, 0.5, 0, 1, 0, 0, 4, 6); // -x
    assertRayIntersection(0.5, 5, 0, 0, -1, 0, 4, 6); // +y
    assertRayIntersection(0.5, -5, 0, 0, 1, 0, 4, 6); // -y
    assertRayIntersection(0.5, 0, 5, 0, 0, -1, 4, 6); // +z
    assertRayIntersection(0.5, 0, -5, 0, 0, 1, 4, 6); // -z
    assertRayIntersection(0, 0.5, 0, 0, 0, 1, -1, 1); // inside
  }

  // Asserts that ray(px,py,pz, vx,vy,vz) hits default cube at t's t1, t2.
  private void assertRayIntersection(
      double px, double py, double pz, double vx, double vy, double vz, double t1, double t2) {
    Shape c = Cube.create();
    Ray r = Ray.create(Tuple.createPoint(px, py, pz), Tuple.createVector(vx, vy, vz));
    Intersections xs = c.intersect(r);
    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isEqualTo(t1);
    assertThat(xs.get(1).t()).isEqualTo(t2);
  }

  @Test
  // Scenario Outline: A ray misses a cube
  public void rayMisses() {
    assertRayMisses(-2, 0, 0, 0.2673, 0.5345, 0.8018);
    assertRayMisses(0, -2, 0, 0.8018, 0.2673, 0.5345);
    assertRayMisses(0, 0, -2, 0.5345, 0.8018, 0.2673);
    assertRayMisses(2, 0, 2, 0, 0, -1);
    assertRayMisses(0, 2, 2, 0, -1, 0);
    assertRayMisses(2, 2, 0, -1, 0, 0);
  }

  // Asserts that ray(px,py,pz, vx,vy,vz) misses default cube.
  private void assertRayMisses(double px, double py, double pz, double vx, double vy, double vz) {
    Shape c = Cube.create();
    Ray r = Ray.create(Tuple.createPoint(px, py, pz), Tuple.createVector(vx, vy, vz));
    Intersections xs = c.intersect(r);
    assertThat(xs.length()).isEqualTo(0);
  }

  @Test
  // Scenario Outline: The normal on the surface of a cube
  public void pointNormal() {
    assertPointNormal(1, 0.5, -0.8, 1, 0, 0);
    assertPointNormal(-1, -0.2, 0.9, -1, 0, 0);
    assertPointNormal(-0.4, 1, -0.1, 0, 1, 0);
    assertPointNormal(0.3, -1, -0.7, 0, -1, 0);
    assertPointNormal(-0.6, 0.3, 1, 0, 0, 1);
    assertPointNormal(0.4, 0.4, -1, 0, 0, -1);
    assertPointNormal(1, 1, 1, 1, 0, 0);
    assertPointNormal(-1, -1, -1, -1, 0, 0);
  }

  // Asserts that the normal of the default cube at point px,py,pz is nx,ny,nz.
  private void assertPointNormal(double px, double py, double pz, double nx, double ny, double nz) {
    Geometry c = new Cube();
    assertThat(c.normalAt(Tuple.createPoint(px, py, pz))).isEqualTo(Tuple.createVector(nx, ny, nz));
  }
}
