package raytracer;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Lights
public class LightTest {

  @Test
  // Scenario: A point light has a position and intensity
  public void constructLight() {
    Tuple position = Tuple.point(0, 0, 0);
    Color intensity = Color.WHITE;
    Light light = Light.create(position, intensity);
    assertThat(light.position()).isEqualTo(position);
    assertThat(light.intensity()).isEqualTo(intensity);
  }
}
