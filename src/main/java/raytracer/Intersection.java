package raytracer;

import com.google.auto.value.AutoValue;

// Describes a Ray/Object intersection plus associated state.
@AutoValue
public abstract class Intersection {

  // Shape is lowest-level shape responsible for this intersection.
  public static Intersection create(Ray ray, double t, Shape shape) {
    Tuple point = ray.position(t);
    Tuple eyev = ray.direction().times(-1);
    Tuple normalv = shape.normalAt(point);
    boolean inside = false;
    if (normalv.dot(eyev) < 0.0) {
      inside = true;
      normalv = normalv.times(-1);
    }
    Tuple reflectv = ray.direction().reflect(normalv);
    Material material = shape.material();

    // Default values for refraction. These are set in copyWithMaterials().
    double n1 = Material.REFRACTIVE_INDEX_VACUUM;
    double n2 = Material.REFRACTIVE_INDEX_VACUUM;
    boolean isTotalInternalReflection = false;
    Tuple refractv = Tuple.createVector(0, 0, 1);
    double schlickReflectance = 0.0;

    return new AutoValue_Intersection(
        t,
        shape,
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
        Shape.createTest(),
        Tuple.createPoint(0, 0, 0),
        Tuple.createVector(0, 0, 0),
        Tuple.createVector(0, 0, 0),
        false,
        Tuple.createVector(0, 0, 0),
        Material.create(),
        Material.REFRACTIVE_INDEX_VACUUM,
        Material.REFRACTIVE_INDEX_VACUUM,
        false,
        Tuple.createVector(0, 0, 0),
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
        shape(),
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

  // Adds another transform to the existing intersection.
  public Intersection copyWithTransform(Matrix transform) {
    Matrix inverseTransform = transform.invert();
    Tuple newPoint = inverseTransform.times(point());
    // TODO invertVectors (transpose first?)
    Tuple newEyev = inverseTransform.times(eyev());
    Tuple newNormalv = inverseTransform.times(normalv());
    Tuple newReflectv = inverseTransform.times(reflectv());
    Tuple newRefractv = inverseTransform.times(refractv());

    return new AutoValue_Intersection(
        t(),
        shape(),
        newPoint,
        newEyev,
        newNormalv,
        inside(),
        newReflectv,
        material(),
        n1(),
        n2(),
        isTotalInternalReflection(),
        newRefractv,
        schlickReflectance());
  }

  public abstract double t();

  public abstract Shape shape();

  public abstract Tuple point();

  public abstract Tuple eyev();

  public abstract Tuple normalv();

  public abstract boolean inside();

  public abstract Tuple reflectv();

  public abstract Material material();

  public abstract double n1();

  public abstract double n2();

  public abstract boolean isTotalInternalReflection();

  public abstract Tuple refractv();

  public abstract double schlickReflectance();
}
