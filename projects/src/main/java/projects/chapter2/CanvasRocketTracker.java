package projects.chapter2;

import projects.chapter1.Environment;
import projects.chapter1.Projectile;
import raytracer.AppUtil;
import raytracer.Canvas;
import raytracer.Color;
import raytracer.Tuple;

public class CanvasRocketTracker {

  public static void main(String[] args) {
    // projectile starts one unit above the origin.
    // velocity is normalized to 1 unit/tick.
    Tuple startingPoint = Tuple.point(0, 1, 0);
    Tuple startingVelocity = Tuple.vector(1, 1.8, 0).normalize().times(11.25);
    Projectile projectile = Projectile.create(startingPoint, startingVelocity);

    // gravity -0.1 unit/tick, and wind is -0.01 unit/tick.
    Tuple gravity = Tuple.vector(0, -0.1, 0);
    Tuple wind = Tuple.vector(-0.01, 0, 0);
    Environment environment = Environment.create(gravity, wind);

    Canvas canvas = new Canvas(900, 550);
    trackProjectile(canvas, projectile, environment);

    AppUtil.saveCanvasToPng(canvas, "rocket");
  }

  private Canvas canvas;
  private Projectile projectile;
  private Environment environment;

  public static void trackProjectile(
      Canvas canvas, Projectile projectile, Environment environment) {
    CanvasRocketTracker tracker = new CanvasRocketTracker(canvas, projectile, environment);
    tracker.trackProjectile();
  }

  public CanvasRocketTracker(Canvas canvas, Projectile projectile, Environment environment) {
    this.canvas = canvas;
    this.projectile = projectile;
    this.environment = environment;
  }

  public void trackProjectile() {
    outputPosition();
    while (projectile.y() > 0) {
      projectile = projectile.tick(environment);
      outputPosition();
    }
  }

  public void outputPosition() {
    int x = (int) projectile.x();
    int y = canvas.height() - (int) projectile.y();
    canvas.setPixel(x, y, Color.WHITE);
  }
}
