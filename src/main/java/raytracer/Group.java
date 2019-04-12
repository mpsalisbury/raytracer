package raytracer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// A collection of shapes.
public class Group implements Shape {

  // Wraps group of shapes with single transform.
  private TransformedIntersectable transformed;
  private List<Shape> shapes = new ArrayList<>();

  public static Group create() {
    return new Group();
  }

  private Group() {
    transformed = new TransformedIntersectable(new GroupIntersectable());
  }

  public void add(Shape s) {
    shapes.add(s);
  }

  public List<Shape> shapes() {
    return shapes;
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
    // Seems silly to ask for the material of a group.
    // We'll give the material of the first shape.
    if (shapes.isEmpty()) {
      return Material.create();
    } else {
      return shapes.get(0).material();
    }
  }

  @Override
  public void setMaterial(Material m) {
    for (Shape s : shapes) {
      s.setMaterial(m);
    }
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
