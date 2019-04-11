package raytracer;

// Describes a Ray/Object intersection.
// @AutoValue
public abstract class LocalIntersection {

  /*
  public static LocalIntersection create(Ray ray, double t, Tuple normalv, Shape shape) {
    return new AutoValue_LocalIntersection(ray, t, normalv, shape);
  }

  // Shape is lowest-level shape responsible for this intersection.
  public static LocalIntersection create(Ray ray, double t, Shape shape) {
    Tuple point = ray.position(t);
    Tuple normalv = shape.normalAt(point);
    return new AutoValue_LocalIntersection(ray, t, normalv, shape);
  }

  public abstract Ray ray();

  public abstract double t();

  public abstract Tuple normalv();

  public abstract Shape shape();
  */
}
