package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.ColorSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Colors
public class ColorTest {

  @Test
  // Scenario: Colors are (red, green, blue) tuples
  public void constructColor() {
    Color c = Color.create(-0.5, 0.4, 1.7);
    assertThat(c.red()).isEqualTo(-0.5);
    assertThat(c.green()).isEqualTo(0.4);
    assertThat(c.blue()).isEqualTo(1.7);
  }

  @Test
  // Scenario: Adding colors
  public void addColors() {
    Color c1 = Color.create(0.9, 0.6, 0.75);
    Color c2 = Color.create(0.7, 0.1, 0.25);
    assertThat(c1.plus(c2)).isEqualTo(Color.create(1.6, 0.7, 1.0));
  }

  @Test
  // Scenario: Subtracting colors
  public void subtractColors() {
    Color c1 = Color.create(0.9, 0.6, 0.75);
    Color c2 = Color.create(0.7, 0.1, 0.25);
    assertThat(c1.minus(c2)).isApproximatelyEqualTo(Color.create(0.2, 0.5, 0.5));
  }

  @Test
  // Scenario: Multiplying a color by a scalar
  public void multiplyColorByScalar() {
    Color c = Color.create(0.2, 0.3, 0.4);
    assertThat(c.times(2)).isEqualTo(Color.create(0.4, 0.6, 0.8));
  }

  @Test
  // Scenario: Multiplying colors
  public void multiplyColors() {
    Color c1 = Color.create(1, 0.2, 0.4);
    Color c2 = Color.create(0.9, 1, 0.1);
    assertThat(c1.times(c2)).isApproximatelyEqualTo(Color.create(0.9, 0.2, 0.04));
  }
}
