package raytracer;

import static com.google.common.truth.Truth.assertAbout;
import static raytracer.Testing.EPSILON;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class TupleSubject extends Subject<TupleSubject, Tuple> {

  public static TupleSubject assertThat(@NullableDecl Tuple tuple) {
    return assertAbout(TUPLE_SUBJECT_FACTORY).that(tuple);
  }

  public static Subject.Factory<TupleSubject, Tuple> tuples() {
    return TUPLE_SUBJECT_FACTORY;
  }

  private static final Subject.Factory<TupleSubject, Tuple> TUPLE_SUBJECT_FACTORY =
      TupleSubject::new;

  private TupleSubject(FailureMetadata failureMetadata, @NullableDecl Tuple subject) {
    super(failureMetadata, subject);
  }

  public void isApproximatelyEqualTo(Tuple other) {
    isApproximatelyEqualTo(EPSILON, other);
  }

  public void isApproximatelyEqualTo(double epsilon, Tuple other) {
    check("x()").that(actual().x()).isWithin(epsilon).of(other.x());
    check("y()").that(actual().y()).isWithin(epsilon).of(other.y());
    check("z()").that(actual().z()).isWithin(epsilon).of(other.z());
    check("w()").that(actual().w()).isWithin(epsilon).of(other.w());
  }
}
