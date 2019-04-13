package projects.appendix1;

import raytracer.AppUtil;
import raytracer.Camera;
import raytracer.Canvas;
import raytracer.Color;
import raytracer.Cube;
import raytracer.Light;
import raytracer.Material;
import raytracer.Matrix;
import raytracer.Plane;
import raytracer.Shape;
import raytracer.Sphere;
import raytracer.Tuple;
import raytracer.World;

public class CoverWorld {

  public static void main(String[] args) {
    Canvas canvas = getCamera().render(getWorld());
    AppUtil.saveCanvasToPng(canvas, "cover");
  }

  public static Camera getCamera() {
    return Camera.create(
        400, 400, 0.785, Tuple.point(-6, 6, -10), Tuple.point(6, 0, 6), Tuple.vector(-0.45, 1, 0));
  }

  public static World getWorld() {
    World world = new World();
    world.addLight(light1());
    world.addLight(light2());
    world.addShape(backdrop());
    world.addShape(sphere1());
    world.addShape(cube1());
    world.addShape(cube2());
    world.addShape(cube3());
    world.addShape(cube4());
    world.addShape(cube5());
    world.addShape(cube6());
    world.addShape(cube7());
    world.addShape(cube8());
    world.addShape(cube9());
    world.addShape(cube10());
    world.addShape(cube11());
    world.addShape(cube12());
    world.addShape(cube13());
    world.addShape(cube14());
    world.addShape(cube15());
    world.addShape(cube16());
    world.addShape(cube17());
    return world;
  }

  private static Light light1() {
    return Light.create(Tuple.point(50, 100, -50), Color.WHITE);
  }

  private static Light light2() {
    return Light.create(Tuple.point(-400, 50, -10), Color.create(0.2, 0.2, 0.2));
  }

  private static Material whiteMaterial() {
    return Material.builder()
        .setColor(Color.WHITE)
        .setDiffuse(0.7)
        .setAmbient(0.1)
        .setSpecular(0)
        .setReflectivity(0.1)
        .build();
  }

  private static Material blueMaterial() {
    return whiteMaterial().toBuilder().setColor(Color.create(0.537, 0.831, 0.914)).build();
  }

  private static Material redMaterial() {
    return whiteMaterial().builder().setColor(Color.create(0.941, 0.322, 0.388)).build();
  }

  private static Material purpleMaterial() {
    return whiteMaterial().builder().setColor(Color.create(0.373, 0.404, 0.550)).build();
  }

  private static Matrix standardTransform() {
    return Matrix.translation(1, -1, 1).scale(0.5, 0.5, 0.5);
  }

  private static Matrix largeObject() {
    return standardTransform().scale(3.5, 3.5, 3.5);
  }

  private static Matrix mediumObject() {
    return standardTransform().scale(3, 3, 3);
  }

  private static Matrix smallObject() {
    return standardTransform().scale(2, 2, 2);
  }

  // White backdrop.
  private static Shape backdrop() {
    Shape backdrop = Plane.create();
    backdrop.setMaterial(
        Material.builder()
            .setColor(Color.WHITE)
            .setAmbient(1)
            .setDiffuse(0)
            .setSpecular(0)
            .build());
    backdrop.setTransform(Matrix.rotationX(Math.PI / 2).translate(0, 0, 500));
    return backdrop;
  }

  private static Shape sphere1() {
    Shape s = Sphere.create();
    s.setMaterial(
        Material.builder()
            .setColor(Color.create(0.373, 0.404, 0.550))
            .setDiffuse(0.2)
            .setAmbient(0)
            .setSpecular(1)
            .setShininess(200)
            .setReflectivity(0.7)
            .setTransparency(0.7)
            .setRefractiveIndex(Material.REFRACTIVE_INDEX_GLASS)
            .build());
    s.setTransform(largeObject());
    return s;
  }

  private static Shape cube1() {
    Shape c = Cube.create();
    c.setMaterial(whiteMaterial());
    c.setTransform(mediumObject().translate(4, 0, 0));
    return c;
  }

  private static Shape cube2() {
    Shape c = Cube.create();
    c.setMaterial(blueMaterial());
    c.setTransform(largeObject().translate(8.5, 1.5, -0.5));
    return c;
  }

  private static Shape cube3() {
    Shape c = Cube.create();
    c.setMaterial(redMaterial());
    c.setTransform(largeObject().translate(0, 0, 4));
    return c;
  }

  private static Shape cube4() {
    Shape c = Cube.create();
    c.setMaterial(whiteMaterial());
    c.setTransform(smallObject().translate(4, 0, 4));
    return c;
  }

  private static Shape cube5() {
    Shape c = Cube.create();
    c.setMaterial(purpleMaterial());
    c.setTransform(mediumObject().translate(7.5, 0.5, 4));
    return c;
  }

  private static Shape cube6() {
    Shape c = Cube.create();
    c.setMaterial(whiteMaterial());
    c.setTransform(mediumObject().translate(-0.25, 0.25, 8));
    return c;
  }

  private static Shape cube7() {
    Shape c = Cube.create();
    c.setMaterial(blueMaterial());
    c.setTransform(largeObject().translate(4, 1, 7.5));
    return c;
  }

  private static Shape cube8() {
    Shape c = Cube.create();
    c.setMaterial(redMaterial());
    c.setTransform(mediumObject().translate(10, 2, 7.5));
    return c;
  }

  private static Shape cube9() {
    Shape c = Cube.create();
    c.setMaterial(whiteMaterial());
    c.setTransform(smallObject().translate(8, 2, 12));
    return c;
  }

  private static Shape cube10() {
    Shape c = Cube.create();
    c.setMaterial(whiteMaterial());
    c.setTransform(smallObject().translate(20, 1, 9));
    return c;
  }

  private static Shape cube11() {
    Shape c = Cube.create();
    c.setMaterial(blueMaterial());
    c.setTransform(largeObject().translate(-0.5, -5, 0.25));
    return c;
  }

  private static Shape cube12() {
    Shape c = Cube.create();
    c.setMaterial(redMaterial());
    c.setTransform(largeObject().translate(4, -4, 0));
    return c;
  }

  private static Shape cube13() {
    Shape c = Cube.create();
    c.setMaterial(whiteMaterial());
    c.setTransform(largeObject().translate(8.5, -4, 0));
    return c;
  }

  private static Shape cube14() {
    Shape c = Cube.create();
    c.setMaterial(blueMaterial());
    c.setTransform(largeObject().translate(0, -4, 4));
    return c;
  }

  private static Shape cube15() {
    Shape c = Cube.create();
    c.setMaterial(purpleMaterial());
    c.setTransform(largeObject().translate(-0.5, -4.5, 8));
    return c;
  }

  private static Shape cube16() {
    Shape c = Cube.create();
    c.setMaterial(whiteMaterial());
    c.setTransform(largeObject().translate(0, -8, 4));
    return c;
  }

  private static Shape cube17() {
    Shape c = Cube.create();
    c.setMaterial(whiteMaterial());
    c.setTransform(largeObject().translate(-0.5, -8.5, 8));
    return c;
  }
}
