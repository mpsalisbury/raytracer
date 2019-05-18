package raytracer;

import java.util.stream.Stream;

// Anything we can intersect with a Ray.
public interface Intersectable {
  default Intersections intersect(Ray ray) {
    return Intersections.create(intersectStream(ray));
  }

  // Returns true if given ray possibly hits this intersectable.
  // Returns false if it definitely does not hit.
  // Used for optimizing expensive intersection tests.
  default boolean maybeHits(Ray ray) {
    return boundingBox().maybeHits(ray);
  }

  // Returns a bounding box for this intersectable.
  BoundingBox boundingBox();

  // Returns an unordered stream of intersections with the given ray.
  Stream<MaterialIntersection> intersectStream(Ray ray);
}
