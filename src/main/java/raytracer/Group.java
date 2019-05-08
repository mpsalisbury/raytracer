package raytracer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// A collection of shapes.
public class Group implements Shape {

  // Wraps group of shapes with single transform.
  private TransformedIntersectable transformed;
  private List<Shape> shapes = new ArrayList<>();
  // Cached version of box bounding entire group.
  private BoundingBox boundingBox = null;

  public static Group create() {
    return new Group();
  }

  private Group() {
    transformed = new TransformedIntersectable(new GroupIntersectable());
  }

  public void add(Shape s) {
    shapes.add(s);
    boundingBox = null;
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
    boundingBox = null;
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

  // Resets the computed bounding box.
  // To be used when a contained shape changes its bounding box.
  public void resetBoundingBox() {
    boundingBox = null;
  }

  @Override
  public BoundingBox boundingBox() {
    if (boundingBox == null) {
      if (shapes.size() < 5) {
        // With few enough shapes, bounding box calculation is overkill.
        return Range3.createUnbounded().createBoundingBox();
      }
      Range3 range = Range3.createEmpty();
      for (Intersectable shape : shapes) {
        range = range.span(shape.boundingBox().getRange());
      }
      boundingBox = range.createBoundingBox();
    }
    return boundingBox;
  }

  // Intersects a raw collection of shapes.
  public class GroupIntersectable implements Intersectable {
    @Override
    public BoundingBox boundingBox() {
      return Group.this.boundingBox();
    }

    @Override
    public Stream<MaterialIntersection> intersectStream(Ray ray) {
      Stream<MaterialIntersection> intersections = Stream.empty();
      if (!boundingBox().maybeHits(ray)) {
        return intersections;
      }
      for (Intersectable s : shapes) {
        intersections = Stream.concat(intersections, s.intersectStream(ray));
      }
      return intersections;
    }
  }
}
