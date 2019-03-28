package projects.chapter1;

import java.io.PrintStream;
import raytracer.Tuple;

public class TextRocketTracker {

  public static void main(String[] args) {
    // projectile starts one unit above the origin.
    // velocity is normalized to 1 unit/tick.
    Tuple startingPoint = Tuple.createPoint(0, 1, 0);
    Tuple startingVelocity = Tuple.createVector(1, 1, 0);
    Projectile p = Projectile.create(startingPoint, startingVelocity);

    // gravity -0.1 unit/tick, and wind is -0.01 unit/tick.
    Tuple gravity = Tuple.createVector(0, -0.1, 0);
    Tuple wind = Tuple.createVector(-0.01, 0, 0);
    Environment e = Environment.create(gravity, wind);

    trackProjectile(System.out, p, e);
  }

  private PrintStream out;
  private Projectile projectile;
  private Environment environment;

  public static void trackProjectile(
      PrintStream out, Projectile projectile, Environment environment) {
    TextRocketTracker tracker = new TextRocketTracker(out, projectile, environment);
    tracker.trackProjectile();
  }

  public TextRocketTracker(PrintStream out, Projectile projectile, Environment environment) {
    this.out = out;
    this.projectile = projectile;
    this.environment = environment;
  }

  public void trackProjectile() {
    printPosition();
    while (projectile.y() > 0) {
      projectile = projectile.tick(environment);
      printPosition();
    }
  }

  public void printPosition() {
    out.println(projectile.positionString());
  }
}
