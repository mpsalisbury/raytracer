package raytracer;

import java.util.Objects;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public abstract class Shape {
  protected Matrix inverseTransform;
  private Material material = Material.create();
  private boolean castsShadow = true;

  // Returns degenerate Shape for testing.
  // TODO: move to testing.
  public static Shape createTest() {
    return new TestShape();
  }

  protected Shape() {
    setTransform(Matrix.identity());
  }

  public Matrix transform() {
    return inverseTransform.invert();
  }

  protected Matrix inverseTransform() {
    return inverseTransform;
  }

  // Transform added to requested transform.
  protected Matrix startingTransform() {
    return Matrix.identity();
  }

  public void setTransform(Matrix transform) {
    this.inverseTransform = transform.times(startingTransform()).invert();
  }

  public Material material() {
    return material;
  }

  public void setMaterial(Material m) {
    this.material = m;
  }

  public boolean castsShadow() {
    return castsShadow;
  }

  public void setCastsShadow(boolean castsShadow) {
    this.castsShadow = castsShadow;
  }

  public Intersections intersect(Ray ray) {
    return new Intersections(intersectStream(ray));
  }

  public Stream<Intersection> intersectStream(Ray ray) {
    return localIntersect(ray.transform(inverseTransform))
        .mapToObj(t -> Intersection.create(ray, t, this));
  }

  // Returns stream of t values where given ray intersects this shape.
  public abstract DoubleStream localIntersect(Ray localRay);

  public Tuple normalAt(Tuple point) {
    Tuple localPoint = inverseTransform.times(point);
    Tuple localNormal = localNormalAt(localPoint);
    Tuple worldNormal = inverseTransform.transpose().times(localNormal);
    // Hack -- w=0 and normalize. TODO: investigate p82.
    return Tuple.createVector(worldNormal.x(), worldNormal.y(), worldNormal.z()).normalize();
  }

  // Returns the vector normal at the given point on this shape.
  public abstract Tuple localNormalAt(Tuple point);

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

  private static class TestShape extends Shape {
    public DoubleStream localIntersect(Ray localRay) {
      return DoubleStream.empty();
    }

    public Tuple localNormalAt(Tuple point) {
      return Tuple.createVector(point.x(), point.y(), point.z());
    }
  }
}
