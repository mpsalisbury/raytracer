package raytracer;

import com.google.auto.value.AutoValue;

// Describes a Camera
@AutoValue
public abstract class Camera {

  public static Camera create(int hPixels, int vPixels, double fieldOfView) {
    return new AutoValue_Camera(hPixels, vPixels, fieldOfView, Matrix.identity());
  }

  public static Camera create(int hPixels, int vPixels, double fieldOfView, Matrix transform) {
    return new AutoValue_Camera(hPixels, vPixels, fieldOfView, transform);
  }

  public static Camera create(
      int hPixels, int vPixels, double fieldOfView, Tuple fromP, Tuple toP, Tuple upV) {
    return new AutoValue_Camera(
        hPixels, vPixels, fieldOfView, Matrix.viewTransform(fromP, toP, upV));
  }

  public abstract int hPixels();

  public abstract int vPixels();

  public abstract double fieldOfView();

  public abstract Matrix transform();

  public Canvas render(World world) {
    Canvas c = new Canvas(hPixels(), vPixels());
    c.forEachIndex(
        (x, y) -> {
          c.setPixel(x, y, world.colorAt(rayForPixel(x, y)));
        });
    return c;
  }

  public Ray rayForPixel(int x, int y) {
    Tuple cameraPos = Tuple.createPoint(0, 0, 0);
    Tuple filmPoint = filmPointForPixel(x, y);
    Tuple direction = filmPoint.minus(cameraPos).normalize();

    Matrix inverseTransform = transform().invert();
    return Ray.create(inverseTransform.times(cameraPos), inverseTransform.times(direction));
  }

  // TODO: correctly map to pixel centers
  // TODO: precompute constant factors, inverseTransform.
  // pixels [0..hPixels-1, 0..vPixels-1] map onto film of fieldOfView.
  // Film is at z=-1, width and height set by fieldOfView from camera at origin.
  public Tuple filmPointForPixel(int x, int y) {
    // film Z plane.
    final double PZ = -1.0;

    double filmSize = 2.0 * Math.tan(fieldOfView() / 2); // film size at distance 1.
    int maxPixels = Math.max(hPixels(), vPixels());
    double pixelScale = filmSize / (maxPixels - 1);

    double middleX = (hPixels() - 1) / 2.0;
    double pX = (x - middleX) * pixelScale;

    double middleY = (vPixels() - 1) / 2.0;
    double pY = ((vPixels() - 1 - y) - middleY) * pixelScale;

    return Tuple.createPoint(pX, pY, PZ);
  }
}
