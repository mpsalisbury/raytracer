package raytracer;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;

// Describes a Point/Vector tuple.
@AutoValue
public abstract class Tuple {

  private static final double POINT_W = 1.0;
  private static final double VECTOR_W = 0.0;
  private static final Tuple ZERO = create(0, 0, 0, VECTOR_W);
  private static final Tuple UNIT_VERTICAL = vector(0, 1, 0);

  public static Tuple create(double x, double y, double z, double w) {
    return new AutoValue_Tuple(x, y, z, w);
  }

  public static Tuple point(double x, double y, double z) {
    return new AutoValue_Tuple(x, y, z, POINT_W);
  }

  public static Tuple vector(double x, double y, double z) {
    return new AutoValue_Tuple(x, y, z, VECTOR_W);
  }

  public abstract double x();

  public abstract double y();

  public abstract double z();

  public abstract double w();

  public boolean isPoint() {
    return w() == POINT_W;
  }

  public boolean isVector() {
    return w() == VECTOR_W;
  }

  public boolean isVertical() {
    return isVector() && Math.abs(dot(UNIT_VERTICAL)) == magnitude();
  }

  public boolean isHorizontal() {
    return isVector() && Math.abs(dot(UNIT_VERTICAL)) == 0;
  }

  public double magnitude() {
    Preconditions.checkState(isVector(), "magnitude() requires Tuple be a vector");
    return Math.sqrt(x() * x() + y() * y() + z() * z() + w() * w());
  }

  public Tuple normalize() {
    Preconditions.checkState(isVector(), "normalize() requires Tuple be a vector");
    return dividedBy(magnitude());
  }

  public Tuple negate() {
    return ZERO.minus(this);
  }

  public Tuple plus(Tuple t) {
    return Tuple.create(x() + t.x(), y() + t.y(), z() + t.z(), w() + t.w());
  }

  public Tuple minus(Tuple t) {
    return Tuple.create(x() - t.x(), y() - t.y(), z() - t.z(), w() - t.w());
  }

  public Tuple times(double f) {
    return Tuple.create(x() * f, y() * f, z() * f, w() * f);
  }

  public Tuple dividedBy(double f) {
    return Tuple.create(x() / f, y() / f, z() / f, w() / f);
  }

  public double dot(Tuple t) {
    return x() * t.x() + y() * t.y() + z() * t.z() + w() * t.w();
  }

  public Tuple cross(Tuple t) {
    Preconditions.checkState(isVector(), "cross() requires Tuple be a vector");
    Preconditions.checkArgument(t.isVector(), "cross(t) requires Tuple be a vector");
    return vector(y() * t.z() - z() * t.y(), z() * t.x() - x() * t.z(), x() * t.y() - y() * t.x());
  }

  public Tuple reflect(Tuple normal) {
    Preconditions.checkState(isVector(), "normal() requires Tuple be a vector");
    Preconditions.checkArgument(normal.isVector(), "reflect(normal) requires Tuple be a vector");
    return minus(normal.times(2.0 * dot(normal)));
  }
}
