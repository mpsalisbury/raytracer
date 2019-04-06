package raytracer;

import java.util.Objects;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public abstract class Shape {
  protected Matrix inverseTransform = Matrix.identity();
  private Material material = Material.create();

  // Returns degenerate Shape for testing.
  // TODO: move to testing.
  public static Shape createTest() {
    return new GeometryShape(new TestGeometry());
  }

  public Shape() { }

  public Matrix transform() {
    return inverseTransform.invert();
  }

  protected Matrix inverseTransform() {
    return inverseTransform;
  }

  public void setTransform(Matrix transform) {
    this.inverseTransform = transform.invert();
  }

  public Material material() {
    return material;
  }

  public void setMaterial(Material m) {
    this.material = m;
  }

  public Intersections intersect(Ray ray) {
    return new Intersections(intersectStream(ray));
  }

  public abstract Stream<Intersection> intersectStream(Ray ray);

  public abstract Tuple normalAt(Tuple point);

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    Shape other = (Shape) obj;
    return (this.inverseTransform.equals(other.inverseTransform))
        && (this.material.equals(other.material));
  }

  @Override
  public int hashCode() {
    return Objects.hash(inverseTransform, material);
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
