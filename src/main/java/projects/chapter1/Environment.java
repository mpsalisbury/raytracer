package projects.chapter1;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import raytracer.Tuple;

// Describes the physical environment for moving objects.
@AutoValue
public abstract class Environment {

  // @param gravity vector
  // @param wind vector
  public static Environment create(Tuple gravity, Tuple wind) {
    Preconditions.checkArgument(gravity.isVector(), "gravity must be a vector");
    Preconditions.checkArgument(wind.isVector(), "wind must be a vector");
    return new AutoValue_Environment(gravity, wind);
  }

  public abstract Tuple gravity();

  public abstract Tuple wind();
}
