package raytracer;

import java.util.stream.Stream;

public class Shape implements Intersectable {

  private TransformedIntersectable transformed;
  private GeometryIntersectable geometry;

  public Shape(Geometry geometry) {
    this.geometry = new GeometryIntersectable(geometry);
    this.transformed = new TransformedIntersectable(this.geometry);
  }

  public Matrix transform() {
    return transformed.transform();
  }

  public void setTransform(Matrix transform) {
    transformed.setTransform(transform);
  }

  public Material material() {
    return geometry.material();
  }

  public void setMaterial(Material m) {
    geometry.setMaterial(m);
  }

  public Stream<MaterialIntersection> intersectStream(Ray ray) {
    return transformed.intersectStream(ray);
  }
}
