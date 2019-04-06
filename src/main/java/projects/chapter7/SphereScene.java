package projects.chapter7;

import raytracer.AppUtil;
import raytracer.Camera;
import raytracer.Canvas;
import raytracer.Color;
import raytracer.Light;
import raytracer.Material;
import raytracer.Matrix;
import raytracer.Shape;
import raytracer.Sphere;
import raytracer.Tuple;
import raytracer.World;

// Render a scene of spheres.
public class SphereScene {

  public static void main(String[] args) {
    Canvas canvas = new SphereScene().render();
    AppUtil.saveCanvasToPng(canvas, "spherescene");
  }

  private final World world;
  private final Camera camera;

  private SphereScene() {
    world = createWorld();
    camera = createCamera();
  }

  private static Camera createCamera() {
    return Camera.create(
        600,
        300,
        Math.PI / 3,
        Tuple.createPoint(0, 1.5, -5),
        Tuple.createPoint(0, 1, 0),
        Tuple.createVector(0, 1, 0));
  }

  private static World createWorld() {
    World w = new World();
    w.addLight(Light.create(Tuple.createPoint(-10, 10, -10), Color.WHITE));
    w.addShape(createFloor());
    w.addShape(createLeftWall());
    w.addShape(createRightWall());
    w.addShape(createBall1());
    w.addShape(createBall2());
    w.addShape(createBall3());
    return w;
  }

  private static Shape createFloor() {
    Shape s = Sphere.create();
    s.setTransform(Matrix.scaling(10, 0.01, 10));
    s.setMaterial(Material.builder().setColor(Color.create(1, 0.9, 0.9)).setSpecular(0).build());
    return s;
  }

  private static Shape createLeftWall() {
    Shape s = Sphere.create();
    s.setTransform(
        Matrix.scaling(10, 0.01, 10).rotateX(Math.PI / 2).rotateY(-Math.PI / 4).translate(0, 0, 5));
    s.setMaterial(Material.builder().setColor(Color.create(1, 0.9, 0.9)).setSpecular(0).build());
    return s;
  }

  private static Shape createRightWall() {
    Shape s = Sphere.create();
    s.setTransform(
        Matrix.scaling(10, 0.01, 10).rotateX(Math.PI / 2).rotateY(Math.PI / 4).translate(0, 0, 5));
    s.setMaterial(Material.builder().setColor(Color.create(1, 0.9, 0.9)).setSpecular(0).build());
    return s;
  }

  private static Shape createBall1() {
    Shape s = Sphere.create();
    s.setTransform(Matrix.translation(-0.5, 1, 0.5));
    s.setMaterial(
        Material.builder()
            .setColor(Color.create(0.1, 1, 0.5))
            .setDiffuse(0.7)
            .setSpecular(0.3)
            .build());
    return s;
  }

  private static Shape createBall2() {
    Shape s = Sphere.create();
    s.setTransform(Matrix.scaling(0.5, 0.5, 0.5).translate(1.5, 0.5, -0.5));
    s.setMaterial(
        Material.builder()
            .setColor(Color.create(0.5, 1, 0.1))
            .setDiffuse(0.7)
            .setSpecular(0.3)
            .build());
    return s;
  }

  private static Shape createBall3() {
    Shape s = Sphere.create();
    s.setTransform(Matrix.scaling(0.33, 0.33, 0.33).translate(-1.5, 0.33, -0.75));
    s.setMaterial(
        Material.builder()
            .setColor(Color.create(1, 0.8, 0.1))
            .setDiffuse(0.7)
            .setSpecular(0.3)
            .build());
    return s;
  }

  private Canvas render() {
    return camera.render(world);
  }
}
