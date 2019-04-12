package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static raytracer.Testing.EPSILON;
import static raytracer.TupleSubject.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Tuples, Vectors, and Points
public class TupleTest {

  @Test
  // Scenario: A tuple with w=1.0 is a point
  public void constructTuplePoint() {
    Tuple a = Tuple.create(4.3, -4.2, 3.1, 1.0);
    assertThat(a.x()).isEqualTo(4.3);
    assertThat(a.y()).isEqualTo(-4.2);
    assertThat(a.z()).isEqualTo(3.1);
    assertThat(a.w()).isEqualTo(1.0);
    assertThat(a.isPoint()).isTrue();
    assertThat(a.isVector()).isFalse();
  }

  @Test
  // Scenario: point() creates tuples with w=1
  public void constructPoint() {
    Tuple p = Tuple.createPoint(4, -4, 3);
    assertThat(p).isEqualTo(Tuple.create(4, -4, 3, 1));
  }

  @Test
  // Scenario: A tuple with w=0 is a vector
  public void constructTupleVector() {
    Tuple a = Tuple.create(4.3, -4.2, 3.1, 0.0);
    assertThat(a.x()).isEqualTo(4.3);
    assertThat(a.y()).isEqualTo(-4.2);
    assertThat(a.z()).isEqualTo(3.1);
    assertThat(a.w()).isEqualTo(0.0);
    assertThat(a.isPoint()).isFalse();
    assertThat(a.isVector()).isTrue();
  }

  @Test
  // Scenario: vector() creates tuples with w=0
  public void constructVector() {
    Tuple v = Tuple.createVector(4, -4, 3);
    assertThat(v).isEqualTo(Tuple.create(4, -4, 3, 0));
  }

  @Test
  // Scenario: Adding two tuples
  public void AddTuples() {
    Tuple a1 = Tuple.create(3, -2, 5, 1);
    Tuple a2 = Tuple.create(-2, 3, 1, 0);
    assertThat(a1.plus(a2)).isEqualTo(Tuple.create(1, 1, 6, 1));
  }

  @Test
  // Scenario: Subtracting two points
  public void SubtractPoints() {
    Tuple p1 = Tuple.createPoint(3, 2, 1);
    Tuple p2 = Tuple.createPoint(5, 6, 7);
    assertThat(p1.minus(p2)).isEqualTo(Tuple.createVector(-2, -4, -6));
  }

  @Test
  // Scenario: Subtracting a vector from a point
  public void SubtractVectorFromPoint() {
    Tuple p = Tuple.createPoint(3, 2, 1);
    Tuple v = Tuple.createVector(5, 6, 7);
    assertThat(p.minus(v)).isEqualTo(Tuple.createPoint(-2, -4, -6));
  }

  @Test
  // Scenario: Subtracting two vectors
  public void SubtractVectors() {
    Tuple v1 = Tuple.createVector(3, 2, 1);
    Tuple v2 = Tuple.createVector(5, 6, 7);
    assertThat(v1.minus(v2)).isEqualTo(Tuple.createVector(-2, -4, -6));
  }

  @Test
  // Scenario: Subtracting a vector from the zero vector
  public void SubtractVectorFromZero() {
    Tuple zero = Tuple.createVector(0, 0, 0);
    Tuple v = Tuple.createVector(1, -2, 3);
    assertThat(zero.minus(v)).isEqualTo(Tuple.createVector(-1, 2, -3));
  }

  @Test
  // Scenario: Negating a tuple
  public void Negate() {
    Tuple a = Tuple.create(1, -2, 3, -4);
    assertThat(a.negate()).isEqualTo(Tuple.create(-1, 2, -3, 4));
  }

  @Test
  // Scenario: Multiplying a tuple by a scalar
  public void Times() {
    Tuple a = Tuple.create(1, -2, 3, -4);
    assertThat(a.times(3.5)).isEqualTo(Tuple.create(3.5, -7, 10.5, -14));
    assertThat(a.times(0.5)).isEqualTo(Tuple.create(0.5, -1, 1.5, -2));
  }

  @Test
  // Scenario: Dividing a tuple by a scalar
  public void Divide() {
    Tuple a = Tuple.create(1, -2, 3, -4);
    assertThat(a.dividedBy(2)).isEqualTo(Tuple.create(0.5, -1, 1.5, -2));
  }

  @Test
  // Scenario: Computing the magnitude of vectors
  public void magnitude() {
    Tuple v = Tuple.createVector(1, 0, 0);
    assertThat(v.magnitude()).isWithin(EPSILON).of(1);

    v = Tuple.createVector(0, 1, 0);
    assertThat(v.magnitude()).isWithin(EPSILON).of(1);

    v = Tuple.createVector(0, 0, 1);
    assertThat(v.magnitude()).isWithin(EPSILON).of(1);

    v = Tuple.createVector(1, 2, 3);
    assertThat(v.magnitude()).isWithin(EPSILON).of(Math.sqrt(14));

    v = Tuple.createVector(-1, -2, -3);
    assertThat(v.magnitude()).isWithin(EPSILON).of(Math.sqrt(14));
  }

  @Test
  // Scenario: Normalize vectors
  public void normalize() {
    Tuple v = Tuple.createVector(4, 0, 0);
    assertThat(v.normalize()).isEqualTo(Tuple.createVector(1, 0, 0));

    v = Tuple.createVector(1, 2, 3);
    assertThat(v.normalize())
        .isApproximatelyEqualTo(EPSILON, Tuple.createVector(0.26726, 0.53452, 0.80178));
    //            vector(1/√14,   2/√14,   3/√14)
  }

  @Test
  // Scenario: The magnitude of a normalized vector
  public void magnitudeOfNormalize() {
    Tuple v = Tuple.createVector(1, 2, 3);
    Tuple norm = v.normalize();
    assertThat(norm.magnitude()).isWithin(EPSILON).of(1);
  }

  @Test
  // Scenario: The dot product of two tuples
  public void dotProduct() {
    Tuple a = Tuple.createVector(1, 2, 3);
    Tuple b = Tuple.createVector(2, 3, 4);
    assertThat(a.dot(b)).isWithin(EPSILON).of(20);
  }

  @Test
  // Scenario: The cross product of two vectors
  public void crossProduct() {
    Tuple a = Tuple.createVector(1, 2, 3);
    Tuple b = Tuple.createVector(2, 3, 4);
    assertThat(a.cross(b)).isEqualTo(Tuple.createVector(-1, 2, -1));
    assertThat(b.cross(a)).isEqualTo(Tuple.createVector(1, -2, 1));
  }

  @Test
  // Scenario: Reflecting a vector approaching at 45°
  public void reflect1() {
    Tuple v = Tuple.createVector(1, -1, 0);
    Tuple n = Tuple.createVector(0, 1, 0);
    assertThat(v.reflect(n)).isEqualTo(Tuple.createVector(1, 1, 0));
  }

  @Test
  // Scenario: Reflecting a vector off a slanted surface
  public void reflect2() {
    Tuple v = Tuple.createVector(0, -1, 0);
    Tuple n = Tuple.createVector(1, 1, 0).normalize();
    assertThat(v.reflect(n)).isApproximatelyEqualTo(EPSILON, Tuple.createVector(1, 0, 0));
  }
}
