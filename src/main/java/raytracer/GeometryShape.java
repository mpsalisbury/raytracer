package raytracer;

import java.util.Objects;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

// Shape build directly from Geometry.
public class GeometryShape extends Shape {
  private Geometry geometry;

  public GeometryShape(Geometry geometry) {
    this.geometry = geometry;
    setTransform(Matrix.identity());
  }

  @Override
  public void setTransform(Matrix transform) {
    super.setTransform(transform.times(geometry.baseTransform()));
  }

  @Override
  public Stream<Intersection> intersectStream(Ray ray) {
    return geometry.intersect(ray.transform(inverseTransform))
        .mapToObj(t -> Intersection.create(ray, t, this));
  }

  @Override
  public Tuple normalAt(Tuple point) {
    Tuple localPoint = inverseTransform.times(point);
    Tuple localNormal = geometry.normalAt(localPoint);
    Tuple worldNormal = inverseTransform.transpose().times(localNormal);
    // Hack -- w=0 and normalize. TODO: investigate p82.
    return Tuple.createVector(worldNormal.x(), worldNormal.y(), worldNormal.z()).normalize();
  }

  private static class TestGeometry extends Geometry {
    public DoubleStream intersect(Ray localRay) {
      return DoubleStream.empty();
    }

    public Tuple normalAt(Tuple point) {
      return Tuple.createVector(point.x(), point.y(), point.z());
    }
  }
}
