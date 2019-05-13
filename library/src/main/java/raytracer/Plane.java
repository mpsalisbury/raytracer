package raytracer;

import com.google.common.collect.Range;
import java.util.stream.DoubleStream;

// Plane on X-Z plane normal in +Y.
public class Plane extends Geometry {

  public static Shape create() {
    return new GeometryShape(new Plane());
  }

  private static final Tuple NORMAL = Tuple.vector(0, 1, 0);
  private static final double EPSILON = 1.0e-5;

  @Override
  public Range3 getRange() {
    return Range3.create(Range.all(), Range.singleton(0.0), Range.all());
  }

  @Override
  public DoubleStream intersect(Ray ray) {
    double yOrigin = ray.origin().y();
    double yVelocity = ray.direction().y();
    if (Math.abs(yVelocity) < EPSILON) {
      // if parallel to plane, doesn't hit.
      return DoubleStream.empty();
    }
    return DoubleStream.of(-yOrigin / yVelocity);
  }

  @Override
  public Tuple normalAt(Tuple point) {
    return NORMAL;
  }
}
