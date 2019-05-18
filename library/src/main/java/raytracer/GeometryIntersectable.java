package raytracer;

import java.util.Objects;
import java.util.stream.Stream;

// An Intersectable form of a given Geometry.
public class GeometryIntersectable implements Intersectable {

  private Geometry geometry;
  private Material material = Material.create();

  public GeometryIntersectable(Geometry geometry) {
    this.geometry = geometry;
  }

  // VisibleForTesting
  public Geometry geometry() {
    return geometry;
  }

  public Material material() {
    return material;
  }

  public void setMaterial(Material m) {
    this.material = m;
  }

  @Override
  public BoundingBox boundingBox() {
    return geometry.getRange().createBoundingBox();
  }

  @Override
  public Stream<MaterialIntersection> intersectStream(Ray ray) {
    return geometry.intersectStream(ray).map(i -> i.copyWithMaterial(material));
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
