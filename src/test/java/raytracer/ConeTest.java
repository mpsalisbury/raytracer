package raytracer;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Cones
public class ConeTest {

  private static final double EPSILON = 1.0e-5;

  @Test
  // Scenario Outline: A ray misses a cone
  public void rayMisses() {
    // horizontal between
    assertRayMisses(1, 1, 0, 0, 0, 1);
    // diagonal between
    assertRayMisses(1, 1, 0, 0, 0.1, 1);
    // horizontal above and below
    assertRayMisses(0, 1.5, 0, 0, 0, 1);
    assertRayMisses(0, -1.5, 0, 0, 0, 1);
    // diagonal above and below
    assertRayMisses(0, 1.5, 0, 0, 0.1, 1);
    assertRayMisses(0, -1.5, 0, 0, 0.1, 1);
  }

  // Asserts that ray(px,py,pz, vx,vy,vz) misses default cone.
  private void assertRayMisses(double px, double py, double pz, double vx, double vy, double vz) {
    assertRayIntersection(px, py, pz, vx, vy, vz);
  }

  // Asserts that ray(px,py,pz, vx,vy,vz) hits default cone at t's.
  private void assertRayIntersection(
      double px, double py, double pz, double vx, double vy, double vz, double... ts) {
    Shape c = Cone.create();
    Ray r = Ray.create(Tuple.createPoint(px, py, pz), Tuple.createVector(vx, vy, vz));
    Intersections xs = c.intersect(r);
    assertThat(xs.length()).isEqualTo(ts.length);
    for (int i = 0; i < xs.length(); ++i) {
      assertThat(xs.get(i).t()).isWithin(EPSILON).of(ts[i]);
    }
  }

  @Test
  // Scenario Outline: A ray strikes a cone
  public void rayIntersects() {
    // horizontal
    assertRayIntersection(0, 0, -5, 0, 0, 1, 4, 6);
    assertRayIntersection(-5, 0, 0, 1, 0, 0, 4, 6);
    assertRayIntersection(0.5, 0, -5, 0.1, 0, 1, 4.80198, 5);
    // tangent
    assertRayIntersection(1, 0, -5, 0, 0, 1, 5, 5);
    // vertical
    assertRayIntersection(0.5, 0, 0, 0, 1, 0, -1, 0.5);
    // diagonal through two walls
    assertRayIntersection(0, 0, 0, 1, 0.1, 0, -1.11111, 0.90909);
    // vertical through cap and wall
    assertRayIntersection(0.9, -0.9, 0, 0, 1, 0, -0.1, 1);
  }

  @Test
  // Scenario Outline: Normal vector on a cone
  public void normal() {
    assertNormal(1, 0, 0, 1 / Math.sqrt(2), 1 / Math.sqrt(2), 0);
    assertNormal(0, 0, 1, 0, 1 / Math.sqrt(2), 1 / Math.sqrt(2));
    assertNormal(-0.3, -1, 0.3, 0, -1, 0);
  }

  // Asserts that the normal at point (px,py,pz) is vector(nx,ny,nz).
  private void assertNormal(double px, double py, double pz, double nx, double ny, double nz) {
    Shape c = Cone.create();
    assertThat(c.normalAt(Tuple.createPoint(px, py, pz))).isEqualTo(Tuple.createVector(nx, ny, nz));
  }
}
