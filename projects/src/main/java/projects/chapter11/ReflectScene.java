package projects.chapter11;

import raytracer.AppUtil;
import raytracer.Camera;
import raytracer.Canvas;
import raytracer.Color;
import raytracer.Light;
import raytracer.Material;
import raytracer.Matrix;
import raytracer.Plane;
import raytracer.Shape;
import raytracer.Sphere;
import raytracer.Tuple;
import raytracer.World;

// Render a scene of spheres on a plane with reflection.
public class ReflectScene {

  public static void main(String[] args) {
    Canvas canvas = new ReflectScene().render();
    AppUtil.saveCanvasToPng(canvas, "reflectscene");
  }

  private final World world;
  private final Camera camera;

  private ReflectScene() {
    world = createWorld();
    camera = createCamera();
  }

  private static Camera createCamera() {
    return Camera.create(
        600,
        300,
        Math.PI / 3,
        Tuple.point(0, 1.5, -5),
        Tuple.point(0, 1, 0),
        Tuple.vector(0, 1, 0));
  }

  private static World createWorld() {
    World w = new World();
    w.addLight(Light.create(Tuple.point(-10, 10, -10), Color.WHITE));
    w.addLight(Light.create(Tuple.point(-10, -3, 10), Color.create(0.3, 0.3, 0.3)));
    w.addShape(createFloor());
    w.addShape(createBall1());
    w.addShape(createBall2());
    w.addShape(createBall3());
    return w;
  }

  private static Shape createFloor() {
    Shape p = Plane.create();
    p.setMaterial(
        Material.builder()
            .setColor(Color.create(1, 0.9, 0.9))
            .setSpecular(0)
            .setReflectivity(0.5)
            .setCastsShadow(false)
            .build());
    return p;
  }

  private static Shape createBall1() {
    Shape s = Sphere.create();
    s.setTransform(Matrix.translation(-0.5, 1, 0.5));
    s.setMaterial(
        Material.builder()
            .setColor(Color.create(0.1, 1, 0.2))
            .setDiffuse(0.7)
            .setSpecular(0.3)
            .setReflectivity(0.5)
            .build());
    return s;
  }

  private static Shape createBall2() {
    Shape s = Sphere.create();
    s.setTransform(Matrix.scaling(0.5, 0.5, 0.5).translate(1.5, 0.5, -0.5));
    s.setMaterial(
        Material.builder()
            .setColor(Color.create(1, 0.1, 0.2))
            .setDiffuse(0.7)
            .setSpecular(0.3)
            .setReflectivity(0.5)
            .build());
    return s;
  }

  private static Shape createBall3() {
    Shape s = Sphere.create();
    s.setTransform(Matrix.scaling(0.33, 0.33, 0.33).translate(-1.5, 0.33, -0.75));
    s.setMaterial(
        Material.builder()
            .setColor(Color.create(0.1, 0.2, 1))
            .setDiffuse(0.7)
            .setSpecular(0.3)
            .setReflectivity(0.5)
            .build());
    return s;
  }

  private Canvas render() {
    return camera.render(world);
  }
}
