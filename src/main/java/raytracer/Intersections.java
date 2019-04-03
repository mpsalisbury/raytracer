package raytracer;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Describes a collection of Intersections
public class Intersections {

  // Shape containing environment material.
  private static final Shape ENVIRONMENT_SHAPE = makeEnvironment();

  private static Shape makeEnvironment() {
    Shape env = new Sphere();
    env.setTransform(Matrix.scaling(100, 100, 100));
    return env;
  }

  private List<Intersection> is;

  public Intersections(Stream<Intersection> inputIs) {
    // Ordered list of shapes containing current intersection along ray.
    // Each successive intersection either passes into a new shape or
    // out of an existing shape on the stack. With each transition we
    // will establish the in and out refractiveIndex in the intersection.
    LinkedList<Shape> shapeStack = new LinkedList<>();
    shapeStack.add(ENVIRONMENT_SHAPE);
    List<Intersection> sortedIs =
        inputIs.sorted(comparing(Intersection::t)).collect(Collectors.toList());
    List<Intersection> materialIs = new ArrayList<>();
    // TODO only continue if material is not opaque.
    // TODO prioritize nesting of shapes.
    for (Intersection i : sortedIs) {
      if (shapeStack.contains(i.shape())) {
        // if we already saw this shape, ray is exiting
        shapeStack.remove(i.shape());
        Shape lastShape = shapeStack.peekLast();
        double n1 = i.shape().material().refractiveIndex();
        double n2 = lastShape.material().refractiveIndex();
        materialIs.add(i.copyWithMaterials(n1, n2));
      } else {
        // if shape not already seen, ray is entering
        Shape lastShape = shapeStack.peekLast();
        double n1 = lastShape.material().refractiveIndex();
        double n2 = i.shape().material().refractiveIndex();
        // This shape is now latest in stack.
        shapeStack.add(i.shape());
        materialIs.add(i.copyWithMaterials(n1, n2));
      }
    }
    ;
    this.is = materialIs;
  }

  public Intersections(Intersection... is) {
    this(Arrays.stream(is));
  }

  public int length() {
    return is.size();
  }

  public Intersection get(int i) {
    return is.get(i);
  }

  public Stream<Intersection> all() {
    return is.stream();
  }

  public Optional<Intersection> hit() {
    return is.stream().filter(i -> i.t() >= 0.0).min(comparingDouble(Intersection::t));
  }

  // Returns intersection of closest object t>0 that will cast a shadow.
  public Optional<Intersection> shadowHit() {
    return is.stream()
        .filter(i -> i.shape().castsShadow())
        .filter(i -> i.t() >= 0.0)
        .min(comparingDouble(Intersection::t));
  }

  // Returns intersections objects that will cast a shadow.
  public Stream<Intersection> shadowHits() {
    return is.stream().filter(i -> i.shape().castsShadow()).filter(i -> i.t() >= 0.0);
  }
}
