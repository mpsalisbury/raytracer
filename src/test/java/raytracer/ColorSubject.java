package raytracer;

// import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.Truth.assertAbout;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class ColorSubject extends Subject<ColorSubject, Color> {

  public static ColorSubject assertThat(@NullableDecl Color color) {
    return assertAbout(COLOR_SUBJECT_FACTORY).that(color);
  }

  public static Subject.Factory<ColorSubject, Color> colors() {
    return COLOR_SUBJECT_FACTORY;
  }

  private static final Subject.Factory<ColorSubject, Color> COLOR_SUBJECT_FACTORY =
      ColorSubject::new;

  private ColorSubject(FailureMetadata failureMetadata, @NullableDecl Color subject) {
    super(failureMetadata, subject);
  }

  private static final double EPSILON = 1.0e-5;

  public void isApproximatelyEqualTo(Color other) {
    isApproximatelyEqualTo(EPSILON, other);
  }

  public void isApproximatelyEqualTo(double epsilon, Color other) {
    check("red()").that(actual().red()).isWithin(epsilon).of(other.red());
    check("green()").that(actual().green()).isWithin(epsilon).of(other.green());
    check("blue()").that(actual().blue()).isWithin(epsilon).of(other.blue());
  }
}
