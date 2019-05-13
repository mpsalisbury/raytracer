package raytracer;

import java.util.Objects;

public abstract class Pattern {
  protected Matrix inverseTransform = Matrix.identity();

  public Color colorAt(Tuple p) {
    Tuple localPoint = inverseTransform.times(p);
    return localColorAt(inverseTransform.times(localPoint));
  }

  protected abstract Color localColorAt(Tuple p);

  public static Pattern createColor(Color color) {
    return new ColorPattern(color);
  }

  public static Pattern createStripe(Color colorA, Color colorB) {
    return new StripePattern(colorA, colorB);
  }

  public static PatternBuilder createStripeBuilder(Color colorA, Color colorB) {
    return new PatternBuilder(new StripePattern(colorA, colorB));
  }

  public static PatternBuilder createGradientBuilder(Color colorA, Color colorB) {
    return new PatternBuilder(new GradientPattern(colorA, colorB));
  }

  public static Pattern createRing(Color colorA, Color colorB) {
    return new RingPattern(colorA, colorB);
  }

  public static PatternBuilder createRingBuilder(Color colorA, Color colorB) {
    return new PatternBuilder(new RingPattern(colorA, colorB));
  }

  public static Pattern create3dChecker(Color colorA, Color colorB) {
    return new ThreeDCheckerPattern(colorA, colorB);
  }

  public static PatternBuilder create3dCheckerBuilder(Color colorA, Color colorB) {
    return new PatternBuilder(new ThreeDCheckerPattern(colorA, colorB));
  }

  public static Pattern createNoise(
      double noiseScale, double noiseMagnitude, Pattern sourcePattern) {
    return new NoisePattern(noiseScale, noiseMagnitude, sourcePattern);
  }

  public Matrix transform() {
    return inverseTransform.invert();
  }

  public void setTransform(Matrix transform) {
    this.inverseTransform = transform.invert();
  }

  // Helper for fluently building pattern objects.
  public static class PatternBuilder {
    private Pattern p;

    private PatternBuilder(Pattern p) {
      this.p = p;
    }

    public PatternBuilder setTransform(Matrix t) {
      p.setTransform(t);
      return this;
    }

    public Pattern build() {
      return p;
    }
  }

  // Pattern with a solid color.
  private static class ColorPattern extends Pattern {
    private Color color;

    public ColorPattern(Color color) {
      this.color = color;
    }

    @Override
    public Color localColorAt(Tuple p) {
      return color;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (obj == this) {
        return true;
      }
      if (obj.getClass() != getClass()) {
        return false;
      }
      ColorPattern other = (ColorPattern) obj;
      return this.inverseTransform.equals(other.inverseTransform) && this.color.equals(other.color);
    }

    @Override
    public int hashCode() {
      return Objects.hash(inverseTransform, color);
    }
  }

  // Abstract Pattern superclass using two colors.
  private abstract static class TwoColorPattern extends Pattern {
    private Color colorA;
    private Color colorB;
    // colorB - colorA.
    private Color colorDiff;

    public TwoColorPattern(Color colorA, Color colorB) {
      this.colorA = colorA;
      this.colorB = colorB;
      this.colorDiff = colorB.minus(colorA);
    }

    @Override
    public Color localColorAt(Tuple p) {
      double colorMix = colorMixAt(p);
      if (colorMix <= 0) {
        return colorA;
      }
      if (colorMix >= 1) {
        return colorB;
      }
      return colorA.plus(colorDiff.times(colorMix));
    }

    // Returns value from 0..1 representing colorA..colorB.
    public abstract double colorMixAt(Tuple p);

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (obj == this) {
        return true;
      }
      if (obj.getClass() != getClass()) {
        return false;
      }
      TwoColorPattern other = (TwoColorPattern) obj;
      return this.inverseTransform.equals(other.inverseTransform)
          && this.colorA.equals(other.colorA)
          && this.colorB.equals(other.colorB);
    }

    @Override
    public int hashCode() {
      return Objects.hash(inverseTransform, colorA, colorB);
    }
  }

  // Pattern alternating colors with X each unit.
  // [0..1) = ColorA
  // [1..2) = ColorB
  // Indefinitely extended in both directions.
  private static class StripePattern extends TwoColorPattern {
    public StripePattern(Color colorA, Color colorB) {
      super(colorA, colorB);
    }

    @Override
    public double colorMixAt(Tuple p) {
      boolean isEvenStripe = ((int) Math.floor(p.x())) % 2 == 0;
      return isEvenStripe ? 0 : 1;
    }
  }

  // Pattern phasing linearly from colorA to colorB and back each unit in X.
  private static class GradientPattern extends TwoColorPattern {
    public GradientPattern(Color colorA, Color colorB) {
      super(colorA, colorB);
    }

    @Override
    public double colorMixAt(Tuple p) {
      double saw = p.x() - 2.0 * Math.floor(p.x() / 2);
      return -Math.abs(-saw + 1) + 1;
    }
  }

  // Pattern alternating between colorA and colorB by unit distance from origin in X/Z.
  private static class RingPattern extends TwoColorPattern {
    public RingPattern(Color colorA, Color colorB) {
      super(colorA, colorB);
    }

    @Override
    public double colorMixAt(Tuple p) {
      double distance = Math.sqrt(p.x() * p.x() + p.z() * p.z());
      boolean isEvenStripe = ((int) Math.floor(distance)) % 2 == 0;
      return isEvenStripe ? 0 : 1;
    }
  }

  // Pattern alternating between colorA and colorB by 3d unit cubes.
  private static class ThreeDCheckerPattern extends TwoColorPattern {
    public ThreeDCheckerPattern(Color colorA, Color colorB) {
      super(colorA, colorB);
    }

    @Override
    public double colorMixAt(Tuple p) {
      int xInt = ((int) Math.floor(p.x()));
      int yInt = ((int) Math.floor(p.y()));
      int zInt = ((int) Math.floor(p.z()));
      return ((xInt + yInt + zInt) % 2 == 0) ? 0.0 : 1.0;
    }
  }

  private static class NoisePattern extends Pattern {
    private double noiseMagnitude;
    private Pattern sourcePattern;

    public NoisePattern(double noiseScale, double noiseMagnitude, Pattern sourcePattern) {
      this.setTransform(Matrix.scaling(noiseScale, noiseScale, noiseScale));
      this.noiseMagnitude = noiseMagnitude;
      this.sourcePattern = sourcePattern;
    }

    protected Color localColorAt(Tuple p) {
      Tuple noisyPoint =
          Tuple.point(
              p.x() + noiseMagnitude * noise(p.x(), p.y(), p.z()),
              p.y() + noiseMagnitude * noise(p.y(), p.z(), p.x()),
              p.z() + noiseMagnitude * noise(p.z(), p.x(), p.y()));
      return sourcePattern.colorAt(noisyPoint);
    }

    // Perlin noise function.
    // https://mrl.nyu.edu/~perlin/noise/
    private static double noise(double x, double y, double z) {
      int X = (int) Math.floor(x) & 255, // FIND UNIT CUBE THAT
          Y = (int) Math.floor(y) & 255, // CONTAINS POINT.
          Z = (int) Math.floor(z) & 255;
      x -= Math.floor(x); // FIND RELATIVE X,Y,Z
      y -= Math.floor(y); // OF POINT IN CUBE.
      z -= Math.floor(z);
      double u = fade(x), // COMPUTE FADE CURVES
          v = fade(y), // FOR EACH OF X,Y,Z.
          w = fade(z);
      int A = p[X] + Y,
          AA = p[A] + Z,
          AB = p[A + 1] + Z, // HASH COORDINATES OF
          B = p[X + 1] + Y,
          BA = p[B] + Z,
          BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

      return lerp(
          w,
          lerp(
              v,
              lerp(
                  u,
                  grad(p[AA], x, y, z), // AND ADD
                  grad(p[BA], x - 1, y, z)), // BLENDED
              lerp(
                  u,
                  grad(p[AB], x, y - 1, z), // RESULTS
                  grad(p[BB], x - 1, y - 1, z))), // FROM  8
          lerp(
              v,
              lerp(
                  u,
                  grad(p[AA + 1], x, y, z - 1), // CORNERS
                  grad(p[BA + 1], x - 1, y, z - 1)), // OF CUBE
              lerp(u, grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1))));
    }

    static double fade(double t) {
      return t * t * t * (t * (t * 6 - 15) + 10);
    }

    static double lerp(double t, double a, double b) {
      return a + t * (b - a);
    }

    static double grad(int hash, double x, double y, double z) {
      int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
      double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
          v = h < 4 ? y : h == 12 || h == 14 ? x : z;
      return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    static final int p[] = new int[512],
        permutation
        [] =
            {
              151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103,
              30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197,
              62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20,
              125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231,
              83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102,
              143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200,
              196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226,
              250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16,
              58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221,
              153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232,
              178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179,
              162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157,
              184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114,
              67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
            };

    static {
      for (int i = 0; i < 256; i++) p[256 + i] = p[i] = permutation[i];
    }
  }
}
