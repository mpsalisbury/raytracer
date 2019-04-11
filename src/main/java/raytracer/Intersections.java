package raytracer;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Describes a collection of Intersections
public class Intersections {

  private List<Intersection> is;

  public static Intersections create(Stream<MaterialIntersection> inputIs) {
    return new Intersections(inputIs.map(i -> Intersection.create(i)));
  }

  public Intersections(Stream<Intersection> inputIs) {
    Stream<Intersection> sortedIs = inputIs.sorted(comparing(Intersection::t));
    this.is = propagateRefractiveIndices(sortedIs)
        .collect(Collectors.toList());
  }

  private static Stream<Intersection> propagateRefractiveIndices(Stream<Intersection> inputIs) {
    return new ProcessIntersections().propagateRefractiveIndices(inputIs);
  }

  private static class ProcessIntersections {
    // Ordered list of <shapeId, material> of current intersection along ray.
    // Each successive intersection either passes into a new shape or
    // out of an existing shape on the stack. With each transition we
    // will establish the in and out refractiveIndex in the intersection.
    LinkedList<Intersection> intersectionStack = new LinkedList<>();

    // Environment material for handling material nesting.
    private static final double ENVIRONMENT_REFRACTIVE_INDEX = Material.REFRACTIVE_INDEX_VACUUM;

    public double getLastRefractiveIndex() {
      if (intersectionStack.isEmpty()) {
        return ENVIRONMENT_REFRACTIVE_INDEX;
      } else {
        return intersectionStack.peekLast().material().refractiveIndex();
      }
    }

    public Stream<Intersection> propagateRefractiveIndices(Stream<Intersection> inputIs) {
      List<Intersection> is = inputIs.collect(Collectors.toList());

      Stream.Builder<Intersection> materialIs = Stream.builder();
      // TODO only continue if material is not opaque.
      // TODO prioritize nesting of shapes.
      for (Intersection i : is) {
        Optional<Intersection> matchedI =
            intersectionStack.stream().filter(si -> si.shapeId() == i.shapeId()).findAny();
        if (matchedI.isPresent()) {
          // if we already saw this shape, ray is exiting
          intersectionStack.remove(matchedI.get());
          double n1 = i.material().refractiveIndex();
          double n2 = getLastRefractiveIndex();
          materialIs.add(i.copyWithMaterials(n1, n2));
        } else {
          // if shape not already seen, ray is entering
          double n1 = getLastRefractiveIndex();
          double n2 = i.material().refractiveIndex();
          // This shape is now latest in stack.
          intersectionStack.add(i);
          materialIs.add(i.copyWithMaterials(n1, n2));
        }
      }
      return materialIs.build();
    }
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
        .filter(i -> i.material().castsShadow())
        .filter(i -> i.t() >= 0.0)
        .min(comparingDouble(Intersection::t));
  }

  // Returns intersections objects that will cast a shadow.
  public Stream<Intersection> shadowHits() {
    return is.stream().filter(i -> i.material().castsShadow()).filter(i -> i.t() >= 0.0);
  }
}
