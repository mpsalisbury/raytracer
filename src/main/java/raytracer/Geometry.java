package raytracer;

import java.util.Objects;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public abstract class Geometry {

  // Returns stream of t values where given ray intersects this shape.
  protected abstract DoubleStream intersect(Ray ray);

  public Stream<MaterialIntersection> intersectStream(Ray ray) {
    return intersect(ray)
        .mapToObj(
            t -> {
              Tuple normalv = normalAt(ray.position(t));
              Material material = null;
              int shapeId = System.identityHashCode(Geometry.this);
              return MaterialIntersection.create(ray, t, normalv, material, shapeId);
            });
  }

  // Returns the vector normal at the given point on this shape.
  public abstract Tuple normalAt(Tuple point);

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
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClass());
  }
}
