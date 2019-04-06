package raytracer;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import java.util.Optional;

// Describes a material
@AutoValue
public abstract class Material {

  public static final double REFRACTIVE_INDEX_VACUUM = 1.0;
  public static final double REFRACTIVE_INDEX_AIR = 1.00029;
  public static final double REFRACTIVE_INDEX_WATER = 1.333;
  public static final double REFRACTIVE_INDEX_GLASS = 1.52;
  public static final double REFRACTIVE_INDEX_DIAMOND = 2.417;

  private static final Pattern DEFAULT_PATTERN = Pattern.createColor(Color.WHITE);
  private static final double DEFAULT_AMBIENT = 0.1;
  private static final double DEFAULT_DIFFUSE = 0.9;
  private static final double DEFAULT_SPECULAR = 0.9;
  private static final double DEFAULT_SHININESS = 200.0;
  private static final double DEFAULT_REFLECTIVITY = 0.0;
  private static final double DEFAULT_TRANSPARENCY = 0.0;
  private static final double DEFAULT_REFRACTIVE_INDEX = REFRACTIVE_INDEX_VACUUM;
  private static final boolean DEFAULT_CASTS_SHADOW = true;

  public static Material create() {
    return AutoValue_Material.builder().build();
  }

  public static Builder builder() {
    return new AutoValue_Material.Builder();
  }

  public abstract Pattern pattern();

  public abstract double ambient();

  public abstract double diffuse();

  public abstract double specular();

  public abstract double shininess();

  public abstract double reflectivity();

  public abstract double transparency();

  public abstract double refractiveIndex();

  public abstract boolean castsShadow();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setPattern(Pattern pattern);

    public Builder setColor(Color color) {
      return setPattern(Pattern.createColor(color));
    }

    public abstract Builder setAmbient(double ambient);

    public abstract Builder setDiffuse(double diffuse);

    public abstract Builder setSpecular(double specular);

    public abstract Builder setShininess(double shininess);

    public abstract Builder setReflectivity(double reflectivity);

    public abstract Builder setTransparency(double transparency);

    public abstract Builder setRefractiveIndex(double refractiveIndex);

    public abstract Builder setCastsShadow(boolean castsShadow);

    abstract Optional<Pattern> pattern();

    abstract Optional<Double> ambient();

    abstract Optional<Double> diffuse();

    abstract Optional<Double> specular();

    abstract Optional<Double> shininess();

    abstract Optional<Double> reflectivity();

    abstract Optional<Double> transparency();

    abstract Optional<Double> refractiveIndex();
    abstract Optional<Boolean> castsShadow();

    abstract Material autoBuild();

    public Material build() {
      if (!pattern().isPresent()) {
        setPattern(DEFAULT_PATTERN);
      }
      if (!ambient().isPresent()) {
        setAmbient(DEFAULT_AMBIENT);
      }
      if (!diffuse().isPresent()) {
        setDiffuse(DEFAULT_DIFFUSE);
      }
      if (!specular().isPresent()) {
        setSpecular(DEFAULT_SPECULAR);
      }
      if (!shininess().isPresent()) {
        setShininess(DEFAULT_SHININESS);
      }
      if (!reflectivity().isPresent()) {
        setReflectivity(DEFAULT_REFLECTIVITY);
      }
      if (!transparency().isPresent()) {
        setTransparency(DEFAULT_TRANSPARENCY);
      }
      if (!refractiveIndex().isPresent()) {
        setRefractiveIndex(DEFAULT_REFRACTIVE_INDEX);
      }
      if (!castsShadow().isPresent()) {
        setCastsShadow(DEFAULT_CASTS_SHADOW);
      }

      Material material = autoBuild();
      Preconditions.checkState(material.ambient() >= 0.0, "Ambient must not be negative");
      Preconditions.checkState(material.diffuse() >= 0.0, "Diffuse must not be negative");
      Preconditions.checkState(material.specular() >= 0.0, "Specular must not be negative");
      Preconditions.checkState(material.shininess() >= 0.0, "Shininess must not be negative");
      Preconditions.checkState(
          material.reflectivity() >= 0.0 && material.reflectivity() <= 1.0,
          "Reflectivity must be between 0 and 1");
      Preconditions.checkState(
          material.transparency() >= 0.0 && material.transparency() <= 1.0,
          "Transparency must be between 0 and 1");
      Preconditions.checkState(
          material.refractiveIndex() >= 1.0, "Refractive Index be at least 1.0");
      return material;
    }
  }

  public Color lighting(Light light, Tuple point, Tuple eyev, Tuple normalv, Color visibleLightC) {
    // find the direction to the light source
    Tuple lightv = light.position().minus(point).normalize();

    Color ambient = pattern().colorAt(point).times(light.intensity()).times(ambient());
    if (visibleLightC == Color.BLACK) {
      return ambient;
    }

    Color diffuse = Color.BLACK;
    Color specular = Color.BLACK;

    // combine the surface color with the transmitted light's color/intensity
    Color effectiveColor = pattern().colorAt(point).times(visibleLightC);

    // light_dot_normal represents the cosine of the angle between the
    // light vector and the normal vector. A negative number means the
    // light is on the other side of the surface.
    double lightDotNormal = lightv.dot(normalv);
    if (lightDotNormal >= 0.0) {
      diffuse = effectiveColor.times(diffuse() * lightDotNormal);

      // reflect_dot_eye represents the cosine of the angle between the
      // reflection vector and the eye vector. A negative number means the
      // light reflects away from the eye.
      Tuple reflectv = lightv.times(-1).reflect(normalv);
      double reflectDotEye = reflectv.dot(eyev);

      if (reflectDotEye > 0.0) {
        double factor = Math.pow(reflectDotEye, shininess());
        specular = visibleLightC.times(specular() * factor);
        // specular = light.intensity().times(specular() * factor);
      }
    }
    // Add the three contributions together to get the final shading
    return ambient.plus(diffuse).plus(specular);
  }
}
