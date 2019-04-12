package projects.chapter16;

import raytracer.CSG;
import raytracer.Cube;
import raytracer.Cylinder;
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
    return sphere;
  }

  private static Shape cube() {
    Shape cube = Cube.create();
    return cube;
  }

  private static Shape holes() {
    Shape xHole = Cylinder.create();
    xHole.setTransform(Matrix.scaling(0.5, 1.1, 0.5).rotateZ(Math.PI / 2));

    Shape yHole = Cylinder.create();
    yHole.setTransform(Matrix.scaling(0.5, 1.1, 0.5));

    Shape zHole = Cylinder.create();
    zHole.setTransform(Matrix.scaling(0.5, 1.1, 0.5).rotateX(Math.PI / 2));

    return CSG.createUnion(xHole, CSG.createUnion(yHole, zHole));
  }
}
