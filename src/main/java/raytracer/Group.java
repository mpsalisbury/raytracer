package raytracer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// A collection of shapes.
public class Group implements Intersectable {

  // Wraps group of shapes with single transform.
  private TransformedIntersectable transformed;
  private List<Intersectable> shapes = new ArrayList<>();

  public Group() {
    transformed = new TransformedIntersectable(new GroupIntersectable());
  }

  public void add(Intersectable s) {
    shapes.add(s);
  }

  public List<Intersectable> shapes() {
    return shapes;
  }

  public Matrix transform() {
    return transformed.transform();
  }

  public void setTransform(Matrix transform) {
    transformed.setTransform(transform);
  }

  @Override
  public Stream<MaterialIntersection> intersectStream(Ray ray) {
    return transformed.intersectStream(ray);
  }

  // Intersects a raw collection of shapes.
  public class GroupIntersectable implements Intersectable {
    @Override
    public Stream<MaterialIntersection> intersectStream(Ray ray) {
      Stream<MaterialIntersection> intersections = Stream.empty();
      for (Intersectable s : shapes) {
        intersections = Stream.concat(intersections, s.intersectStream(ray));
      }
      return intersections;
    }
  }
}
