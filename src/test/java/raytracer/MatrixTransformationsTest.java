package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.MatrixSubject.assertThat;
import static raytracer.TupleSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Matrix Transformations
public class MatrixTransformationsTest {

  private static final double EPSILON = 1.0e-5;

  @Test
  // Scenario: Multiplying by a translation matrix
  public void translatePoint() {
    Matrix t = Matrix.translation(5, -3, 2);
    Tuple p = Tuple.createPoint(-3, 4, 5);
    assertThat(t.times(p)).isEqualTo(Tuple.createPoint(2, 1, 7));
  }

  @Test
  // Scenario: Multiplying by the inverse of a translation matrix
  public void inverseTranslatePoint() {
    Matrix t = Matrix.translation(5, -3, 2);
    Tuple p = Tuple.createPoint(-3, 4, 5);
    assertThat(t.invert().times(p)).isEqualTo(Tuple.createPoint(-8, 7, 3));
  }

  @Test
  // Scenario: Translation does not affect vectors
  public void translateIgnoresVectors() {
    Matrix t = Matrix.translation(5, -3, 2);
    Tuple v = Tuple.createVector(-3, 4, 5);
    assertThat(t.times(v)).isEqualTo(v);
  }

  @Test
  // Scenario: A scaling matrix applied to a point
  public void scalePoint() {
    Matrix s = Matrix.scaling(2, 3, 4);
    Tuple p = Tuple.createPoint(-4, 6, 8);
    assertThat(s.times(p)).isEqualTo(Tuple.createPoint(-8, 18, 32));
  }

  @Test
  // Scenario: Multiplying by the inverse of a scaling matrix
  public void inverseScalePoint() {
    Matrix s = Matrix.scaling(2, 3, 4);
    Tuple v = Tuple.createVector(-4, 6, 8);
    assertThat(s.invert().times(v)).isEqualTo(Tuple.createVector(-2, 2, 2));
  }

  @Test
  // Scenario: A scaling matrix applied to a vector
  public void scaleVector() {
    Matrix s = Matrix.scaling(2, 3, 4);
    Tuple v = Tuple.createVector(-4, 6, 8);
    assertThat(s.times(v)).isEqualTo(Tuple.createVector(-8, 18, 32));
  }

  @Test
  // Scenario: Reflection is scaling by a negative value
  public void reflect() {
    Matrix s = Matrix.scaling(-1, 1, 1);
    Tuple p = Tuple.createPoint(2, 3, 4);
    assertThat(s.times(p)).isEqualTo(Tuple.createPoint(-2, 3, 4));
  }

  @Test
  // Scenario: Rotating a point around the x axis
  public void rotateX() {
    Matrix rotateX45 = Matrix.rotationX(Math.PI / 4);
    Matrix rotateX90 = Matrix.rotationX(Math.PI / 2);
    Tuple p = Tuple.createPoint(0, 1, 0);
    assertThat(rotateX45.times(p))
        .isApproximatelyEqualTo(Tuple.createPoint(0, 1.0 / Math.sqrt(2), 1.0 / Math.sqrt(2)));
    assertThat(rotateX90.times(p)).isApproximatelyEqualTo(Tuple.createPoint(0, 0, 1));
  }

  @Test
  // Scenario: The inverse of an x-rotation rotates in the opposite direction
  public void inverseRotateX() {
    Matrix rotateX45 = Matrix.rotationX(Math.PI / 4);
    Tuple p = Tuple.createPoint(0, 1, 0);
    assertThat(rotateX45.invert().times(p))
        .isApproximatelyEqualTo(Tuple.createPoint(0, 1.0 / Math.sqrt(2), -1.0 / Math.sqrt(2)));
  }

  @Test
  // Scenario: Rotating a point around the y axis
  public void rotateY() {
    Matrix rotateY45 = Matrix.rotationY(Math.PI / 4);
    Matrix rotateY90 = Matrix.rotationY(Math.PI / 2);
    Tuple p = Tuple.createPoint(0, 0, 1);
    assertThat(rotateY45.times(p))
        .isApproximatelyEqualTo(Tuple.createPoint(1.0 / Math.sqrt(2), 0, 1.0 / Math.sqrt(2)));
    assertThat(rotateY90.times(p)).isApproximatelyEqualTo(Tuple.createPoint(1, 0, 0));
  }

  @Test
  // Scenario: Rotating a point around the z axis
  public void rotateZ() {
    Matrix rotateZ45 = Matrix.rotationZ(Math.PI / 4);
    Matrix rotateZ90 = Matrix.rotationZ(Math.PI / 2);
    Tuple p = Tuple.createPoint(0, 1, 0);
    assertThat(rotateZ45.times(p))
        .isApproximatelyEqualTo(Tuple.createPoint(-1.0 / Math.sqrt(2), 1.0 / Math.sqrt(2), 0));
    assertThat(rotateZ90.times(p)).isApproximatelyEqualTo(Tuple.createPoint(-1, 0, 0));
  }

  @Test
  // Scenario: A shearing transformation moves x in proportion to y
  public void shearingXtoY() {
    Matrix s = Matrix.shearing(1, 0, 0, 0, 0, 0);
    Tuple p = Tuple.createPoint(2, 3, 4);
    assertThat(s.times(p)).isEqualTo(Tuple.createPoint(5, 3, 4));
  }

  @Test
  // Scenario: A shearing transformation moves x in proportion to z
  public void shearingXtoZ() {
    Matrix s = Matrix.shearing(0, 1, 0, 0, 0, 0);
    Tuple p = Tuple.createPoint(2, 3, 4);
    assertThat(s.times(p)).isEqualTo(Tuple.createPoint(6, 3, 4));
  }

  @Test
  // Scenario: A shearing transformation moves y in proportion to x
  public void shearingYtoX() {
    Matrix s = Matrix.shearing(0, 0, 1, 0, 0, 0);
    Tuple p = Tuple.createPoint(2, 3, 4);
    assertThat(s.times(p)).isEqualTo(Tuple.createPoint(2, 5, 4));
  }

  @Test
  // Scenario: A shearing transformation moves y in proportion to z
  public void shearingYtoZ() {
    Matrix s = Matrix.shearing(0, 0, 0, 1, 0, 0);
    Tuple p = Tuple.createPoint(2, 3, 4);
    assertThat(s.times(p)).isEqualTo(Tuple.createPoint(2, 7, 4));
  }

  @Test
  // Scenario: A shearing transformation moves z in proportion to x
  public void shearingZtoX() {
    Matrix s = Matrix.shearing(0, 0, 0, 0, 1, 0);
    Tuple p = Tuple.createPoint(2, 3, 4);
    assertThat(s.times(p)).isEqualTo(Tuple.createPoint(2, 3, 6));
  }

  @Test
  // Scenario: A shearing transformation moves z in proportion to y
  public void shearingZtoY() {
    Matrix s = Matrix.shearing(0, 0, 0, 0, 0, 1);
    Tuple p = Tuple.createPoint(2, 3, 4);
    assertThat(s.times(p)).isEqualTo(Tuple.createPoint(2, 3, 7));
  }

  @Test
  // Scenario: Individual transformations are applied in sequence
  public void sequentialTransforms() {
    Tuple p = Tuple.createPoint(1, 0, 1);
    Matrix a = Matrix.rotationX(Math.PI / 2);
    Matrix b = Matrix.scaling(5, 5, 5);
    Matrix c = Matrix.translation(10, 5, 7);

    Tuple p2 = a.times(p);
    assertThat(p2).isApproximatelyEqualTo(Tuple.createPoint(1, -1, 0));

    Tuple p3 = b.times(p2);
    assertThat(p3).isApproximatelyEqualTo(Tuple.createPoint(5, -5, 0));

    Tuple p4 = c.times(p3);
    assertThat(p4).isApproximatelyEqualTo(Tuple.createPoint(15, 0, 7));
  }

  @Test
  // Scenario: Chained transformations must be applied in reverse order
  public void chainedTransforms() {
    Tuple p = Tuple.createPoint(1, 0, 1);
    Matrix a = Matrix.rotationX(Math.PI / 2);
    Matrix b = Matrix.scaling(5, 5, 5);
    Matrix c = Matrix.translation(10, 5, 7);

    Matrix t = c.times(b.times(a));
    assertThat(t.times(p)).isApproximatelyEqualTo(Tuple.createPoint(15, 0, 7));
  }

  @Test
  // Scenario: Fluent chain transforms match explicitly combined.
  public void fluentTransforms() {
    Matrix a = Matrix.rotationX(Math.PI / 2);
    Matrix b = Matrix.scaling(5, 5, 5);
    Matrix c = Matrix.translation(10, 5, 7);
    Matrix t = c.times(b.times(a));

    Matrix f = Matrix.identity().rotateX(Math.PI / 2).scale(5, 5, 5).translate(10, 5, 7);
    assertThat(f).isApproximatelyEqualTo(t);
  }

  @Test
  // Scenario: The transformation matrix for the default orientation
  public void defaultViewTransform() {
    Tuple from = Tuple.createPoint(0, 0, 0);
    Tuple to = Tuple.createPoint(0, 0, -1);
    Tuple up = Tuple.createVector(0, 1, 0);

    Matrix t = Matrix.viewTransform(from, to, up);
    assertThat(t).isApproximatelyEqualTo(Matrix.identity());
  }

  @Test
  // Scenario: A view transformation matrix looking in positive z direction
  public void viewTransformPositiveZ() {
    Tuple from = Tuple.createPoint(0, 0, 0);
    Tuple to = Tuple.createPoint(0, 0, 1);
    Tuple up = Tuple.createVector(0, 1, 0);

    Matrix t = Matrix.viewTransform(from, to, up);
    assertThat(t).isApproximatelyEqualTo(Matrix.scaling(-1, 1, -1));
  }

  @Test
  // Scenario: The view transformation moves the world
  public void translatingViewTransform() {
    Tuple from = Tuple.createPoint(0, 0, 8);
    Tuple to = Tuple.createPoint(0, 0, 0);
    Tuple up = Tuple.createVector(0, 1, 0);

    Matrix t = Matrix.viewTransform(from, to, up);
    assertThat(t).isApproximatelyEqualTo(Matrix.translation(0, 0, -8));
  }

  @Test
  // Scenario: An arbitrary view transformation
  public void arbitraryViewTransform() {
    Tuple from = Tuple.createPoint(1, 3, 2);
    Tuple to = Tuple.createPoint(4, -2, 8);
    Tuple up = Tuple.createVector(1, 1, 0);

    Matrix t = Matrix.viewTransform(from, to, up);
    assertThat(t)
        .isApproximatelyEqualTo(
            Matrix.create(
                4,
                4,
                new double[] {
                  -0.50709, 0.50709, 0.67612, -2.36643, 0.76772, 0.60609, 0.12122, -2.82843,
                  -0.35857, 0.59761, -0.71714, 0.00000, 0.00000, 0.00000, 0.00000, 1.00000
                }));
  }
}
