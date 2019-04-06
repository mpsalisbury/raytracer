package raytracer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

// A collection of shapes.
public class Group extends Shape {

  private List<Shape> shapes = new ArrayList<>();

  public void add(Shape s) {
    shapes.add(s);
  }

  public List<Shape> shapes() {
    return shapes;
  }

  @Override
  public Stream<Intersection> intersectStream(Ray ray) {
    Ray transformedRay = ray.transform(inverseTransform);
    Stream<Intersection> intersections = Stream.empty();
    for (Shape s : shapes) {
      intersections = Stream.concat(intersections, s.intersectStream(transformedRay));
    }
    return intersections;
  }

  @Override
  public Tuple normalAt(Tuple point) {
    throw new UnsupportedOperationException();
  }
}
