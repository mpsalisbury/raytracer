package raytracer;

import java.util.stream.DoubleStream;

// Plane on X-Z plane normal in +Y.
public class Plane extends Shape {

  private static final Tuple NORMAL = Tuple.createVector(0, 1, 0);
  private static final double EPSILON = 1.0e-5;

  @Override
  public DoubleStream localIntersect(Ray ray) {
    double yOrigin = ray.origin().y();
    double yVelocity = ray.direction().y();
    if (Math.abs(yVelocity) < EPSILON) {
      // if parallel to plane, doesn't hit.
      return DoubleStream.empty();
    }
    return DoubleStream.of(-yOrigin / yVelocity);
  }

  @Override
  public Tuple localNormalAt(Tuple point) {
    return NORMAL;
  }
}
