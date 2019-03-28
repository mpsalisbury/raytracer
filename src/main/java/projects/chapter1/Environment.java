package projects.chapter1;

import com.google.auto.value.AutoValue;
import raytracer.Tuple;

// Describes the physical environment for moving objects.
@AutoValue
public abstract class Environment {

  // @param gravity vector
  // @param wind vector
  public static Environment create(Tuple gravity, Tuple wind) {
    // todo assert gravity.isVector();
    // todo assert wind.isVector();
    return new AutoValue_Environment(gravity, wind);
  }

  public abstract Tuple gravity();

  public abstract Tuple wind();
}
