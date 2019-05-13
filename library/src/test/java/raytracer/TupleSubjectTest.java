package raytracer;

import static com.google.common.truth.ExpectFailure.expectFailureAbout;
import static raytracer.TupleSubject.assertThat;
import static raytracer.TupleSubject.tuples;

import com.google.common.truth.ExpectFailure.SimpleSubjectBuilderCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class TupleSubjectTest {
  private static final double EPSILON = 0.001;
  private static final Tuple TUPLE = Tuple.create(0.1, 0.2, 0.3, 0.4);

  @Test
  public void approximatelyEqualTo() {
    assertThat(TUPLE).isApproximatelyEqualTo(EPSILON, Tuple.create(0.1001, 0.1999, 0.3001, 0.3999));

    expectFailure(
        whenTesting ->
            whenTesting
                .that(TUPLE)
                .isApproximatelyEqualTo(EPSILON, Tuple.create(0.11, 0.2, 0.3, 0.4)));
    expectFailure(
        whenTesting ->
            whenTesting
                .that(TUPLE)
                .isApproximatelyEqualTo(EPSILON, Tuple.create(0.1, 0.19, 0.3, 0.4)));
    expectFailure(
        whenTesting ->
            whenTesting
                .that(TUPLE)
                .isApproximatelyEqualTo(EPSILON, Tuple.create(0.1, 0.2, 0.31, 0.4)));
    expectFailure(
        whenTesting ->
            whenTesting
                .that(TUPLE)
                .isApproximatelyEqualTo(EPSILON, Tuple.create(0.1, 0.2, 0.3, 0.39)));
  }

  private static AssertionError expectFailure(
      SimpleSubjectBuilderCallback<TupleSubject, Tuple> callback) {
    return expectFailureAbout(tuples(), callback);
  }
}
