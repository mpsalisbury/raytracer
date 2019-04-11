package raytracer;

import java.util.Objects;
import java.util.stream.Stream;

public class GeometryIntersectable implements Intersectable {

  private Geometry geometry;
  private Material material = Material.create();

  public GeometryIntersectable(Geometry geometry) {
    this.geometry = geometry;
  }

  public Material material() {
    return material;
  }

  public void setMaterial(Material m) {
    this.material = m;
  }

  @Override
  public Stream<MaterialIntersection> intersectStream(Ray ray) {
    return geometry
        .intersect(ray)
        .mapToObj(
            t -> {
              Tuple normalv = geometry.normalAt(ray.position(t));
              int shapeId = System.identityHashCode(GeometryIntersectable.this);
              return MaterialIntersection.create(ray, t, normalv, material, shapeId);
            });
  }

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
    GeometryIntersectable other = (GeometryIntersectable) obj;
    return this.geometry.equals(other.geometry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(geometry);
  }
}
