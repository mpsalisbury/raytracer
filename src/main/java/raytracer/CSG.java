package raytracer;

import static java.util.Comparator.comparing;

import com.google.auto.value.AutoValue;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// A CSG combination of two shapes.
public class CSG implements Shape {

  public static CSG createUnion(Shape left, Shape right) {
    return new CSG(new UnionOperation(), left, right);
  }

  public static CSG createIntersection(Shape left, Shape right) {
    return new CSG(new IntersectionOperation(), left, right);
  }

  public static CSG createDifference(Shape left, Shape right) {
    return new CSG(new DifferenceOperation(), left, right);
  }

  // Wraps the combined shapes with single transform.
  private TransformedIntersectable transformed;

  private Operation operation;
  private Shape left;
  private Shape right;

  private CSG(Operation operation, Shape left, Shape right) {
    this.operation = operation;
    this.left = left;
    this.right = right;
    this.transformed = new TransformedIntersectable(new CSGIntersectable());
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
    // We'll give the material of the first shape.
    return left.material();
  }

  @Override
  public void setMaterial(Material m) {
    left.setMaterial(m);
    right.setMaterial(m);
  }

  @Override
  public Stream<MaterialIntersection> intersectStream(Ray ray) {
    return transformed.intersectStream(ray);
  }

  // Intersects a raw CSG combination.
  public class CSGIntersectable implements Intersectable {
    @Override
    public Stream<MaterialIntersection> intersectStream(Ray ray) {
      List<CSGIntersection> is =
          Stream.concat(
                  left.intersectStream(ray).map(i -> CSGIntersection.createLeft(i)),
                  right.intersectStream(ray).map(i -> CSGIntersection.createRight(i)))
              .sorted(comparing(i -> i.intersection().t()))
              .collect(Collectors.toList());
      Stream.Builder<MaterialIntersection> outputIntersections = Stream.builder();
      boolean inLeft = false;
      boolean inRight = false;
      for (CSGIntersection i : is) {
        if (operation.useIntersection(i.isLeft(), inLeft, inRight)) {
          outputIntersections.add(i.intersection());
        }
        if (i.isLeft()) {
          inLeft = !inLeft;
        } else {
          inRight = !inRight;
        }
      }
      return outputIntersections.build();
    }
  }

  // CSG combiner operation -- decides whether a given intersection is
  // part of the combined shape or not.
  private interface Operation {
    // Returns true if these characteristics of a source intersection should
    // produce an intersection in the combined shape or not.
    // @param isLeft is this intersection on the 'left' source shape.
    // @param inLeft is this intersection inside of the 'left' source shape.
    // @param inRight is this intersection inside of the 'right' source shape.
    boolean useIntersection(boolean isLeft, boolean inLeft, boolean inRight);
  }

  private static class UnionOperation implements Operation {
    @Override
    public boolean useIntersection(boolean isLeft, boolean inLeft, boolean inRight) {
      return (isLeft && !inRight) || (!isLeft && !inLeft);
    }
  }

  private static class IntersectionOperation implements Operation {
    @Override
    public boolean useIntersection(boolean isLeft, boolean inLeft, boolean inRight) {
      return (isLeft && inRight) || (!isLeft && inLeft);
    }
  }

  private static class DifferenceOperation implements Operation {
    @Override
    public boolean useIntersection(boolean isLeft, boolean inLeft, boolean inRight) {
      return (isLeft && !inRight) || (!isLeft && inLeft);
    }
  }

  // Attaches left/right to an intersection.
  @AutoValue
  public abstract static class CSGIntersection {
    public static CSGIntersection createLeft(MaterialIntersection i) {
      return new AutoValue_CSG_CSGIntersection(true, i);
    }

    public static CSGIntersection createRight(MaterialIntersection i) {
      return new AutoValue_CSG_CSGIntersection(false, i);
    }

    public abstract boolean isLeft();

    public abstract MaterialIntersection intersection();
  }
}
