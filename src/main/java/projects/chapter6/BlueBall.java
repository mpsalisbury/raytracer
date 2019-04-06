package projects.chapter6;

import java.util.Optional;
import raytracer.AppUtil;
import raytracer.Canvas;
import raytracer.Color;
import raytracer.Intersection;
import raytracer.Light;
import raytracer.Material;
import raytracer.Matrix;
import raytracer.Ray;
import raytracer.Sphere;
import raytracer.Tuple;

// Render a sphere.
public class BlueBall {

  public static void main(String[] args) {
    Matrix transform = Matrix.identity();
    Canvas canvas = new BlueBall(transform).render();
    AppUtil.saveCanvasToPng(canvas, "blueball");
  }

  // Sphere is (by default) at origin with unit radius.
  private final Sphere shape;
  // Light source
  private final Light light = Light.create(Tuple.createPoint(-10, 10, -10), Color.WHITE);

  // Camera is looking in +Z towards sphere with film behind it.

  // Z location of the camera.
  private final double cameraZ;
  // Z location of the film.
  private final double filmZ;
  // +/- X/Y size of the film.
  private final double filmSize;
  // Number of pixels in each dimension of film.
  private final int filmPixels;

  private BlueBall(Matrix transform) {
    Sphere sphere = new Sphere();
    sphere.setTransform(transform);
    sphere.setMaterial(Material.builder().setColor(Color.create(1, 0.2, 1)).build());
    this.shape = sphere;
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
          Optional<Intersection> maybeHit = shape.intersect(cameraRay).hit();
          if (maybeHit.isPresent()) {
            Intersection hit = maybeHit.get();
            Tuple point = cameraRay.position(hit.t());
            Tuple normal = hit.normalv();
            Tuple eye = cameraRay.direction().times(-1);
            Color color = hit.material().lighting(light, point, eye, normal, Color.WHITE);
            canvas.setPixel(x, y, color);
          }
        });
    return canvas;
  }

  private Ray getCameraRayForPixel(int x, int y) {
    Tuple cameraPos = Tuple.createPoint(0, 0, cameraZ);
    Tuple filmPos = Tuple.createPoint(getFilmPos(x), getFilmPos(filmPixels - 1 - y), filmZ);
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
