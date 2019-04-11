package raytracer;

import java.util.stream.Stream;

public class TransformedIntersectable implements Intersectable {
  private Intersectable inner;
  private Matrix transform = Matrix.identity();
  private Matrix inverseTransform = Matrix.identity();
  private Matrix inverseTranspose = Matrix.identity();

  public TransformedIntersectable(Intersectable inner) {
    this.inner = inner;
  }

  @Override
  public Stream<MaterialIntersection> intersectStream(Ray ray) {
    return inner.intersectStream(inverseTransformRay(ray)).map(i -> transformIntersection(i));
  }

  private Ray inverseTransformRay(Ray ray) {
    return ray.transform(inverseTransform);
  }

  private MaterialIntersection transformIntersection(MaterialIntersection intersection) {
    Ray transformedRay = intersection.ray().transform(transform);
    Tuple transformedNormalv = inverseTranspose.times(intersection.normalv()).normalize();

    return MaterialIntersection.create(
        transformedRay,
        intersection.t(),
        transformedNormalv,
        intersection.material(),
        intersection.shapeId());
  }

  public Matrix transform() {
    return transform;
  }

  public void setTransform(Matrix transform) {
    this.transform = transform;
    this.inverseTransform = transform.invert();
    this.inverseTranspose = inverseTransform.transpose().zeroRow(3);
  }
}
