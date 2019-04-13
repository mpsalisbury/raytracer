package projects.chapter5;

import raytracer.AppUtil;
import raytracer.Canvas;
import raytracer.Color;
import raytracer.Matrix;
import raytracer.Ray;
import raytracer.Shape;
import raytracer.Sphere;
import raytracer.Tuple;

// Render the silhouette of a sphere.
public class Silhouette {

  public static void main(String[] args) {
    Matrix transform = Matrix.identity(); // Matrix.scaling(0.5, 1, 1).shear(1, 0, 0, 0, 0, 0);
    Canvas canvas = new Silhouette(transform).render();
    AppUtil.saveCanvasToPng(canvas, "silhouette");
  }

  // Sphere is (by default) at origin with unit radius.
  private final Shape sphere;
  // Camera is looking in +Z towards sphere with film behind it.

  // Z location of the camera.
  private final double cameraZ;
  // Z location of the film.
  private final double filmZ;
  // +/- X/Y size of the film.
  private final double filmSize;
  // Number of pixels in each dimension of film.
  private final int filmPixels;

  private Silhouette(Matrix transform) {
    Shape sphere = Sphere.create();
    sphere.setTransform(transform);
    this.sphere = sphere;
    this.cameraZ = -2;
    this.filmZ = 3;
    this.filmSize = 3;
    this.filmPixels = 200;
  }

  private Canvas render() {
    Canvas canvas = new Canvas(filmPixels, filmPixels);
    canvas.forEachIndex(
        (x, y) -> {
          Ray cameraRay = getCameraRayForPixel(x, y);
          boolean hit = sphere.intersect(cameraRay).hit().isPresent();
          if (hit) {
            canvas.setPixel(x, y, Color.RED);
          }
        });
    return canvas;
  }

  private Ray getCameraRayForPixel(int x, int y) {
    Tuple cameraPos = Tuple.point(0, 0, cameraZ);
    Tuple filmPos = Tuple.point(getFilmPos(x), getFilmPos(y), filmZ);
    return Ray.create(cameraPos, filmPos.minus(cameraPos).normalize());
  }

  // Compute x/y position of film given pixel index.
  private double getFilmPos(int x) {
    double minFilm = -filmSize;
    double maxFilm = filmSize;
    int minIndex = 0;
    int maxIndex = filmPixels - 1;
    return minFilm + (double) (x - minIndex) * (maxFilm - minFilm) / (maxIndex - minIndex);
  }
}
