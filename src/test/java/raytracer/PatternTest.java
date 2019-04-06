package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.Color.BLACK;
import static raytracer.Color.WHITE;
import static raytracer.ColorSubject.assertThat;
import static raytracer.MatrixSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Patterns
public class PatternTest {

  // Pattern that returns point value as color.
  public static class TestPattern extends Pattern {
    @Override
    public Color localColorAt(Tuple p) {
      return Color.create(p.x(), p.y(), p.z());
    }
  }

  @Test
  // Scenario: A stripe pattern is constant in y
  public void stripeConstantInY() {
    Pattern pattern = Pattern.createStripe(WHITE, BLACK);
    assertThat(pattern.colorAt(Tuple.createPoint(0, 0, 0))).isEqualTo(WHITE);
    assertThat(pattern.colorAt(Tuple.createPoint(0, 1, 0))).isEqualTo(WHITE);
    assertThat(pattern.colorAt(Tuple.createPoint(0, 2, 0))).isEqualTo(WHITE);
  }

  @Test
  // Scenario: A stripe pattern is constant in z
  public void stripeConstantInZ() {
    Pattern pattern = Pattern.createStripe(WHITE, BLACK);
    assertThat(pattern.colorAt(Tuple.createPoint(0, 0, 0))).isEqualTo(WHITE);
    assertThat(pattern.colorAt(Tuple.createPoint(0, 0, 1))).isEqualTo(WHITE);
    assertThat(pattern.colorAt(Tuple.createPoint(0, 0, 2))).isEqualTo(WHITE);
  }

  @Test
  // Scenario: A stripe pattern alternates in x
  public void stripeAlternatesInX() {
    Pattern pattern = Pattern.createStripe(WHITE, BLACK);
    assertThat(pattern.colorAt(Tuple.createPoint(-1.1, 0, 0))).isEqualTo(WHITE);
    assertThat(pattern.colorAt(Tuple.createPoint(-1, 0, 0))).isEqualTo(BLACK);
    assertThat(pattern.colorAt(Tuple.createPoint(-0.1, 0, 0))).isEqualTo(BLACK);
    assertThat(pattern.colorAt(Tuple.createPoint(0, 0, 0))).isEqualTo(WHITE);
    assertThat(pattern.colorAt(Tuple.createPoint(0.9, 0, 0))).isEqualTo(WHITE);
    assertThat(pattern.colorAt(Tuple.createPoint(1, 0, 0))).isEqualTo(BLACK);
  }

  /*
  @Test
  // Scenario: Stripes with an object transformation
  public void stripeObjectTransform() {
    Shape object = new Sphere();
    object.setTransform(Matrix.scaling(2,2,2));
    Pattern pattern = Pattern.createStripe(WHITE, BLACK);
    assertThat(pattern.colorAt(Tuple.createPoint(1.5, 0, 0))).isEqualTo(BLACK);
  }
  */

  @Test
  // Scenario: Stripes with a pattern transformation
  public void stripePatternTransform() {
    Shape object = Sphere.create();
    Pattern pattern = Pattern.createStripe(WHITE, BLACK);
    pattern.setTransform(Matrix.scaling(2, 2, 2));
    assertThat(pattern.colorAt(Tuple.createPoint(1.5, 0, 0))).isEqualTo(WHITE);
  }

  @Test
  // Scenario: The default pattern transformation
  public void defaultPatternTransform() {
    Pattern pattern = Pattern.createColor(WHITE);
    assertThat(pattern.transform()).isApproximatelyEqualTo(Matrix.identity());
  }

  @Test
  // Scenario: Assigning a transformation
  public void assignTransform() {
    Pattern pattern = Pattern.createColor(WHITE);
    pattern.setTransform(Matrix.translation(1, 2, 3));
    assertThat(pattern.transform()).isApproximatelyEqualTo(Matrix.translation(1, 2, 3));
  }
}

/*
Scenario: Stripes with both an object and a pattern transformation
  Given object ← sphere()
    And set_transform(object, scaling(2, 2, 2))
    And pattern ← stripe_pattern(white, black)
    And set_pattern_transform(pattern, translation(0.5, 0, 0))
  When c ← stripe_at_object(pattern, object, point(2.5, 0, 0))
  Then c = white

Scenario: A pattern with an object transformation
  Given shape ← sphere()
    And set_transform(shape, scaling(2, 2, 2))
    And pattern ← test_pattern()
  When c ← pattern_at_shape(pattern, shape, point(2, 3, 4))
  Then c = color(1, 1.5, 2)

Scenario: A pattern with a pattern transformation
  Given shape ← sphere()
    And pattern ← test_pattern()
    And set_pattern_transform(pattern, scaling(2, 2, 2))
  When c ← pattern_at_shape(pattern, shape, point(2, 3, 4))
  Then c = color(1, 1.5, 2)

Scenario: A pattern with both an object and a pattern transformation
  Given shape ← sphere()
    And set_transform(shape, scaling(2, 2, 2))
    And pattern ← test_pattern()
    And set_pattern_transform(pattern, translation(0.5, 1, 1.5))
  When c ← pattern_at_shape(pattern, shape, point(2.5, 3, 3.5))
  Then c = color(0.75, 0.5, 0.25)

Scenario: A gradient linearly interpolates between colors
  Given pattern ← gradient_pattern(white, black)
  Then pattern_at(pattern, point(0, 0, 0)) = white
    And pattern_at(pattern, point(0.25, 0, 0)) = color(0.75, 0.75, 0.75)
    And pattern_at(pattern, point(0.5, 0, 0)) = color(0.5, 0.5, 0.5)
    And pattern_at(pattern, point(0.75, 0, 0)) = color(0.25, 0.25, 0.25)

Scenario: A ring should extend in both x and z
  Given pattern ← ring_pattern(white, black)
  Then pattern_at(pattern, point(0, 0, 0)) = white
    And pattern_at(pattern, point(1, 0, 0)) = black
    And pattern_at(pattern, point(0, 0, 1)) = black
    # 0.708 = just slightly more than √2/2
    And pattern_at(pattern, point(0.708, 0, 0.708)) = black

Scenario: Checkers should repeat in x
  Given pattern ← checkers_pattern(white, black)
  Then pattern_at(pattern, point(0, 0, 0)) = white
    And pattern_at(pattern, point(0.99, 0, 0)) = white
    And pattern_at(pattern, point(1.01, 0, 0)) = black

Scenario: Checkers should repeat in y
  Given pattern ← checkers_pattern(white, black)
  Then pattern_at(pattern, point(0, 0, 0)) = white
    And pattern_at(pattern, point(0, 0.99, 0)) = white
    And pattern_at(pattern, point(0, 1.01, 0)) = black

Scenario: Checkers should repeat in z
  Given pattern ← checkers_pattern(white, black)
  Then pattern_at(pattern, point(0, 0, 0)) = white
    And pattern_at(pattern, point(0, 0, 0.99)) = white
    And pattern_at(pattern, point(0, 0, 1.01)) = black
*/
