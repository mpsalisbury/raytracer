package raytracer;

import static com.google.common.truth.ExpectFailure.expectFailureAbout;
import static raytracer.ColorSubject.assertThat;
import static raytracer.ColorSubject.colors;

import com.google.common.truth.ExpectFailure.SimpleSubjectBuilderCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class ColorSubjectTest {
  private static final double EPSILON = 0.001;
  private static final Color COLOR = Color.create(0.1, 0.2, 0.3);

  @Test
  public void approximatelyEqualTo() {
    assertThat(COLOR).isApproximatelyEqualTo(EPSILON, Color.create(0.1001, 0.1999, 0.3001));

    expectFailure(
        whenTesting ->
            whenTesting.that(COLOR).isApproximatelyEqualTo(EPSILON, Color.create(0.11, 0.2, 0.3)));
    expectFailure(
        whenTesting ->
            whenTesting.that(COLOR).isApproximatelyEqualTo(EPSILON, Color.create(0.1, 0.19, 0.3)));
    expectFailure(
        whenTesting ->
            whenTesting.that(COLOR).isApproximatelyEqualTo(EPSILON, Color.create(0.1, 0.2, 0.31)));
  }

  private static AssertionError expectFailure(
      SimpleSubjectBuilderCallback<ColorSubject, Color> callback) {
    return expectFailureAbout(colors(), callback);
  }
}
