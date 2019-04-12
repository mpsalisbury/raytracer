package projects.chapter16;

import raytracer.AppUtil;
import raytracer.CSG;
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

// Render a scene of CSG-composed shapes.
public class CsgScene {

  public static void main(String[] args) {
    Canvas canvas = new CsgScene().render();
    AppUtil.saveCanvasToPng(canvas, "csgscene");
  }

  private final World world;
  private final Camera camera;

  private CsgScene() {
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
    w.addShape(createSphereCsgUnion());
    w.addShape(createSphereCsgIntersection());
    w.addShape(createSphereCsgDifference());
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

  private static Shape createSphereCsgUnion() {
    Shape left = Sphere.create();
    left.setTransform(Matrix.translation(-0.5, 0, 0));
    Shape right = Sphere.create();
    right.setTransform(Matrix.translation(+0.5, 0, 0));
    Shape csg = CSG.createUnion(left, right);
    csg.setTransform(Matrix.translation(-3, 1, 4));
    csg.setMaterial(Material.builder().setColor(Color.create(0.8, 0.1, 0.1)).build());
    return csg;
  }

  private static Shape createSphereCsgIntersection() {
    Shape left = Sphere.create();
    left.setTransform(Matrix.translation(-0.5, 0, 0));
    Shape right = Sphere.create();
    right.setTransform(Matrix.translation(+0.5, 0, 0));
    Shape csg = CSG.createIntersection(left, right);
    csg.setTransform(Matrix.rotationY(Math.PI / 8).translate(0, 1, 4));
    csg.setMaterial(Material.builder().setColor(Color.create(0.8, 0.1, 0.1)).build());
    return csg;
  }

  private static Shape createSphereCsgDifference() {
    Shape left = Sphere.create();
    left.setTransform(Matrix.translation(-0.5, 0, 0));
    Shape right = Sphere.create();
    right.setTransform(Matrix.translation(+0.5, 0, 0));
    Shape csg = CSG.createDifference(left, right);
    csg.setTransform(Matrix.rotationY(Math.PI / 4).translate(3, 1, 4));
    csg.setMaterial(Material.builder().setColor(Color.create(0.8, 0.1, 0.1)).build());
    return csg;
  }

  private Canvas render() {
    return camera.render(world);
  }
}
