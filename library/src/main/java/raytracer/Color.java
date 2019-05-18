package raytracer;

import com.google.auto.value.AutoValue;

// Describes a Color, r,g,b channels 0..1.
@AutoValue
public abstract class Color {

  public static final Color BLACK = Color.create(0, 0, 0);
  public static final Color WHITE = Color.create(1, 1, 1);
  public static final Color RED = Color.create(1, 0, 0);
  public static final Color GREEN = Color.create(0, 1, 0);
  public static final Color BLUE = Color.create(0, 0, 1);

  public static Color create(double r, double g, double b) {
    return new AutoValue_Color(r, g, b);
  }

  public abstract double red();

  public abstract double green();

  public abstract double blue();

  // Add this color to the given color.
  public Color plus(Color c) {
    return Color.create(red() + c.red(), green() + c.green(), blue() + c.blue());
  }

  // Subtract this color from the given color.
  public Color minus(Color c) {
    return Color.create(red() - c.red(), green() - c.green(), blue() - c.blue());
  }

  // Multiply this color by the given factor.
  public Color times(double f) {
    return Color.create(red() * f, green() * f, blue() * f);
  }

  // Multiply this color by the given color channels.
  public Color times(Color c) {
    return Color.create(red() * c.red(), green() * c.green(), blue() * c.blue());
  }

  // Sqrt of color, hack to support transparent light code.
  // TODO: Remove when transparent lighting code (World.visibleLightColor) handles
  // light through material vs through two surfaces.
  public Color sqrt() {
    return Color.create(Math.sqrt(red()), Math.sqrt(green()), Math.sqrt(blue()));
  }
}
