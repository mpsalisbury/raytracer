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

  // The location of this light source.
  public abstract Tuple position();

  // The color and intensity of this light source.
  public abstract Color intensity();
}
