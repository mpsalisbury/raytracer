package raytracer;

import java.util.stream.DoubleStream;

// Sphere centered at origin with radius 1.
public class Sphere extends Geometry {

  public static Shape create() {
    return new Shape(new Sphere());
  }

  public static Shape createGlass() {
    Shape sphere = Sphere.create();
    sphere.setMaterial(
        Material.builder()
            .setTransparency(1.0)
            .setRefractiveIndex(Material.REFRACTIVE_INDEX_GLASS)
            .build());
    return sphere;
  }

  private static final Tuple CENTER = Tuple.createPoint(0, 0, 0);

  @Override
  public DoubleStream intersect(Ray ray) {
    Tuple sphereToRay = ray.origin().minus(CENTER);
    double a = ray.direction().dot(ray.direction());
    double b = 2.0 * ray.direction().dot(sphereToRay);
    double c = sphereToRay.dot(sphereToRay) - 1.0;
    double discriminant = b * b - 4.0 * a * c;

    if (discriminant < 0.0) {
      // Ray misses sphere.
      return DoubleStream.empty();
    }
    double t1 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
    double t2 = (-b + Math.sqrt(discriminant)) / (2.0 * a);
    return DoubleStream.of(t1, t2);
  }

  @Override
  public Tuple normalAt(Tuple point) {
    return point.minus(CENTER);
  }
}
