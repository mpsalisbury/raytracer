package projects.chapter10;

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

// Render a scene of spheres on a plane.
public class StripeBalls {

  public static void main(String[] args) {
    Canvas canvas = new StripeBalls().render();
    AppUtil.saveCanvasToPng(canvas, "stripeballs");
  }

  private final World world;
  private final Camera camera;

  private StripeBalls() {
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
            .setPattern(Pattern.createRing(Color.create(1, 0.9, 0.9), Color.create(0.1, 0.3, 0.3)))
            .setSpecular(0)
            .setCastsShadow(false)
            .build());
    return p;
  }

  private static Shape createBall1() {
    Shape s = Sphere.create();
    s.setTransform(Matrix.translation(-0.5, 1, 0.5));
    s.setMaterial(
        Material.builder()
            .setPattern(
                Pattern.createNoise(
                    0.5,
                    0.2,
                    // Pattern.create3dCheckerBuilder(
                    //       Color.create(0.1, 1, 0.5), Color.create(0.1, 0.5, 0.2))
                    Pattern.createStripeBuilder(
                            Color.create(0.1, 1, 0.5), Color.create(0.1, 0.5, 0.2))
                        .setTransform(Matrix.scaling(0.5, 0.5, 0.5).rotateY(Math.PI / 4))
                        .build()))
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
            .setPattern(
                Pattern.createStripeBuilder(Color.create(0.5, 1, 0.1), Color.create(0.2, 0.5, 0.1))
                    .setTransform(Matrix.scaling(0.25, 0.25, 0.25).rotateY(Math.PI / 5))
                    .build())
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
            .setPattern(
                Pattern.createStripeBuilder(Color.create(1, 0.8, 0.1), Color.create(0.5, 0.4, 0.1))
                    .setTransform(
                        Matrix.scaling(0.33, 0.33, 0.33).rotateX(Math.PI / 2).rotateY(Math.PI / 5))
                    .build())
            .setDiffuse(0.7)
            .setSpecular(0.3)
            .build());
    return s;
  }

  private Canvas render() {
    return camera.render(world);
  }
}
