package projects.chapter16;

import raytracer.CSG;
import raytracer.Color;
import raytracer.Cube;
import raytracer.Cylinder;
import raytracer.Material;
import raytracer.Matrix;
import raytracer.Shape;
import raytracer.Sphere;

// A CSG-constructed shape.
public class RoundHoleCube {

  public static Shape create() {
    Shape roundCube = CSG.createIntersection(sphere(), cube());
    return CSG.createDifference(roundCube, holes());
  }

  private static Shape sphere() {
    Shape sphere = Sphere.create();
    sphere.setTransform(Matrix.scaling(Math.sqrt(2)));
    sphere.setMaterial(
        Material.builder().setColor(Color.BLACK).setReflectivity(0.5).setSpecular(300).build());
    return sphere;
  }

  private static Shape cube() {
    Shape cube = Cube.create();
    cube.setMaterial(
        Material.builder()
            .setColor(Color.create(0.7, 0.7, 0.7))
            .setDiffuse(0.4)
            .setReflectivity(0.2)
            .build());
    return cube;
  }

  private static Shape holes() {
    Shape xHole = Cylinder.create();
    xHole.setTransform(Matrix.scaling(0.5, 1.1, 0.5).rotateZ(Math.PI / 2));
    xHole.setMaterial(Material.builder().setColor(Color.create(0.9, 0.1, 0.1)).build());

    Shape yHole = Cylinder.create();
    yHole.setTransform(Matrix.scaling(0.5, 1.1, 0.5));
    yHole.setMaterial(Material.builder().setColor(Color.create(0.1, 0.9, 0.1)).build());

    Shape zHole = Cylinder.create();
    zHole.setTransform(Matrix.scaling(0.5, 1.1, 0.5).rotateX(Math.PI / 2));
    zHole.setMaterial(Material.builder().setColor(Color.create(0.1, 0.1, 0.9)).build());

    return CSG.createUnion(xHole, CSG.createUnion(yHole, zHole));
  }
}
