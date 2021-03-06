package raytracer;

import java.util.Optional;
import java.util.stream.DoubleStream;

// Cone centered at origin with bottom radius 1, top at y=1, bottom at y=-1.
public class Cone extends Geometry {

  public static Shape create() {
    return new GeometryShape(new Cone());
  }

  // Define cone from y={-2..0} then translate up y+1.
  private static final Tuple BOTTOM_NORMAL = Tuple.vector(0, -1, 0);
  private static final double TOP_Y = 0;
  private static final double BOTTOM_Y = -2;
  private static final double RADIUS = 1;
  private static final double RADIUS2 = RADIUS * RADIUS;
  private static final double Y_SHIFT = 1;
  private static final double EPSILON = 1.0e-5;
  private static final Matrix Y_SHIFT_TRANSFORM = Matrix.translation(0, -Y_SHIFT, 0);

  @Override
  public Range3 getRange() {
    return Range3.create(-1, 1, -1, 1, -1, 1);
  }

  @Override
  public DoubleStream intersect(Ray ray) {
    Ray localRay = ray.transform(Y_SHIFT_TRANSFORM);
    return DoubleStream.concat(intersectWalls(localRay), intersectCap(localRay));
  }

  // Returns t's where ray intersects cone walls.
  // Returns empty stream if no intersection.
  public DoubleStream intersectWalls(Ray ray) {
    Tuple ro = ray.origin();
    Tuple rd = ray.direction();
    double a = rd.x() * rd.x() + rd.z() * rd.z() - RADIUS2 * rd.y() * rd.y();
    double b = 2.0 * (ro.x() * rd.x() + ro.z() * rd.z() - RADIUS2 * ro.y() * rd.y());
    double c = ro.x() * ro.x() + ro.z() * ro.z() - RADIUS2 * ro.y() * ro.y();
    double discriminant = b * b - 4.0 * a * c;

    if (discriminant < 0.0 || Math.abs(a) < EPSILON) {
      // Ray misses cone.
      return DoubleStream.empty();
    }
    double t1 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
    double t2 = (-b + Math.sqrt(discriminant)) / (2.0 * a);
    return DoubleStream.of(t1, t2).filter(t -> isYInRange(ray, t));
  }

  private boolean isYInRange(Ray ray, double t) {
    double y = ray.origin().y() + ray.direction().y() * t;
    return y > BOTTOM_Y && y <= TOP_Y;
  }

  // Returns t's where ray intersects cone cap.
  public DoubleStream intersectCap(Ray ray) {
    Optional<Double> t = intersectPlane(ray.origin().y(), ray.direction().y(), BOTTOM_Y);
    if (t.isPresent() && isPInCone(ray, t.get())) {
      return DoubleStream.of(t.get());
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

  private boolean isPInCone(Ray ray, double t) {
    Tuple point = ray.position(t);
    Tuple originToPoint = Tuple.vector(point.x(), 0, point.z());
    return originToPoint.dot(originToPoint) < RADIUS2;
  }

  @Override
  public Tuple normalAt(Tuple point) {
    double y = Y_SHIFT_TRANSFORM.times(point).y();
    if (Math.abs(y - BOTTOM_Y) < EPSILON) {
      return BOTTOM_NORMAL;
    }
    double normalY = Math.sqrt(point.x() * point.x() + point.z() * point.z());
    return Tuple.vector(point.x(), normalY, point.z());
  }
}
