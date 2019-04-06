package raytracer;

import java.util.Objects;
import java.util.stream.DoubleStream;

public abstract class Geometry {
  
  public Matrix baseTransform() {
    return Matrix.identity();
  }

  // Returns stream of t values where given ray intersects this shape.
  public abstract DoubleStream intersect(Ray localRay);

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
