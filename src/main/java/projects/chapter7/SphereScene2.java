package projects.chapter7;

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

// Render a scene of spheres.
public class SphereScene2 {

  public static void main(String[] args) {
    Canvas canvas = new SphereScene2().render();
    AppUtil.saveCanvasToPng(canvas, "spherescene2");
  }

  private final World world;
  private final Camera camera;

  private SphereScene2() {
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
    w.addShape(createBall(-1,0,Color.create(1,0.1,0.2)));
    w.addShape(createBall(0,0,Color.create(0.2,1,0.1)));
    w.addShape(createBall(1,0,Color.create(0.1,0.2,1)));
    w.addShape(createBall(0,1,Color.create(1,0.2,1)));
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

  private static Shape createBall(double x, double z, Color color) {
    Shape s = Sphere.create();
    s.setTransform(Matrix.scaling(0.1).translate(x, 0.1, z));
    s.setMaterial(
        Material.builder()
            .setColor(color)
            .setDiffuse(0.7)
            .setSpecular(0.3)
            .build());
    return s;
  }

  private Canvas render() {
    return camera.render(world);
  }
}
