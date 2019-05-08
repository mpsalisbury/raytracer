package raytracer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

// A 3d range of spatial values.
class Range3 {

  public static Range3 createEmpty() {
    return create(null, null, null);
  }

  public static Range3 createUnbounded() {
    return create(Range.all(), Range.all(), Range.all());
  }

  public static Range3 create(
      double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
    return create(Range.closed(xMin, xMax), Range.closed(yMin, yMax), Range.closed(zMin, zMax));
  }

  public static Range3 create(Range<Double> xRange, Range<Double> yRange, Range<Double> zRange) {
    return new Range3(xRange, yRange, zRange);
  }

  private Range<Double> xRange;
  private Range<Double> yRange;
  private Range<Double> zRange;

  private Range3(Range<Double> xRange, Range<Double> yRange, Range<Double> zRange) {
    this.xRange = xRange;
    this.yRange = yRange;
    this.zRange = zRange;
  }

  public BoundingBox createBoundingBox() {
    return new Range3BoundingBox(this);
  }

  // Returns a cube-oid inverse transform (or null for degenerate range).
  private Matrix makeCubeTransform() {
    if (isEmpty() || isUnbounded()) {
      return null;
    }

    double xSize = xRange.upperEndpoint() - xRange.lowerEndpoint();
    double xMiddle = (xRange.upperEndpoint() + xRange.lowerEndpoint()) / 2;
    double ySize = yRange.upperEndpoint() - yRange.lowerEndpoint();
    double yMiddle = (yRange.upperEndpoint() + yRange.lowerEndpoint()) / 2;
    double zSize = zRange.upperEndpoint() - zRange.lowerEndpoint();
    double zMiddle = (zRange.upperEndpoint() + zRange.lowerEndpoint()) / 2;
    return Matrix.scaling(xSize / 2, ySize / 2, zSize / 2).translate(xMiddle, yMiddle, zMiddle);
  }

  // Returns the range spanning this Range3 and other.
  public Range3 span(Range3 other) {
    if (isEmpty()) {
      return other;
    } else if (other.isEmpty()) {
      return this;
    } else {
      return new Range3(
          xRange.span(other.xRange), yRange.span(other.yRange), zRange.span(other.zRange));
    }
  }

  private boolean isEmpty() {
    return xRange == null;
    // return xRange.isEmpty() || yRange.isEmpty() || zRange.isEmpty();
  }

  private boolean isUnbounded() {
    boolean fullyBounded =
        xRange.hasLowerBound()
            && xRange.hasUpperBound()
            && xRange.hasLowerBound()
            && xRange.hasUpperBound()
            && xRange.hasLowerBound()
            && xRange.hasUpperBound();
    return !fullyBounded;
  }

  // Returns this range transformed by the given transform.
  private Range3 transform(Matrix transform) {
    if (isEmpty()) {
      return this;
    }

    // If any dimension is open-ended, mark this transform as unbounded.
    if (isUnbounded()) {
      return Range3.createUnbounded();
    }

    // Collect the corners of this range.
    List<Double> xs = ImmutableList.of(xRange.lowerEndpoint(), xRange.upperEndpoint());
    List<Double> ys = ImmutableList.of(yRange.lowerEndpoint(), yRange.upperEndpoint());
    List<Double> zs = ImmutableList.of(zRange.lowerEndpoint(), zRange.upperEndpoint());

    // Find range that includes all eight transformed corners of this range.
    return cartesian(xs, ys, zs, (x, y, z) -> Tuple.point(x, y, z))
        .map(p -> transform.times(p))
        .map(p -> toRange(p))
        .reduce((a, b) -> a.span(b))
        .get();
  }

  // Accepts three T's to produce a U.
  @FunctionalInterface
  private interface Generator3D<T, U> {
    U generate(T x, T y, T z);
  }

  // Returns stream of cartesian combinations of xs, ys, and zs, combined by generator function.
  private <T, U> Stream<U> cartesian(
      List<T> xs, List<T> ys, List<T> zs, Generator3D<T, U> generator) {
    return xs.stream()
        .flatMap(x -> ys.stream().flatMap(y -> zs.stream().map(z -> generator.generate(x, y, z))));
  }

  // Returns a point Range3 for the given Point.
  private static Range3 toRange(Tuple p) {
    return Range3.create(Range.singleton(p.x()), Range.singleton(p.y()), Range.singleton(p.z()));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    Range3 other = (Range3) obj;
    return this.xRange.equals(other.xRange)
        && this.yRange.equals(other.yRange)
        && this.zRange.equals(other.zRange);
  }

  @Override
  public int hashCode() {
    return Objects.hash(xRange, yRange, zRange);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Range3(")
        .append(xRange)
        .append(yRange)
        .append(zRange)
        .append(")")
        .toString();
  }

  public Tuple getMinPoint_TESTING() {
    if (isEmpty()) {
      return Tuple.point(0, 0, 0);
    }
    return Tuple.point(xRange.lowerEndpoint(), yRange.lowerEndpoint(), zRange.lowerEndpoint());
  }

  public Tuple getMaxPoint_TESTING() {
    if (isEmpty()) {
      return Tuple.point(0, 0, 0);
    }
    return Tuple.point(xRange.upperEndpoint(), yRange.upperEndpoint(), zRange.upperEndpoint());
  }

  // Converts an intersectable into a bounding box.
  private static class Range3BoundingBox implements BoundingBox {
    private Range3 range;
    private Matrix transform;
    private Matrix inverseTransform;

    public Range3BoundingBox(Range3 range) {
      this.range = range;
      this.transform = range.makeCubeTransform();
      this.inverseTransform = (transform == null) ? null : transform.invert();
    }

    @Override
    public boolean maybeHits(Ray ray) {
      if (range.isEmpty()) {
        return false;
      }
      if (range.isUnbounded()) {
        return true;
      }
      return new Cube().intersectStream(ray.transform(inverseTransform)).anyMatch(i -> true);
    }

    @Override
    public Range3 getRange() {
      return range;
    }

    @Override
    public BoundingBox transform(Matrix transform) {
      return new Range3BoundingBox(range.transform(transform));
    }
  }
}
