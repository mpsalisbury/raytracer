package projects.chapter14;

import raytracer.Cylinder;
import raytracer.Group;
import raytracer.Matrix;
import raytracer.Shape;
import raytracer.Sphere;

// Render a scene of cylinders on a plane.
public class Hexagon {

  public static Shape create() {
    return hexagon();
  }

  private static Shape corner() {
    Shape corner = Sphere.create();
    corner.setTransform(Matrix.scaling(0.25, 0.25, 0.25).translate(0, 0, -1));
    return corner;
  }

  private static Shape edge() {
    Shape edge = Cylinder.create();
    edge.setTransform(
        Matrix.translation(0, 1, 0)
            .scale(0.25, 0.5, 0.25)
            .rotateZ(-Math.PI / 2)
            .rotateY(-Math.PI / 6)
            .translate(0, 0, -1));
    return edge;
  }

  private static Shape side() {
    Group side = Group.create();
    side.add(corner());
    side.add(edge());
    return side;
  }

  private static Shape hexagon() {
    Group hexagon = Group.create();
    for (int n = 0; n < 6; ++n) {
      Shape side = side();
      side.setTransform(Matrix.rotationY(n * Math.PI / 3));
      hexagon.add(side);
    }
    return hexagon;
  }
}
