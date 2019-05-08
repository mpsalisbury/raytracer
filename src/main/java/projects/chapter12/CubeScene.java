package projects.chapter12;

import raytracer.AppUtil;
import raytracer.Camera;
import raytracer.Canvas;
import raytracer.Color;
import raytracer.Cube;
import raytracer.Light;
import raytracer.Material;
import raytracer.Matrix;
import raytracer.Pattern;
import raytracer.Plane;
import raytracer.Shape;
import raytracer.Tuple;
import raytracer.World;

// Render a scene of cubes on a plane.
public class CubeScene {

  public static void main(String[] args) {
    Canvas canvas = new CubeScene().render();
    AppUtil.saveCanvasToPng(canvas, "cubescene");
  }

  private final World world;
  private final Camera camera;

  private CubeScene() {
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
    w.addShape(createCube1());
    w.addShape(createCube2());
    w.addShape(createCube3());
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

  private static Shape createCube1() {
    Shape s = Cube.create();
    s.setTransform(Matrix.rotationY(Math.PI / 6).translate(-0.5, 1, 0.5));
    s.setMaterial(
        Material.builder().setColor(Color.create(0.4, 0, 0)).setReflectivity(0.5).build());
    return s;
  }

  private static Shape createCube2() {
    Shape s = Cube.create();
    s.setTransform(Matrix.scaling(0.5, 0.5, 0.5).rotateY(-Math.PI / 6).translate(1.5, 0.5, -0.5));
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

  private static Shape createCube3() {
    Shape s = Cube.create();
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
