package projects.chapter16;

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
import raytracer.Tuple;
import raytracer.World;

// Render a scene with intersting shape.
public class RoundHoleCubeScene {

  public static void main(String[] args) {
    Canvas canvas = new RoundHoleCubeScene().render();
    AppUtil.saveCanvasToPng(canvas, "rhcscene");
  }

  private final World world;
  private final Camera camera;

  private RoundHoleCubeScene() {
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
    w.addShape(createRoundHoleCube());
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
            .build());
    return p;
  }

  private static Shape createRoundHoleCube() {
    Shape rhc = RoundHoleCube.create();
    rhc.setTransform(Matrix.translation(0, 1, 2).rotateY(Math.PI / 3));
    rhc.setMaterial(Material.builder().setColor(Color.create(0.8, 0.1, 0.1)).build());
    return rhc;
  }

  private Canvas render() {
    return camera.render(world);
  }
}
