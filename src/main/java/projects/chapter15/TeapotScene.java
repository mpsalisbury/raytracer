package projects.chapter15;

import java.io.IOException;
import raytracer.AppUtil;
import raytracer.Camera;
import raytracer.Canvas;
import raytracer.Color;
import raytracer.Cube;
import raytracer.Light;
import raytracer.Material;
import raytracer.Matrix;
import raytracer.ObjFile;
import raytracer.ObjFile.ParsingException;
import raytracer.Pattern;
import raytracer.Shape;
import raytracer.Sphere;
import raytracer.Tuple;
import raytracer.World;

// Render a scene with intersting shape.
public class TeapotScene {

  public static void main(String[] args) {
    Canvas canvas = new TeapotScene().render();
    AppUtil.saveCanvasToPng(canvas, "teapotscene");
  }

  private final World world;
  private final Camera camera;

  private TeapotScene() {
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
    w.addShape(createTeapot());
    return w;
  }

  private static Shape createFloor() {
    Shape p = Cube.create();
    p.setMaterial(
        Material.builder()
            .setPattern(
                Pattern.create3dCheckerBuilder(
                        Color.create(0.8, 0.8, 0.8), Color.create(0.6, 0.6, 0.6))
                    .setTransform(Matrix.translation(0.1, 0.1, 0.1))
                    .build())
            .setSpecular(0)
            .build());
    p.setTransform(Matrix.translation(0, 1, 0).scale(20));
    return p;
  }

  private static Shape createTeapot() {
    try {
      Shape teapot = ObjFile.parseResource("/teapot-low.obj").asShape();
      teapot.setTransform(
          Matrix.scaling(0.15).rotateX(-Math.PI / 2).rotateY(Math.PI / 6).translate(0, 0, 1));
      teapot.setMaterial(
          Material.builder()
              .setColor(Color.create(0.8, 0.2, 0.2))
              .setDiffuse(0.7)
              .setSpecular(300)
              // .setReflectivity(0.3)
              .build());
      return teapot;
    } catch (IOException | ParsingException e) {
      System.err.println("Error loading file: " + e.getMessage());
      return Sphere.create(); // TODO yuck
    }
  }

  private Canvas render() {
    return camera.render(world);
  }
}
