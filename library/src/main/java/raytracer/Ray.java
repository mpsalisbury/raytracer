package raytracer;

import com.google.auto.value.AutoValue;

// Describes a Ray
@AutoValue
public abstract class Ray {

  private static final double EPSILON = 1.0e-5;

  public static Ray create(Tuple origin, Tuple direction) {
    if (!origin.isPoint()) {
      throw new IllegalArgumentException("Origin must be a point " + origin);
    }
    if (!direction.isVector()) {
      throw new IllegalArgumentException("Direction must be a vector " + direction);
    }
    return new AutoValue_Ray(origin, direction);
  }

  public abstract Tuple origin();

  public abstract Tuple direction();

  // Returns the point along this ray at parameter t.
  public Tuple position(double t) {
    return origin().plus(direction().times(t));
  }

  // Returns this ray transformed by the given matrix.
  public Ray transform(Matrix m) {
    return create(m.times(origin()), m.times(direction()));
  }

  // Bump ray starting point EPSILON along direction in order to
  // escape intersections just under surface due to precision noise.
  public Ray bumpForward() {
    return create(origin().plus(direction().times(EPSILON)), direction());
  }
}
