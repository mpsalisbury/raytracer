package raytracer;

import com.google.auto.value.AutoValue;

// Describes a Ray/Object intersection plus associated state.
@AutoValue
public abstract class Intersection {

  public static Intersection create(MaterialIntersection i) {
    return create(i.ray(), i.t(), i.normalv(), i.material(), i.shapeId());
  }

  // Visible for testing.
  public static Intersection create(
      Ray ray, double t, Tuple normalv, Material material, int shapeId) {
    Tuple point = ray.position(t);
    Tuple eyev = ray.direction().times(-1);
    boolean inside = false;
    if (normalv.dot(eyev) < 0.0) {
      inside = true;
      normalv = normalv.times(-1);
    }
    Tuple reflectv = ray.direction().reflect(normalv);

    // Default values for refraction. These are set in copyWithMaterials().
    double n1 = Material.REFRACTIVE_INDEX_VACUUM;
    double n2 = Material.REFRACTIVE_INDEX_VACUUM;
    boolean isTotalInternalReflection = false;
    Tuple refractv = Tuple.vector(0, 0, 1);
    double schlickReflectance = 0.0;

    return new AutoValue_Intersection(
        t,
        shapeId,
        point,
        eyev,
        normalv,
        inside,
        reflectv,
        material,
        n1,
        n2,
        isTotalInternalReflection,
        refractv,
        schlickReflectance);
  }

  // For testing only.
  public static Intersection create(double t) {
    return new AutoValue_Intersection(
        t,
        12345,
        Tuple.point(0, 0, 0),
        Tuple.vector(0, 0, 0),
        Tuple.vector(0, 0, 0),
        false,
        Tuple.vector(0, 0, 0),
        Material.create(),
        Material.REFRACTIVE_INDEX_VACUUM,
        Material.REFRACTIVE_INDEX_VACUUM,
        false,
        Tuple.vector(0, 0, 0),
        1.0);
  }

  // Sets n1 and n2 in copy of this Intersection.
  public Intersection copyWithMaterials(double n1, double n2) {
    // sinI / sinT = n2 / n1
    double nRatio = n1 / n2;
    double cosI = eyev().dot(normalv());
    double sin2T = nRatio * nRatio * (1.0 - cosI * cosI);
    boolean isTotalInternalReflection = sin2T > 1.0;

    double cosT = Math.sqrt(1.0 - sin2T);
    Tuple refractv = normalv().times(nRatio * cosI - cosT).minus(eyev().times(nRatio));

    double schlickReflectance = 0.0;
    if (isTotalInternalReflection) {
      schlickReflectance = 1.0;
    } else {
      double cosF = cosI;
      if (n1 > n2) {
        cosF = cosT;
      }
      double r0 = Math.pow((n1 - n2) / (n1 + n2), 2);
      schlickReflectance = r0 + (1 - r0) * Math.pow(1 - cosF, 5);
    }

    return new AutoValue_Intersection(
        t(),
        shapeId(),
        point(),
        eyev(),
        normalv(),
        inside(),
        reflectv(),
        material(),
        n1,
        n2,
        isTotalInternalReflection,
        refractv,
        schlickReflectance);
  }

  // The ray parameter at which this intersection occurs.
  public abstract double t();

  // Id for the shape causing this intersection.
  // Used to recognize entry and exit intersections for a single object.
  public abstract int shapeId();

  // The geometric point of the intersection.
  public abstract Tuple point();

  // The vector from the eye to the intersection.
  public abstract Tuple eyev();

  // The normal vector of the surface at the intersection.
  public abstract Tuple normalv();

  // Does the ray hit the inside of the surface.
  public abstract boolean inside();

  // The ray representing the reflection of the original ray off the surface.
  public abstract Tuple reflectv();

  // The material of the object at the intersection.
  public abstract Material material();

  // The refractivity index of the material before the intersection.
  public abstract double n1();

  // The refractivity index of the material after the intersection.
  public abstract double n2();

  // Is the ray trapped inside the material.
  public abstract boolean isTotalInternalReflection();

  // The ray representing the refraction of the original ray off the surface.
  public abstract Tuple refractv();

  // Reflectance calculation.
  public abstract double schlickReflectance();
}
