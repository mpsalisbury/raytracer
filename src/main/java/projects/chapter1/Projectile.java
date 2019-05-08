package projects.chapter1;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import raytracer.Tuple;

// Describes a physical projectile
@AutoValue
public abstract class Projectile {

  // @param position point
  // @param velocity vector
  public static Projectile create(Tuple position, Tuple velocity) {
    Preconditions.checkArgument(position.isPoint(), "position must be a vector");
    Preconditions.checkArgument(velocity.isVector(), "velocity must be a vector");
    return new AutoValue_Projectile(position, velocity);
  }

  public abstract Tuple position();

  public abstract Tuple velocity();

  public double x() {
    return position().x();
  }

  public double y() {
    return position().y();
  }

  public String positionString() {
    return String.format("%6.2f %6.2f", x(), y());
  }

  public Projectile tick(Environment environment) {
    return create(
        position().plus(velocity()),
        velocity().plus(environment.gravity()).plus(environment.wind()));
  }
}
