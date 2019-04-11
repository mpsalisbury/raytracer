package raytracer;

import java.util.stream.Stream;

public interface Intersectable {
  default Intersections intersect(Ray ray) {
    return Intersections.create(intersectStream(ray));
  }

  // Returns an unordered stream of intersections with the given ray.
  public Stream<MaterialIntersection> intersectStream(Ray ray);
}
