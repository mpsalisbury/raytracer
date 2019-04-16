package raytracer;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import java.util.stream.DoubleStream;

// A triangle in space.
@AutoValue
public abstract class Triangle extends Geometry {

  public static Shape create(Tuple p1, Tuple p2, Tuple p3) {
    return new GeometryShape(createRaw(p1, p2, p3));
  }

  public static Triangle createRaw(Tuple p1, Tuple p2, Tuple p3) {
    return new AutoValue_Triangle(p1, p2, p3);
  }

  public Shape asShape() {
    return new GeometryShape(this);
  }

  public abstract Tuple p1();

  public abstract Tuple p2();

  public abstract Tuple p3();

  @Memoized
  public Tuple e1() {
    return p2().minus(p1());
  }

  @Memoized
  public Tuple e2() {
    return p3().minus(p1());
  }

  @Memoized
  public Tuple normal() {
    return e2().cross(e1()).normalize();
  }

  private static final double EPSILON = 1.0e-5;

  @Override
  public DoubleStream intersect(Ray ray) {
    Tuple rayCrossE2 = ray.direction().cross(e2());
    double det = rayCrossE2.dot(e1());
    // Ray is parallel to triangle.
    if (Math.abs(det) < EPSILON) {
      return DoubleStream.empty();
    }
    // TODO: rework to understand.
    double f = 1 / det;
    Tuple p1ToOrigin = ray.origin().minus(p1());
    double u = f * p1ToOrigin.dot(rayCrossE2);
    // Ray misses p1-p3 edge.
    if ((u < 0) || (u > 1)) {
      return DoubleStream.empty();
    }

    Tuple originCrossE1 = p1ToOrigin.cross(e1());
    double v = f * ray.direction().dot(originCrossE1);
    // Ray misses p1-p2 or p2-p3 edge.
    if ((v < 0) || (u + v > 1)) {
      return DoubleStream.empty();
    }

    // Ray hits.
    double t = f * e2().dot(originCrossE1);
    return DoubleStream.of(t);
  }

  @Override
  public Tuple normalAt(Tuple point) {
    return normal();
  }
}
