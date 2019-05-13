package raytracer;

import com.google.auto.value.AutoValue;

// Describes a lightsource
@AutoValue
public abstract class Light {

  public static Light create(Tuple position, Color intensity) {
    if (!position.isPoint()) {
      throw new IllegalArgumentException("Position must be a point");
    }
    return new AutoValue_Light(position, intensity);
  }

  public abstract Tuple position();

  public abstract Color intensity();
}
