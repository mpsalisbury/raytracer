package raytracer;

public interface BoundingBox {
  // Might the given ray hit this bounding box? False === no. True === maybe.
  // Used for optimizing expensive intersection tests.
  boolean maybeHits(Ray ray);

  // Range covered by this bounding box.
  // Used to combine bounding boxes.
  Range3 getRange();

  // Returns a bounding box bounding this bounding box transformed by the given transform.
  BoundingBox transform(Matrix transform);
}
