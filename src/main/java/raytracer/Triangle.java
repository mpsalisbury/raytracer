package raytracer;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.collect.Range;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

// A triangle in space.
@AutoValue
public abstract class Triangle extends Geometry {

  public static Shape create(Tuple p1, Tuple p2, Tuple p3) {
    return new GeometryShape(createRaw(p1, p2, p3));
  }

  public static Shape create(Tuple p1, Tuple p2, Tuple p3, Tuple n1, Tuple n2, Tuple n3) {
    return new GeometryShape(createRaw(p1, p2, p3, n1, n2, n3));
  }

  public static Triangle createRaw(Tuple p1, Tuple p2, Tuple p3) {
    Tuple e1 = p2.minus(p1);
    Tuple e2 = p3.minus(p1);
    Tuple normal = e2.cross(e1).normalize();
    return new AutoValue_Triangle(p1, p2, p3, normal, normal, normal);
  }

  public static Triangle createRaw(Tuple p1, Tuple p2, Tuple p3, Tuple n1, Tuple n2, Tuple n3) {
    return new AutoValue_Triangle(p1, p2, p3, n1, n2, n3);
  }

  public Shape asShape() {
    return new GeometryShape(this);
  }

  // Triangle corner points.
  public abstract Tuple p1();

  public abstract Tuple p2();

  public abstract Tuple p3();

  // Triangle corner normals.
  public abstract Tuple n1();

  public abstract Tuple n2();

  public abstract Tuple n3();

  @Memoized
  public Tuple e1() {
    return p2().minus(p1());
  }

  @Memoized
  public Tuple e2() {
    return p3().minus(p1());
  }

  @Override
  public Range3 getRange() {
    Range<Double> xRange = getRange(p -> p.x());
    Range<Double> yRange = getRange(p -> p.y());
    Range<Double> zRange = getRange(p -> p.z());
    return Range3.create(xRange, yRange, zRange);
  }

  private Range<Double> getRange(ToDoubleFunction<Tuple> getCoord) {
    double min = forEachVertex().mapToDouble(getCoord).min().orElse(0);
    double max = forEachVertex().mapToDouble(getCoord).max().orElse(0);
    return Range.closed(min, max);
  }

  private Stream<Tuple> forEachVertex() {
    return Stream.of(p1(), p2(), p3());
  }

  @Override
  public DoubleStream intersect(Ray ray) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Tuple normalAt(Tuple point) {
    throw new UnsupportedOperationException("Not implemented");
  }

  private static final double EPSILON = 1.0e-5;

  @Override
  public Stream<MaterialIntersection> intersectStream(Ray ray) {
    Tuple rayCrossE2 = ray.direction().cross(e2());
    double det = rayCrossE2.dot(e1());
    // Ray is parallel to triangle.
    if (Math.abs(det) < EPSILON) {
      return Stream.empty();
    }
    // TODO: rework to understand.
    double f = 1 / det;
    Tuple p1ToOrigin = ray.origin().minus(p1());
    double u = f * p1ToOrigin.dot(rayCrossE2);
    // Ray misses p1-p3 edge.
    if ((u < 0) || (u > 1)) {
      return Stream.empty();
    }

    Tuple originCrossE1 = p1ToOrigin.cross(e1());
    double v = f * ray.direction().dot(originCrossE1);
    // Ray misses p1-p2 or p2-p3 edge.
    if ((v < 0) || (u + v > 1)) {
      return Stream.empty();
    }

    // Ray hits.
    double t = f * e2().dot(originCrossE1);
    Tuple normal = normalAt(u, v);
    Material material = Material.create();
    int shapeId = System.identityHashCode(this);
    return Stream.of(MaterialIntersection.create(ray, t, normal, material, shapeId));
  }

  private Tuple normalAt(double u, double v) {
    Tuple weightedN1 = n1().times(1 - u - v);
    Tuple weightedN2 = n2().times(u);
    Tuple weightedN3 = n3().times(v);
    return weightedN1.plus(weightedN2).plus(weightedN3);
  }
}
