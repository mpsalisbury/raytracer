package projects.chapter11;

import raytracer.AppUtil;
import raytracer.Camera;
import raytracer.Canvas;
import raytracer.Color;
import raytracer.Light;
import raytracer.Material;
import raytracer.Matrix;
import raytracer.Pattern;
import raytracer.Plane;
import raytracer.Shape;
import raytracer.Sphere;
import raytracer.Tuple;
import raytracer.World;

// Render a scene of spheres on a plane with refraction.
public class RefractScene {

  public static void main(String[] args) {
    Canvas canvas = new RefractScene().render();
    AppUtil.saveCanvasToPng(canvas, "refractscene");
  }

  private final World world;
  private final Camera camera;

  private RefractScene() {
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
    w.addShape(createBall1());
    w.addShape(createBall2());
    w.addShape(createBall3());
    return w;
  }

  private static Shape createFloor() {
    Shape p = Plane.create();
    p.setMaterial(
        Material.builder()
            .setPattern(
                Pattern.create3dCheckerBuilder(Color.create(1, 0.9, 0.9), Color.create(0, 0.1, 0.1))
                    .setTransform(Matrix.translation(0, -0.1, 0))
                    .build())
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
            .setColor(Color.create(0.4, 0, 0))
            .setDiffuse(0.0)
            .setSpecular(1)
            .setReflectivity(0.5)
            .setTransparency(1.0)
            .setRefractiveIndex(Material.REFRACTIVE_INDEX_GLASS)
            .build());
    return s;
  }

  private static Shape createBall2() {
    Shape s = Sphere.create();
    s.setTransform(Matrix.scaling(0.5, 0.5, 0.5).translate(1.5, 0.5, -0.5));
    s.setMaterial(
        Material.builder()
            .setColor(Color.create(0, 0, 0.4))
            .setDiffuse(0.0)
            .setSpecular(1)
            .setReflectivity(0.5)
            .setTransparency(1.0)
            .setRefractiveIndex(Material.REFRACTIVE_INDEX_GLASS)
            .build());
    return s;
  }

  private static Shape createBall3() {
    Shape s = Sphere.create();
    s.setTransform(Matrix.scaling(0.33, 0.33, 0.33).translate(-1.5, 0.33, -0.75));
    s.setMaterial(
        Material.builder()
            .setColor(Color.create(0, 0.4, 0))
            .setDiffuse(0.0)
            .setSpecular(1)
            .setTransparency(1.0)
            .setRefractiveIndex(Material.REFRACTIVE_INDEX_GLASS)
            .build());
    return s;
  }

  private Canvas render() {
    return camera.render(world);
  }
}
