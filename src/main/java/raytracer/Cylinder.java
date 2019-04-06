package raytracer;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

// Cylinder centered at origin with radius 1, top at y=1, bottom at y=-1.
public class Cylinder extends Geometry {

  public static Shape create() {
    return new GeometryShape(new Cylinder());
  }

  private static final Tuple TOP_NORMAL = Tuple.createVector(0, 1, 0);
  private static final Tuple BOTTOM_NORMAL = Tuple.createVector(0, -1, 0);
  private static final double TOP_Y = 1;
  private static final double BOTTOM_Y = -1;
  private static final double RADIUS = 1;
  private static final double RADIUS2 = RADIUS * RADIUS;
  private static final double EPSILON = 1.0e-5;

  @Override
  public DoubleStream intersect(Ray ray) {
    List<Double> wallTs = intersectWalls(ray).boxed().collect(Collectors.toList());
    List<Double> capTs = intersectCaps(ray).boxed().collect(Collectors.toList());
    if (capTs.isEmpty()) {
      if (isRayBetweenCaps(ray)) {
        return wallTs.stream().mapToDouble(x -> x);
      } else {
        return DoubleStream.empty();
      }
    }
    if (wallTs.isEmpty()) {
      if (isRayBetweenWalls(ray)) {
        return capTs.stream().mapToDouble(x -> x);
      } else {
        return DoubleStream.empty();
      }
    }
    return intersectRanges(Lists.newArrayList(wallTs, capTs));
  }

  private boolean isRayBetweenCaps(Ray ray) {
    if (!ray.direction().isHorizontal()) {
      return false;
    }
    double y = ray.origin().y();
    return y > BOTTOM_Y && y < TOP_Y;
  }

  private boolean isRayBetweenWalls(Ray ray) {
    if (!ray.direction().isVertical()) {
      return false;
    }
    Tuple originToPoint = Tuple.createVector(ray.origin().x(), 0, ray.origin().z());
    return originToPoint.dot(originToPoint) < RADIUS2;
  }

  public DoubleStream intersectRanges(List<List<Double>> ranges) {
    DoubleStream mins =
        ranges
            .stream()
            .mapToDouble(range -> range.stream().mapToDouble(x -> x).min().getAsDouble());
    DoubleStream maxs =
        ranges
            .stream()
            .mapToDouble(range -> range.stream().mapToDouble(x -> x).max().getAsDouble());
    double maxMin = mins.max().getAsDouble();
    double minMax = maxs.min().getAsDouble();
    if (maxMin < minMax) {
      return DoubleStream.of(maxMin, minMax);
    } else {
      return DoubleStream.empty();
    }
  }

  // Returns t's where ray intersects cylinder walls.
  // Returns empty stream if no intersection.
  public DoubleStream intersectWalls(Ray ray) {
    // Flatten ray in y dimension.
    Tuple fro = Tuple.createVector(ray.origin().x(), 0, ray.origin().z());
    Tuple frd = Tuple.createVector(ray.direction().x(), 0, ray.direction().z());
    double a = frd.dot(frd);
    double b = 2.0 * (frd.dot(fro));
    double c = fro.dot(fro) - RADIUS2;
    double discriminant = b * b - 4.0 * a * c;

    if (discriminant < 0.0 || Math.abs(a) < EPSILON) {
      // Ray misses cylinder.
      return DoubleStream.empty();
    }
    double t1 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
    double t2 = (-b + Math.sqrt(discriminant)) / (2.0 * a);
    return DoubleStream.of(t1, t2);
  }

  // Returns t's where ray intersects cylinder cap planes.
  public DoubleStream intersectCaps(Ray ray) {
    Optional<Double> t1 = intersectPlane(ray.origin().y(), ray.direction().y(), TOP_Y);
    Optional<Double> t2 = intersectPlane(ray.origin().y(), ray.direction().y(), BOTTOM_Y);
    if (t1.isPresent() && t2.isPresent()) {
      return DoubleStream.of(t1.get(), t2.get()).sorted();
    } else {
      return DoubleStream.empty();
    }
  }

  // Flatten ray in y dimension.
  // Returns t for where 1d ray starting at origin with direction/velocity hits plane
  // (origin + t * direction = plane)
  public Optional<Double> intersectPlane(double origin, double direction, double plane) {
    double target = plane - origin;
    if (Math.abs(direction) < EPSILON) {
      return Optional.empty();
    } else {
      return Optional.of(target / direction);
    }
  }

  @Override
  public Tuple normalAt(Tuple point) {
    double y = point.y();
    if (Math.abs(y - BOTTOM_Y) < EPSILON) {
      return BOTTOM_NORMAL;
    }
    if (Math.abs(y - TOP_Y) < EPSILON) {
      return TOP_NORMAL;
    }
    return Tuple.createVector(point.x(), 0, point.z());
  }
}
