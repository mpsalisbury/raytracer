package raytracer;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.stream.DoubleStream;

// Cube centered at origin with side length 2.
public class Cube extends Shape {

  // Normals in order +x, -x, +y, -y, +z, -z.
  private static final List<Tuple> NORMALS =
      Lists.newArrayList(
          Tuple.createVector(1, 0, 0),
          Tuple.createVector(-1, 0, 0),
          Tuple.createVector(0, 1, 0),
          Tuple.createVector(0, -1, 0),
          Tuple.createVector(0, 0, 1),
          Tuple.createVector(0, 0, -1));
  private static final double EPSILON = 1.0e-5;

  @Override
  public DoubleStream localIntersect(Ray ray) {
    List<Double> xRange =
        Lists.newArrayList(
            intersectPlane(ray.origin().x(), ray.direction().x(), 1),
            intersectPlane(ray.origin().x(), ray.direction().x(), -1));
    List<Double> yRange =
        Lists.newArrayList(
            intersectPlane(ray.origin().y(), ray.direction().y(), 1),
            intersectPlane(ray.origin().y(), ray.direction().y(), -1));
    List<Double> zRange =
        Lists.newArrayList(
            intersectPlane(ray.origin().z(), ray.direction().z(), 1),
            intersectPlane(ray.origin().z(), ray.direction().z(), -1));
    return intersectRanges(Lists.newArrayList(xRange, yRange, zRange));
  }

  public double intersectPlane(double origin, double direction, double plane) {
    double target = plane - origin;
    if (Math.abs(direction) >= EPSILON) {
      return target / direction;
    } else {
      return target * Double.POSITIVE_INFINITY;
    }
  }

  public DoubleStream intersectRanges(List<List<Double>> ranges) {
    DoubleStream mins =
        ranges
            .stream()
            .mapToDouble(r -> r.stream().mapToDouble(Double::doubleValue).min().getAsDouble());
    DoubleStream maxs =
        ranges
            .stream()
            .mapToDouble(r -> r.stream().mapToDouble(Double::doubleValue).max().getAsDouble());
    double maxMin = mins.max().getAsDouble();
    double minMax = maxs.min().getAsDouble();
    if (maxMin < minMax) {
      return DoubleStream.of(maxMin, minMax);
    } else {
      return DoubleStream.empty();
    }
  }

  @Override
  public Tuple localNormalAt(Tuple point) {
    // Return normal for whichever point
    int maxIndex = maxIndex(point.x(), -point.x(), point.y(), -point.y(), point.z(), -point.z());
    return NORMALS.get(maxIndex);
  }

  // Return (first) index of max value within values.
  private int maxIndex(double... values) {
    double max = Doubles.max(values);
    return Doubles.indexOf(values, max);
  }
}
