package raytracer;

import static com.google.common.truth.Truth.assertAbout;
import static raytracer.Testing.EPSILON;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class Range3Subject extends Subject<Range3Subject, Range3> {

  public static Range3Subject assertThat(@NullableDecl Range3 range) {
    return assertAbout(RANGE3_SUBJECT_FACTORY).that(range);
  }

  public static Subject.Factory<Range3Subject, Range3> range3s() {
    return RANGE3_SUBJECT_FACTORY;
  }

  private static final Subject.Factory<Range3Subject, Range3> RANGE3_SUBJECT_FACTORY =
      Range3Subject::new;

  private Range3Subject(FailureMetadata failureMetadata, @NullableDecl Range3 subject) {
    super(failureMetadata, subject);
  }

  public void isApproximatelyEqualTo(Range3 other) {
    isApproximatelyEqualTo(EPSILON, other);
  }

  public void isApproximatelyEqualTo(double epsilon, Range3 other) {
    check("minPoint()")
        .about(TupleSubject.tuples())
        .that(actual().getMinPoint_TESTING())
        .isApproximatelyEqualTo(epsilon, other.getMinPoint_TESTING());
    check("maxPoint()")
        .about(TupleSubject.tuples())
        .that(actual().getMaxPoint_TESTING())
        .isApproximatelyEqualTo(epsilon, other.getMaxPoint_TESTING());
  }
}
