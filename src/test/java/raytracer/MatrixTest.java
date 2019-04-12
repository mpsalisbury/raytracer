package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.MatrixSubject.assertThat;
import static raytracer.Testing.EPSILON;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Matrices
public class MatrixTest {

  @Test
  // Scenario: Constructing and inspecting a 4x4 matrix
  public void construct4x4Matrix() {
    Matrix m =
        Matrix.create(
            4,
            4,
            new double[] {1, 2, 3, 4, 5.5, 6.5, 7.5, 8.5, 9, 10, 11, 12, 13.5, 14.5, 15.5, 16.5});
    assertThat(m.get(0, 0)).isEqualTo(1.0);
    assertThat(m.get(0, 3)).isEqualTo(4.0);
    assertThat(m.get(1, 0)).isEqualTo(5.5);
    assertThat(m.get(1, 2)).isEqualTo(7.5);
    assertThat(m.get(2, 2)).isEqualTo(11.0);
    assertThat(m.get(3, 0)).isEqualTo(13.5);
    assertThat(m.get(3, 2)).isEqualTo(15.5);
  }

  @Test
  // Scenario: A 2x2 matrix ought to be representable
  public void construct2x2Matrix() {
    Matrix m = Matrix.create(2, 2, new double[] {-3, 5, 1, -2});
    assertThat(m.get(0, 0)).isEqualTo(-3.0);
    assertThat(m.get(0, 1)).isEqualTo(5.0);
    assertThat(m.get(1, 0)).isEqualTo(1.0);
    assertThat(m.get(1, 1)).isEqualTo(-2.0);
  }

  @Test
  // Scenario: A 3x3 matrix ought to be representable
  public void construct3x3Matrix() {
    Matrix m = Matrix.create(3, 3, new double[] {-3, 5, 0, 1, -2, 7, 0, 1, 1});
    assertThat(m.get(0, 0)).isEqualTo(-3.0);
    assertThat(m.get(1, 1)).isEqualTo(-2.0);
    assertThat(m.get(2, 2)).isEqualTo(1.0);
  }

  @Test
  // Scenario: Matrix equality with identical matrices
  public void equal() {
    Matrix a = Matrix.create(4, 4, new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2});
    Matrix b = Matrix.create(4, 4, new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2});
    assertThat(a).isEqualTo(b);
  }

  @Test
  // Scenario: Matrix equality with different matrices
  public void notEqual() {
    Matrix a = Matrix.create(4, 4, new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2});
    Matrix b = Matrix.create(4, 4, new double[] {2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2, 1});
    assertThat(a).isNotEqualTo(b);
  }

  @Test
  // Scenario: Multiplying two matrices
  public void multiply() {
    Matrix a = Matrix.create(4, 4, new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2});
    Matrix b = Matrix.create(4, 4, new double[] {-2, 1, 2, 3, 3, 2, 1, -1, 4, 3, 6, 5, 1, 2, 7, 8});
    Matrix expectedProduct =
        Matrix.create(
            4,
            4,
            new double[] {20, 22, 50, 48, 44, 54, 114, 108, 40, 58, 110, 102, 16, 26, 46, 42});
    assertThat(a.times(b)).isEqualTo(expectedProduct);
  }

  @Test
  // Scenario: A matrix multiplied by a tuple
  public void multiplyByTuple() {
    Matrix a = Matrix.create(4, 4, new double[] {1, 2, 3, 4, 2, 4, 4, 2, 8, 6, 4, 1, 0, 0, 0, 1});
    Tuple b = Tuple.create(1, 2, 3, 1);
    assertThat(a.times(b)).isEqualTo(Tuple.create(18, 24, 33, 1));
  }

  @Test
  // Scenario: Multiplying a matrix by the identity matrix
  public void multiplyByIdentity() {
    Matrix a =
        Matrix.create(4, 4, new double[] {0, 1, 2, 4, 1, 2, 4, 8, 2, 4, 8, 16, 4, 8, 16, 32});
    assertThat(a.times(Matrix.identity())).isEqualTo(a);
  }

  @Test
  // Scenario: Multiplying the identity matrix by a tuple
  public void multiplyTupleByIdentity() {
    Tuple a = Tuple.create(1, 2, 3, 4);
    assertThat(Matrix.identity().times(a)).isEqualTo(a);
  }

  @Test
  // Scenario: Transposing a matrix
  public void transpose() {
    Matrix a = Matrix.create(4, 4, new double[] {0, 9, 3, 0, 9, 8, 0, 8, 1, 8, 5, 3, 0, 0, 5, 8});
    Matrix expectedTranspose =
        Matrix.create(4, 4, new double[] {0, 9, 1, 0, 9, 8, 8, 0, 3, 0, 5, 5, 0, 8, 3, 8});
    assertThat(a.transpose()).isEqualTo(expectedTranspose);
  }

  @Test
  // Scenario: Transposing the identity matrix
  public void transposeIdentity() {
    Matrix identity = Matrix.identity();
    assertThat(identity.transpose()).isEqualTo(identity);
  }

  @Test
  // Scenario: Calculating the determinant of a 2x2 matrix
  public void determinant2x2() {
    Matrix a = Matrix.create(2, 2, new double[] {1, 5, -3, 2});
    assertThat(a.determinant()).isWithin(EPSILON).of(17);
  }

  @Test
  // Scenario: A submatrix of a 3x3 matrix is a 2x2 matrix
  public void submatrix3x3() {
    Matrix a = Matrix.create(3, 3, new double[] {1, 5, 0, -3, 2, 7, 0, 6, -3});
    Matrix expectedSubmatrix = Matrix.create(2, 2, new double[] {-3, 2, 0, 6});
    assertThat(a.submatrix(0, 2)).isEqualTo(expectedSubmatrix);
  }

  @Test
  // Scenario: A submatrix of a 4x4 matrix is a 3x3 matrix
  public void submatrix4x4() {
    Matrix a =
        Matrix.create(4, 4, new double[] {-6, 1, 1, 6, -8, 5, 8, 6, -1, 0, 8, 2, -7, 1, -1, 1});
    Matrix expectedSubmatrix = Matrix.create(3, 3, new double[] {-6, 1, 6, -8, 8, 6, -7, -1, 1});
    assertThat(a.submatrix(2, 1)).isEqualTo(expectedSubmatrix);
  }

  @Test
  // Scenario: Calculating a minor of a 3x3 matrix
  public void minor3x3() {
    Matrix a = Matrix.create(3, 3, new double[] {3, 5, 0, 2, -1, -7, 6, -1, 5});
    assertThat(a.minor(1, 0)).isWithin(EPSILON).of(25);
  }

  @Test
  // Scenario: Calculating the cofactor of a 3x3 matrix
  public void cofactor3x3() {
    Matrix a = Matrix.create(3, 3, new double[] {3, 5, 0, 2, -1, -7, 6, -1, 5});
    assertThat(a.minor(0, 0)).isWithin(EPSILON).of(-12);
    assertThat(a.cofactor(0, 0)).isWithin(EPSILON).of(-12);
    assertThat(a.minor(1, 0)).isWithin(EPSILON).of(25);
    assertThat(a.cofactor(1, 0)).isWithin(EPSILON).of(-25);
  }

  @Test
  // Scenario: Calculating the determinant of a 3x3 matrix
  public void determinant3x3() {
    Matrix a = Matrix.create(3, 3, new double[] {1, 2, 6, -5, 8, -4, 2, 6, 4});
    assertThat(a.cofactor(0, 0)).isWithin(EPSILON).of(56);
    assertThat(a.cofactor(0, 1)).isWithin(EPSILON).of(12);
    assertThat(a.cofactor(0, 2)).isWithin(EPSILON).of(-46);
    assertThat(a.determinant()).isWithin(EPSILON).of(-196);
  }

  @Test
  // Scenario: Calculating the determinant of a 4x4 matrix
  public void determinant4x4() {
    Matrix a =
        Matrix.create(4, 4, new double[] {-2, -8, 3, 5, -3, 1, 7, 3, 1, 2, -9, 6, -6, 7, 7, -9});
    assertThat(a.cofactor(0, 0)).isWithin(EPSILON).of(690);
    assertThat(a.cofactor(0, 1)).isWithin(EPSILON).of(447);
    assertThat(a.cofactor(0, 2)).isWithin(EPSILON).of(210);
    assertThat(a.cofactor(0, 3)).isWithin(EPSILON).of(51);
    assertThat(a.determinant()).isWithin(EPSILON).of(-4071);
  }

  @Test
  // Scenario: Testing an invertible matrix for invertibility
  public void isInvertible() {
    Matrix a =
        Matrix.create(4, 4, new double[] {6, 4, 4, 4, 5, 5, 7, 6, 4, -9, 3, -7, 9, 1, 7, -6});
    assertThat(a.determinant()).isWithin(EPSILON).of(-2120);
    assertThat(a.isInvertible()).isTrue();
  }

  @Test
  // Scenario: Testing a noninvertible matrix for invertibility
  public void isNotInvertible() {
    Matrix a =
        Matrix.create(4, 4, new double[] {-4, 2, -2, -3, 9, 6, 2, 6, 0, -5, 1, -5, 0, 0, 0, 0});
    assertThat(a.determinant()).isWithin(EPSILON).of(0);
    assertThat(a.isInvertible()).isFalse();
  }

  @Test
  // Scenario: Calculating the inverse of a matrix
  public void invert1() {
    Matrix a =
        Matrix.create(4, 4, new double[] {-5, 2, 6, -8, 1, -5, 1, 8, 7, 7, -6, -7, 1, -3, 7, 4});
    Matrix b = a.invert();

    assertThat(a.determinant()).isWithin(EPSILON).of(532);
    assertThat(a.cofactor(2, 3)).isWithin(EPSILON).of(-160);
    assertThat(b.get(3, 2)).isWithin(EPSILON).of(-160.0 / 532.0);
    assertThat(a.cofactor(3, 2)).isWithin(EPSILON).of(105);
    assertThat(b.get(2, 3)).isWithin(EPSILON).of(105.0 / 532.0);

    Matrix expectedInvert =
        Matrix.create(
            4,
            4,
            new double[] {
              0.21805, 0.45113, 0.24060, -0.04511, -0.80827, -1.45677, -0.44361, 0.52068, -0.07895,
              -0.22368, -0.05263, 0.19737, -0.52256, -0.81391, -0.30075, 0.30639
            });
    assertThat(b).isApproximatelyEqualTo(expectedInvert);
  }

  @Test
  // Scenario: Calculating the inverse of another matrix
  public void invert2() {
    Matrix a =
        Matrix.create(4, 4, new double[] {8, -5, 9, 2, 7, 5, 6, 1, -6, 0, 9, 6, -3, 0, -9, -4});
    Matrix expectedInvert =
        Matrix.create(
            4,
            4,
            new double[] {
              -0.15385, -0.15385, -0.28205, -0.53846, -0.07692, 0.12308, 0.02564, 0.03077, 0.35897,
              0.35897, 0.43590, 0.92308, -0.69231, -0.69231, -0.76923, -1.92308
            });
    assertThat(a.invert()).isApproximatelyEqualTo(expectedInvert);
  }

  @Test
  // Scenario: Calculating the inverse of a third matrix
  public void invert3() {
    Matrix a =
        Matrix.create(4, 4, new double[] {9, 3, 0, 9, -5, -2, -6, -3, -4, 9, 6, 4, -7, 6, 6, 2});
    Matrix expectedInvert =
        Matrix.create(
            4,
            4,
            new double[] {
              -0.04074, -0.07778, 0.14444, -0.22222, -0.07778, 0.03333, 0.36667, -0.33333, -0.02901,
              -0.14630, -0.10926, 0.12963, 0.17778, 0.06667, -0.26667, 0.33333
            });
    assertThat(a.invert()).isApproximatelyEqualTo(expectedInvert);
  }

  @Test
  // Scenario: Multiplying a product by its inverse
  public void multiplyProductByInverse() {
    Matrix a =
        Matrix.create(4, 4, new double[] {3, -9, 7, 3, 3, -8, 2, -9, -4, 4, 4, 1, -6, 5, -1, 1});
    Matrix b = Matrix.create(4, 4, new double[] {8, 2, 2, 2, 3, -1, 7, 0, 7, 0, 5, 4, 6, -2, 0, 5});
    Matrix c = a.times(b);
    assertThat(c.times(b.invert())).isApproximatelyEqualTo(a);
  }
}
