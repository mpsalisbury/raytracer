package raytracer;

import com.google.auto.value.AutoValue;

// Describes a Ray/Object intersection.
@AutoValue
public abstract class LocalIntersection {

  // Shape is lowest-level shape responsible for this intersection.
  public static LocalIntersection create(Ray ray, double t, Shape shape) {
    Tuple point = ray.position(t);
    Tuple normalv = shape.normalAt(point);
    return new AutoValue_LocalIntersection(
        ray,
        t,
        shape,
        normalv);
  }

  public abstract Ray ray();
  public abstract double t();
  public abstract Shape shape();
  public abstract Tuple normalv();
}
