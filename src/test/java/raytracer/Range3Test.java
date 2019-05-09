package raytracer;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static raytracer.TupleSubject.assertThat;

import com.google.common.collect.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Range3
public class Range3Test {

  // Returns the intersections between the bounding box of the given range and the ray specified by
  // the given point and direction.
  private boolean maybeHits(
      Range3 range, double px, double py, double pz, double dx, double dy, double dz) {
    Tuple origin = Tuple.point(px, py, pz);
    Tuple direction = Tuple.vector(dx, dy, dz);
    Ray r = Ray.create(origin, direction);
    return range.createBoundingBox().maybeHits(r);
  }

  @Test
  // Scenario: Construct empty range.
  public void createEmpty() {
    Range3 emptyRange = Range3.createEmpty();
    // Hard to verify that no ray will intersect empty range.
    assertThat(emptyRange.getMinPoint_TESTING()).isEqualTo(Tuple.point(0, 0, 0));
    assertThat(emptyRange.getMaxPoint_TESTING()).isEqualTo(Tuple.point(0, 0, 0));
  }

  @Test
  // Scenario: Construct range with doubles
  public void createFromDoubles() {
    Range3 range = Range3.create(0, 1, 2, 3, 4, 5);
    assertThat(range.getMinPoint_TESTING()).isEqualTo(Tuple.point(0, 2, 4));
    assertThat(range.getMaxPoint_TESTING()).isEqualTo(Tuple.point(1, 3, 5));
  }

  @Test
  // Scenario: Construct range with ranges
  public void createFromRanges() {
    Range3 range =
        Range3.create(Range.closed(0.0, 1.0), Range.closed(2.0, 3.0), Range.closed(4.0, 5.0));
    assertThat(range.getMinPoint_TESTING()).isEqualTo(Tuple.point(0, 2, 4));
    assertThat(range.getMaxPoint_TESTING()).isEqualTo(Tuple.point(1, 3, 5));
  }

  @Test
  // Scenario: Test bounding box extents.
  public void boundingBox() {
    Range3 range =
        Range3.create(Range.closed(0.0, 1.0), Range.closed(2.0, 3.0), Range.closed(4.0, 5.0));
    assertThat(maybeHits(range, 0.5, 1.9, 4.5, 1, 0, 0)).isFalse();
    assertThat(maybeHits(range, 0.5, 2.1, 4.5, 1, 0, 0)).isTrue();
    assertThat(maybeHits(range, 0.5, 2.9, 4.5, 1, 0, 0)).isTrue();
    assertThat(maybeHits(range, 0.5, 3.1, 4.5, 1, 0, 0)).isFalse();

    assertThat(maybeHits(range, 0.5, 2.5, 3.9, 1, 0, 0)).isFalse();
    assertThat(maybeHits(range, 0.5, 2.5, 4.1, 1, 0, 0)).isTrue();
    assertThat(maybeHits(range, 0.5, 2.5, 4.9, 1, 0, 0)).isTrue();
    assertThat(maybeHits(range, 0.5, 2.5, 5.1, 1, 0, 0)).isFalse();

    assertThat(maybeHits(range, -0.1, 2.5, 4.5, 0, 1, 0)).isFalse();
    assertThat(maybeHits(range, 0.1, 2.5, 4.5, 0, 1, 0)).isTrue();
    assertThat(maybeHits(range, 0.9, 2.5, 4.5, 0, 1, 0)).isTrue();
    assertThat(maybeHits(range, 1.1, 2.5, 4.5, 0, 1, 0)).isFalse();

    assertThat(maybeHits(range, 0.5, 2.5, 3.9, 0, 1, 0)).isFalse();
    assertThat(maybeHits(range, 0.5, 2.5, 4.1, 0, 1, 0)).isTrue();
    assertThat(maybeHits(range, 0.5, 2.5, 4.9, 0, 1, 0)).isTrue();
    assertThat(maybeHits(range, 0.5, 2.5, 5.1, 0, 1, 0)).isFalse();

    assertThat(maybeHits(range, -0.1, 2.5, 4.5, 0, 0, 1)).isFalse();
    assertThat(maybeHits(range, 0.1, 2.5, 4.5, 0, 0, 1)).isTrue();
    assertThat(maybeHits(range, 0.9, 2.5, 4.5, 0, 0, 1)).isTrue();
    assertThat(maybeHits(range, 1.1, 2.5, 4.5, 0, 0, 1)).isFalse();

    assertThat(maybeHits(range, 0.5, 1.9, 4.5, 0, 0, 1)).isFalse();
    assertThat(maybeHits(range, 0.5, 2.1, 4.5, 0, 0, 1)).isTrue();
    assertThat(maybeHits(range, 0.5, 2.9, 4.5, 0, 0, 1)).isTrue();
    assertThat(maybeHits(range, 0.5, 3.1, 4.5, 0, 0, 1)).isFalse();
  }

  @Test
  // Scenario: combine ranges
  public void span() {
    Range3 emptyRange = Range3.createEmpty();
    Range3 range1 = Range3.create(0, 1, 2, 3, 4, 5);
    Range3 range2 = Range3.create(3, 3, 2.5, 4, 1, 2);
    Range3 expectedCombinedRange = Range3.create(0, 3, 2, 4, 1, 5);

    assertThat(emptyRange.span(range1)).isEqualTo(range1);
    assertThat(range1.span(emptyRange)).isEqualTo(range1);
    assertThat(range1.span(range2)).isEqualTo(expectedCombinedRange);
    assertThat(range2.span(range1)).isEqualTo(expectedCombinedRange);
  }

  @Test
  // Scenario: transform range
  public void transform() {
    Range3 range = Range3.create(0, 1, 2, 3, 4, 5);

    assertThat(range.transform(Matrix.translation(1, 0, 0)))
        .isEqualTo(Range3.create(1, 2, 2, 3, 4, 5));
    assertThat(range.transform(Matrix.translation(0, 1, 0)))
        .isEqualTo(Range3.create(0, 1, 3, 4, 4, 5));
    assertThat(range.transform(Matrix.translation(0, 0, 1)))
        .isEqualTo(Range3.create(0, 1, 2, 3, 5, 6));

    assertThat(range.transform(Matrix.scaling(0, 1, 2)))
        .isEqualTo(Range3.create(0, 0, 2, 3, 8, 10));
  }
}
