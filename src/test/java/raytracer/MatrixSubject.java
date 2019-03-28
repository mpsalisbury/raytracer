package raytracer;

// import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.Truth.assertAbout;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// TODO: test
public final class MatrixSubject extends Subject<MatrixSubject, Matrix> {

  public static MatrixSubject assertThat(@NullableDecl Matrix matrix) {
    return assertAbout(MATRIX_SUBJECT_FACTORY).that(matrix);
  }

  public static Subject.Factory<MatrixSubject, Matrix> matrices() {
    return MATRIX_SUBJECT_FACTORY;
  }

  private static final Subject.Factory<MatrixSubject, Matrix> MATRIX_SUBJECT_FACTORY =
      MatrixSubject::new;

  private MatrixSubject(FailureMetadata failureMetadata, @NullableDecl Matrix subject) {
    super(failureMetadata, subject);
  }

  private static final double EPSILON = 1.0e-5;

  public void isApproximatelyEqualTo(Matrix other) {
    isApproximatelyEqualTo(EPSILON, other);
  }

  public void isApproximatelyEqualTo(double epsilon, Matrix other) {
    check("numRows").that(actual().getNumRows()).isEqualTo(other.getNumRows());
    check("numCols").that(actual().getNumCols()).isEqualTo(other.getNumCols());
    other.forEachCell(
        (row, col) -> {
          check(String.format("cell(%d,%d)", row, col))
              .that(actual().get(row, col))
              .isWithin(epsilon)
              .of(other.get(row, col));
        });
  }
}
