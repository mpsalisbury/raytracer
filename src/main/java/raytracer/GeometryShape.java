package raytracer;

import java.util.stream.Stream;

public class GeometryShape implements Shape {

  private TransformedIntersectable transformed;
  private GeometryIntersectable geometry;

  public GeometryShape(Geometry geometry) {
    this.geometry = new GeometryIntersectable(geometry);
    this.transformed = new TransformedIntersectable(this.geometry);
  }

  // VisibleForTesting
  public Geometry geometry() {
    return geometry.geometry();
  }

  @Override
  public Matrix transform() {
    return transformed.transform();
  }

  @Override
  public void setTransform(Matrix transform) {
    transformed.setTransform(transform);
  }

  @Override
  public Material material() {
    return geometry.material();
  }

  @Override
  public void setMaterial(Material m) {
    geometry.setMaterial(m);
  }

  @Override
  public Stream<MaterialIntersection> intersectStream(Ray ray) {
    return transformed.intersectStream(ray);
  }
}
