package raytracer;

import com.google.auto.value.AutoValue;

// Describes a Ray/Object intersection with material.
@AutoValue
public abstract class MaterialIntersection {

  public static MaterialIntersection create(
      Ray ray, double t, Tuple normalv, Material material, int shapeId) {
    return new AutoValue_MaterialIntersection(ray, t, normalv, material, shapeId);
  }

  // The ray fired at the geometry.
  public abstract Ray ray();

  // The t parameter where the ray hit the geometry.
  public abstract double t();

  // The normal at the hit point.
  public abstract Tuple normalv();

  // Material at this hit point.
  public abstract Material material();

  // Id of the shape that produced this intersection, used to couple with other intersections of the
  // same shape.
  // TODO: Use intersection ranges instead to avoid need for this.
  public abstract int shapeId();
}
