package raytracer;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;

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

  @Memoized
  protected Matrix inverseTransform() {
    return transform().invert();
  }

  public Canvas render(World world) {
    Canvas c = new Canvas(hPixels(), vPixels());
    c.forEachIndex(
        (x, y) -> {
          c.setPixel(x, y, world.colorAt(rayForPixel(x, y)));
        });
    return c;
  }

  private static final Tuple CAMERA_POS = Tuple.point(0, 0, 0);

  public Ray rayForPixel(int x, int y) {
    Tuple filmPoint = filmPointForPixel(x, y);
    Tuple direction = filmPoint.minus(CAMERA_POS).normalize();

    return Ray.create(inverseTransform().times(CAMERA_POS), inverseTransform().times(direction));
  }

  // Returns how big each pixel should be in x/y-space units.
  @Memoized
  protected double pixelScale() {
    double filmSize = 2.0 * Math.tan(fieldOfView() / 2); // film size at distance 1.
    int maxPixels = Math.max(hPixels(), vPixels());
    double pixelScale = filmSize / (maxPixels - 1);
    return pixelScale;
  }

  // TODO: correctly map to pixel centers
  // pixels [0..hPixels-1, 0..vPixels-1] map onto film of fieldOfView.
  // Film is at z=-1, width and height set by fieldOfView from camera at origin.
  public Tuple filmPointForPixel(int x, int y) {
    // film Z plane.
    final double PZ = -1.0;
    final double pixelScale = pixelScale();
    final double middleX = (hPixels() - 1) / 2.0;
    final double middleY = (vPixels() - 1) / 2.0;

    double pX = ((hPixels() - 1 - x) - middleX) * pixelScale;
    double pY = ((vPixels() - 1 - y) - middleY) * pixelScale;

    return Tuple.point(pX, pY, PZ);
  }
}
