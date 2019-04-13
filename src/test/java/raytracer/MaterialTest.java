package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.ColorSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Materials
public class MaterialTest {

  @Test
  // Scenario: The default material
  public void constructMaterial() {
    Material m = Material.create();
    assertThat(m.pattern().colorAt(Tuple.point(0, 0, 0))).isEqualTo(Color.WHITE);
    assertThat(m.ambient()).isEqualTo(0.1);
    assertThat(m.diffuse()).isEqualTo(0.9);
    assertThat(m.specular()).isEqualTo(0.9);
    assertThat(m.shininess()).isEqualTo(200.0);
  }

  @Test
  // Scenario: Characteristics for the default material
  public void defaultCharacteristics() {
    Material m = Material.create();
    assertThat(m.reflectivity()).isEqualTo(0.0);
    assertThat(m.transparency()).isEqualTo(0.0);
    assertThat(m.refractiveIndex()).isEqualTo(1.0);
  }

  @Test
  // Scenario: Lighting with the eye between the light and the surface
  public void lightingEyeStraightLightStraight() {
    Material m = Material.create();
    Tuple position = Tuple.point(0, 0, 0);
    Tuple eyev = Tuple.vector(0, 0, -1);
    Tuple normalv = Tuple.vector(0, 0, -1);
    Light light = Light.create(Tuple.point(0, 0, -10), Color.WHITE);
    Color visibleLight = Color.WHITE;
    assertThat(m.lighting(light, position, eyev, normalv, visibleLight))
        .isEqualTo(Color.create(1.9, 1.9, 1.9));
  }

  @Test
  // Scenario: Lighting with the eye between light and surface, eye offset 45°
  public void lightingEye45LightStraight() {
    Material m = Material.create();
    Tuple position = Tuple.point(0, 0, 0);
    Tuple eyev = Tuple.vector(0, 1, -1).normalize();
    Tuple normalv = Tuple.vector(0, 0, -1);
    Light light = Light.create(Tuple.point(0, 0, -10), Color.WHITE);
    Color visibleLight = Color.WHITE;
    assertThat(m.lighting(light, position, eyev, normalv, visibleLight))
        .isEqualTo(Color.create(1.0, 1.0, 1.0));
  }

  @Test
  // Scenario: Lighting with eye opposite surface, light offset 45°
  public void lightingEyeStraightLight45() {
    Material m = Material.create();
    Tuple position = Tuple.point(0, 0, 0);
    Tuple eyev = Tuple.vector(0, 0, -1);
    Tuple normalv = Tuple.vector(0, 0, -1);
    Light light = Light.create(Tuple.point(0, 10, -10), Color.WHITE);
    Color visibleLight = Color.WHITE;
    assertThat(m.lighting(light, position, eyev, normalv, visibleLight))
        .isApproximatelyEqualTo(Color.create(0.7364, 0.7364, 0.7364));
  }

  @Test
  // Scenario: Lighting with eye in the path of the reflection vector
  public void lightingEye45LightOpposite45() {
    Material m = Material.create();
    Tuple position = Tuple.point(0, 0, 0);
    Tuple eyev = Tuple.vector(0, -1, -1).normalize();
    Tuple normalv = Tuple.vector(0, 0, -1);
    Light light = Light.create(Tuple.point(0, 10, -10), Color.WHITE);
    Color visibleLight = Color.WHITE;
    assertThat(m.lighting(light, position, eyev, normalv, visibleLight))
        .isApproximatelyEqualTo(Color.create(1.6364, 1.6364, 1.6364));
  }

  @Test
  // Scenario: Lighting with the light behind the surface
  public void lightingLightBehindSurface() {
    Material m = Material.create();
    Tuple position = Tuple.point(0, 0, 0);
    Tuple eyev = Tuple.vector(0, 0, -1);
    Tuple normalv = Tuple.vector(0, 0, -1);
    Light light = Light.create(Tuple.point(0, 0, 10), Color.WHITE);
    Color visibleLight = Color.WHITE;
    assertThat(m.lighting(light, position, eyev, normalv, visibleLight))
        .isApproximatelyEqualTo(Color.create(0.1, 0.1, 0.1));
  }

  @Test
  // Scenario: Lighting with the surface in shadow
  public void lightingInShadow() {
    Material m = Material.create();
    Tuple position = Tuple.point(0, 0, 0);
    Tuple eyev = Tuple.vector(0, 0, -1);
    Tuple normalv = Tuple.vector(0, 0, -1);
    Light light = Light.create(Tuple.point(0, 0, -10), Color.WHITE);
    Color visibleLight = Color.BLACK;
    assertThat(m.lighting(light, position, eyev, normalv, visibleLight))
        .isApproximatelyEqualTo(Color.create(0.1, 0.1, 0.1));
  }

  @Test
  // Scenario: Lighting with a pattern applied
  public void lightingPattern() {
    Material m =
        Material.builder()
            .setPattern(Pattern.createStripe(Color.WHITE, Color.BLACK))
            .setAmbient(1)
            .setDiffuse(0)
            .setSpecular(0)
            .build();
    Tuple eyev = Tuple.vector(0, 0, -1);
    Tuple normalv = Tuple.vector(0, 0, -1);
    Light light = Light.create(Tuple.point(0, 0, -10), Color.WHITE);
    Color visibleLight = Color.WHITE;
    assertThat(m.lighting(light, Tuple.point(0.9, 0, 0), eyev, normalv, visibleLight))
        .isEqualTo(Color.WHITE);
    assertThat(m.lighting(light, Tuple.point(1.1, 0, 0), eyev, normalv, visibleLight))
        .isEqualTo(Color.BLACK);
  }
}
