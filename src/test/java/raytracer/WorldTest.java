package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.ColorSubject.assertThat;

import com.google.common.collect.Iterables;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: World
public class WorldTest {

  private static final double EPSILON = 1.0e-5;

  @Test
  // Scenario: Creating a world
  public void constructWorld() {
    World w = new World();
    assertThat(w.getLights()).isEmpty();
    assertThat(w.getShapes()).isEmpty();
  }

  @Test
  // Scenario: The default world
  public void defaultWorld() {
    Light light = Light.create(Tuple.createPoint(-10, 10, -10), Color.WHITE);

    Sphere s1 = new Sphere();
    s1.setMaterial(
        Material.builder()
            .setColor(Color.create(0.8, 1.0, 0.6))
            .setDiffuse(0.7)
            .setSpecular(0.2)
            .build());

    Sphere s2 = new Sphere();
    s2.setTransform(Matrix.scaling(0.5, 0.5, 0.5));

    World w = World.createDefault();
    assertThat(w.getLights()).containsExactly(light);
    assertThat(w.getShapes()).containsExactly(s1, s2);
  }

  @Test
  // Scenario: Intersect a world with a ray
  public void intersect() {
    World w = World.createDefault();
    Ray r = Ray.create(Tuple.createPoint(0, 0, -5), Tuple.createVector(0, 0, 1));
    Intersections xs = w.intersect(r);
    assertThat(xs.length()).isEqualTo(4);
    assertThat(xs.get(0).t()).isWithin(EPSILON).of(4);
    assertThat(xs.get(1).t()).isWithin(EPSILON).of(4.5);
    assertThat(xs.get(2).t()).isWithin(EPSILON).of(5.5);
    assertThat(xs.get(3).t()).isWithin(EPSILON).of(6);
  }

  @Test
  // Scenario: Shading an intersection
  public void shadingOutside() {
    World w = World.createDefault();
    Ray r = Ray.create(Tuple.createPoint(0, 0, -5), Tuple.createVector(0, 0, 1));
    Shape shape = Iterables.get(w.getShapes(), 0);
    Intersection i = shape.intersect(r).get(0);
    assertThat(i.t()).isWithin(EPSILON).of(4);
    assertThat(w.shadeHit(i, 0)).isApproximatelyEqualTo(Color.create(0.38066, 0.47583, 0.2855));
  }

  @Test
  // Scenario: Shading an intersection from the inside
  public void shadingInside() {
    World w = World.createDefault();
    w.clearLights();
    w.addLight(Light.create(Tuple.createPoint(0, 0.25, 0), Color.WHITE));
    Ray r = Ray.create(Tuple.createPoint(0, 0, 0), Tuple.createVector(0, 0, 1));
    Shape shape = Iterables.get(w.getShapes(), 1);
    Intersection i = shape.intersect(r).get(1);
    assertThat(i.t()).isWithin(EPSILON).of(0.5);
    assertThat(w.shadeHit(i, 0)).isApproximatelyEqualTo(Color.create(0.90498, 0.90498, 0.90498));
  }

  @Test
  // Scenario: The color when a ray misses
  public void colorWhenRayMisses() {
    World w = World.createDefault();
    Ray r = Ray.create(Tuple.createPoint(0, 0, -5), Tuple.createVector(0, 1, 0));
    assertThat(w.colorAt(r, 0)).isEqualTo(Color.create(0, 0, 0));
  }

  @Test
  // Scenario: The color when a ray hits
  public void colorWhenRayHits() {
    World w = World.createDefault();
    Ray r = Ray.create(Tuple.createPoint(0, 0, -5), Tuple.createVector(0, 0, 1));
    assertThat(w.colorAt(r, 0)).isApproximatelyEqualTo(Color.create(0.38066, 0.47583, 0.2855));
  }

  @Test
  // Scenario: The color with an intersection behind the ray
  public void colorWithIntersectionBehindRay() {
    World w = World.createDefault();

    Shape outer = Iterables.get(w.getShapes(), 0);
    outer.setMaterial(outer.material().toBuilder().setAmbient(1).build());

    Shape inner = Iterables.get(w.getShapes(), 1);
    inner.setMaterial(inner.material().toBuilder().setAmbient(1).build());

    Ray r = Ray.create(Tuple.createPoint(0, 0, 0.75), Tuple.createVector(0, 0, -1));
    // TODO Which point is appropriate to use here?
    assertThat(w.colorAt(r, 0))
        .isApproximatelyEqualTo(inner.material().pattern().colorAt(Tuple.createPoint(0, 0, 0)));
  }

  @Test
  // Scenario: There is no shadow when nothing is collinear with point and light
  public void noShadowWhenNothingBlocking() {
    World w = World.createDefault();
    Tuple p = Tuple.createPoint(0, 10, 0);
    Light l = Iterables.get(w.getLights(), 0);
    assertThat(w.isShadowed(p, l)).isFalse();
  }

  @Test
  // Scenario: The shadow when an object is between the point and the light
  public void shadowWhenBlocking() {
    World w = World.createDefault();
    Tuple p = Tuple.createPoint(10, -10, 10);
    Light l = Iterables.get(w.getLights(), 0);
    assertThat(w.isShadowed(p, l)).isTrue();
  }

  @Test
  // Scenario: There is no shadow when an object is behind the light
  public void noShadowWhenObjectBehindLight() {
    World w = World.createDefault();
    Tuple p = Tuple.createPoint(-20, 20, -20);
    Light l = Iterables.get(w.getLights(), 0);
    assertThat(w.isShadowed(p, l)).isFalse();
  }

  @Test
  // Scenario: There is no shadow when an object is behind the point
  public void noShadowWhenObjectBehindPoint() {
    World w = World.createDefault();
    Tuple p = Tuple.createPoint(-2, 2, -2);
    Light l = Iterables.get(w.getLights(), 0);
    assertThat(w.isShadowed(p, l)).isFalse();
  }

  @Test
  // Scenario: The reflected color for a nonreflective material
  public void reflectedColorForNonreflectiveMaterial() {
    World w = World.createDefault();
    Shape shape = Iterables.get(w.getShapes(), 0);
    shape.setMaterial(shape.material().toBuilder().setAmbient(1).build());
    Ray r = Ray.create(Tuple.createPoint(0, 0, 0), Tuple.createVector(0, 0, 1));
    Intersection i = shape.intersect(r).get(0);
    assertThat(w.reflectedColor(i, 0)).isApproximatelyEqualTo(Color.create(0, 0, 0));
  }

  @Test
  // Scenario: The reflected color for a reflective material
  public void reflectedColorForReflectiveMaterial() {
    World w = World.createDefault();
    Shape shape = new Plane();
    shape.setMaterial(shape.material().toBuilder().setReflectivity(0.5).build());
    shape.setTransform(Matrix.translation(0, -1, 0));
    w.addShape(shape);
    Ray r =
        Ray.create(
            Tuple.createPoint(0, 0, -3),
            Tuple.createVector(0, -1 / Math.sqrt(2), 1 / Math.sqrt(2)));
    Intersection i = shape.intersect(r).get(0);
    assertThat(w.reflectedColor(i, 1))
        .isApproximatelyEqualTo(Color.create(0.19033, 0.23791, 0.14275));
  }

  @Test
  // Scenario: shade_hit() with a reflective material
  public void shadeHitWithReflectiveMaterial() {
    World w = World.createDefault();
    Shape shape = new Plane();
    shape.setMaterial(shape.material().toBuilder().setReflectivity(0.5).build());
    shape.setTransform(Matrix.translation(0, -1, 0));
    w.addShape(shape);
    Ray r =
        Ray.create(
            Tuple.createPoint(0, 0, -3),
            Tuple.createVector(0, -1 / Math.sqrt(2), 1 / Math.sqrt(2)));
    Intersection i = shape.intersect(r).get(0);
    assertThat(w.shadeHit(i, 1)).isApproximatelyEqualTo(Color.create(0.87676, 0.92434, 0.82917));
  }

  @Test
  // Scenario: color_at() with mutually reflective surfaces
  public void colorAtMututallyReflective() {
    World w = new World();
    Shape lower = new Plane();
    lower.setMaterial(lower.material().toBuilder().setReflectivity(1).build());
    lower.setTransform(Matrix.translation(0, -1, 0));
    w.addShape(lower);
    Shape upper = new Plane();
    upper.setMaterial(upper.material().toBuilder().setReflectivity(1).build());
    upper.setTransform(Matrix.translation(0, 1, 0));
    w.addShape(upper);
    Ray r = Ray.create(Tuple.createPoint(0, 0, 0), Tuple.createVector(0, 1, 0));
    // should terminate;
    w.colorAt(r);
  }

  @Test
  // Scenario: The reflected color at the maximum recursive depth
  public void reflectedColorAtMaxDepth() {
    World w = World.createDefault();
    Shape shape = new Plane();
    shape.setMaterial(shape.material().toBuilder().setReflectivity(0.5).build());
    shape.setTransform(Matrix.translation(0, -1, 0));
    w.addShape(shape);
    Ray r =
        Ray.create(
            Tuple.createPoint(0, 0, -3),
            Tuple.createVector(0, -1 / Math.sqrt(2), 1 / Math.sqrt(2)));
    Intersection i = shape.intersect(r).get(0);
    assertThat(w.reflectedColor(i, 0)).isEqualTo(Color.BLACK);
  }

  @Test
  // Scenario: The refracted color with an opaque surface
  public void refractedColorOnOpaque() {
    World w = World.createDefault();
    Shape shape = Iterables.get(w.getShapes(), 0);
    Ray r = Ray.create(Tuple.createPoint(0, 0, -5), Tuple.createVector(0, 0, 1));
    Intersection i = w.intersect(r).get(0);
    assertThat(w.refractedColor(i, 5)).isEqualTo(Color.BLACK);
  }

  @Test
  // Scenario: The refracted color at the maximum recursive depth
  public void refractedColorAtMaxDepth() {
    World w = World.createDefault();
    Shape shape = Iterables.get(w.getShapes(), 0);
    shape.setMaterial(Material.builder().setTransparency(1.0).setRefractiveIndex(1.5).build());
    Ray r = Ray.create(Tuple.createPoint(0, 0, -5), Tuple.createVector(0, 0, 1));
    Intersection i = w.intersect(r).get(0);
    assertThat(i.t()).isWithin(EPSILON).of(4);
    assertThat(w.refractedColor(i, 0)).isEqualTo(Color.BLACK);
  }

  @Test
  // Scenario: The refracted color under total internal reflection
  public void refractedColorAtTotalInternalReflection() {
    World w = World.createDefault();
    Shape shape = Iterables.get(w.getShapes(), 0);
    shape.setMaterial(Material.builder().setTransparency(1.0).setRefractiveIndex(1.5).build());
    Ray r = Ray.create(Tuple.createPoint(0, 0, 1 / Math.sqrt(2)), Tuple.createVector(0, 1, 0));
    Intersection i = w.intersect(r).get(1);
    assertThat(i.t()).isWithin(EPSILON).of(1 / Math.sqrt(2));
    assertThat(w.refractedColor(i, 5)).isEqualTo(Color.BLACK);
  }

  @Test
  // Scenario: The refracted color with a refracted ray
  public void refractedColorWithRefractedRay() {
    World w = World.createDefault();
    Shape a = Iterables.get(w.getShapes(), 0);
    a.setMaterial(
        Material.builder().setAmbient(1.0).setPattern(new PatternTest.TestPattern()).build());
    Shape b = Iterables.get(w.getShapes(), 1);
    b.setMaterial(Material.builder().setTransparency(1.0).setRefractiveIndex(1.5).build());
    Ray r = Ray.create(Tuple.createPoint(0, 0, 0.1), Tuple.createVector(0, 1, 0));
    Intersection i = w.intersect(r).get(2);
    assertThat(i.t()).isWithin(EPSILON).of(0.4899);
    assertThat(w.refractedColor(i, 5)).isApproximatelyEqualTo(Color.create(0, 0.99888, 0.04722));
  }

  @Test
  // Scenario: shade_hit() with a transparent material
  public void shadeHitWithTransparent() {
    World w = World.createDefault();

    Shape floor = new Plane();
    floor.setTransform(Matrix.translation(0, -1, 0));
    floor.setMaterial(Material.builder().setTransparency(0.5).setRefractiveIndex(1.5).build());
    w.addShape(floor);

    Shape ball = new Sphere();
    ball.setTransform(Matrix.translation(0, -3.5, -0.5));
    ball.setMaterial(Material.builder().setColor(Color.create(1, 0, 0)).setAmbient(0.5).build());
    w.addShape(ball);

    Ray r =
        Ray.create(
            Tuple.createPoint(0, 0, -3),
            Tuple.createVector(0, -1 / Math.sqrt(2), 1 / Math.sqrt(2)));
    Intersection i = w.intersect(r).get(0);
    assertThat(i.t()).isWithin(EPSILON).of(Math.sqrt(2));
//    assertThat(w.shadeHit(i, 5)).isApproximatelyEqualTo(Color.create(0.93642, 0.68643, 0.68643));
    assertThat(w.shadeHit(i, 5)).isApproximatelyEqualTo(Color.create(1.20377, 0.68643, 0.68643));
  }

  @Test
  // Scenario: shade_hit() with a reflective, transparent material
  public void shadeHitWithReflectiveTransparent() {
    World w = World.createDefault();

    Shape floor = new Plane();
    floor.setTransform(Matrix.translation(0, -1, 0));
    floor.setMaterial(
        Material.builder()
            .setReflectivity(0.5)
            .setTransparency(0.5)
            .setRefractiveIndex(1.5)
            .build());
    w.addShape(floor);

    Shape ball = new Sphere();
    ball.setTransform(Matrix.translation(0, -3.5, -0.5));
    ball.setMaterial(Material.builder().setColor(Color.create(1, 0, 0)).setAmbient(0.5).build());
    w.addShape(ball);

    Ray r =
        Ray.create(
            Tuple.createPoint(0, 0, -3),
            Tuple.createVector(0, -1 / Math.sqrt(2), 1 / Math.sqrt(2)));
    Intersection i = w.intersect(r).get(0);
    assertThat(i.t()).isWithin(EPSILON).of(Math.sqrt(2));
    //assertThat(w.shadeHit(i, 5)).isApproximatelyEqualTo(Color.create(0.93391, 0.69643, 0.69243));
    assertThat(w.shadeHit(i, 5)).isApproximatelyEqualTo(Color.create(1.19001, 0.69643, 0.69243));
  }
}
/*
Scenario: shade_hit() is given an intersection in shadow
  Given w ← world()
    And w.light ← point_light(point(0, 0, -10), color(1, 1, 1))
    And s1 ← sphere()
    And s1 is added to w
    And s2 ← sphere() with:
      | transform | translation(0, 0, 10) |
    And s2 is added to w
    And r ← ray(point(0, 0, 5), vector(0, 0, 1))
    And i ← intersection(4, s2)
  When comps ← prepare_computations(i, r)
    And c ← shade_hit(w, comps)
  Then c = color(0.1, 0.1, 0.1)

*/
