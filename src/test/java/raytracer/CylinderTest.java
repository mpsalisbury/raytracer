package raytracer;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Cylinders
public class CylinderTest {

  private static final double EPSILON = 1.0e-5;

  @Test
  // Scenario Outline: A ray misses a cylinder
  public void rayMisses() {
    // vertical alongside
    assertRayMisses(1, 0, 0, 0, 1, 0);
    // horizontal above and below
    assertRayMisses(0, 1.5, 0, 0, 0, 1);
    assertRayMisses(0, -1.5, 0, 0, 0, 1);
    // horizontal to the side
    assertRayMisses(1.1, 0, 1.1, 0, 0, 1);
    assertRayMisses(1.1, 0, 1.1, 1, 0, 0);
    // diagonal above and below
    assertRayMisses(2, 0, 0, 1, 1, 0);
    assertRayMisses(2, 0, 0, 1, -1, 0);
    // diagonal alongside
    assertRayMisses(0, 0, -5, 1, 1, 1);
  }

  // Asserts that ray(px,py,pz, vx,vy,vz) misses default cylinder.
  private void assertRayMisses(double px, double py, double pz, double vx, double vy, double vz) {
    Shape c = new Cylinder();
    Ray r = Ray.create(Tuple.createPoint(px, py, pz), Tuple.createVector(vx, vy, vz));
    Intersections xs = c.intersect(r);
    assertThat(xs.length()).isEqualTo(0);
  }

  @Test
  // Scenario Outline: A ray strikes a cylinder
  public void rayIntersects() {
    // horizontal
    assertRayIntersection(0, 0, -5, 0, 0, 1, 4, 6);
    assertRayIntersection(-5, 0, 0, 1, 0, 0, 4, 6);
    assertRayIntersection(0.5, 0, -5, 0.1, 0, 1, 4.80198, 5);
    // tangent
    assertRayIntersection(1, 0, -5, 0, 0, 1, 5, 5);
    // vertical
    assertRayIntersection(0, 0, 0, 0, 1, 0, -1, 1);
    // diagonal through two walls
    assertRayIntersection(0, 0, 0, 1, 0.1, 0, -1, 1);
    // diagonal through two caps
    assertRayIntersection(0, 0, 0, 0.1, 1, 0, -1, 1);
    // diagonal through bottom cap and wall
    assertRayIntersection(0.9, -0.9, 0, 1, 1, 0, -0.1, 0.1);
    // diagonal through wall and top cap
    assertRayIntersection(-0.9, 0.9, 0, 1, 1, 0, -0.1, 0.1);
  }

  // Asserts that ray(px,py,pz, vx,vy,vz) hits default cylinder at t's t1, t2.
  private void assertRayIntersection(
      double px, double py, double pz, double vx, double vy, double vz, double t1, double t2) {
    Shape c = new Cylinder();
    Ray r = Ray.create(Tuple.createPoint(px, py, pz), Tuple.createVector(vx, vy, vz));
    Intersections xs = c.intersect(r);
    assertThat(xs.length()).isEqualTo(2);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(t1);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(t2);
  }

  @Test
  // Scenario Outline: Normal vector on a cylinder
  public void normal() {
    assertNormal(1, 0, 0, 1, 0, 0);
    assertNormal(0, 0.5, -1, 0, 0, -1);
    assertNormal(0, -0.5, 1, 0, 0, 1);
    assertNormal(-1, 0.9, 0, -1, 0, 0);
    assertNormal(-0.3, 1, 0.3, 0, 1, 0);
    assertNormal(-0.3, -1, 0.3, 0, -1, 0);
  }

  // Asserts that the normal at point (px,py,pz) is vector(nx,ny,nz).
  private void assertNormal(double px, double py, double pz, double nx, double ny, double nz) {
    Shape c = new Cylinder();
    assertThat(c.normalAt(Tuple.createPoint(px, py, pz))).isEqualTo(Tuple.createVector(nx, ny, nz));
  }
}
