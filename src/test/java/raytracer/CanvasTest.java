package raytracer;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
// Feature: Canvas
public class CanvasTest {

  @Test
  // Scenario: Creating a canvas
  public void constructCanvas() {
    Canvas c = new Canvas(10, 20);
    assertThat(c.width()).isEqualTo(10);
    assertThat(c.height()).isEqualTo(20);
    for (int y = 0; y < 20; ++y) {
      for (int x = 0; x < 10; ++x) {
        assertThat(c.pixel(x, y)).isEqualTo(Color.BLACK);
      }
    }
  }

  @Test
  // Scenario: Writing pixels to a canvas
  public void writePixel() {
    Canvas c = new Canvas(10, 20);
    c.setPixel(2, 3, Color.RED);
    assertThat(c.pixel(2, 3)).isEqualTo(Color.RED);
  }

  @Test
  // Scenario: Writing pixels to a canvas out of range
  // Doesn't cause error, and returns BLACK.
  public void handlePixelOutOfRange() {
    Canvas c = new Canvas(10, 20);
    c.setPixel(-2, 3, Color.RED);
    assertThat(c.pixel(-2, 3)).isEqualTo(Color.BLACK);

    c.setPixel(2, -3, Color.RED);
    assertThat(c.pixel(2, -3)).isEqualTo(Color.BLACK);

    c.setPixel(12, 3, Color.RED);
    assertThat(c.pixel(12, 3)).isEqualTo(Color.BLACK);

    c.setPixel(2, 23, Color.RED);
    assertThat(c.pixel(2, 23)).isEqualTo(Color.BLACK);
  }

  // Returns the PPM file string for the given canvas.
  private String toPpmString(Canvas c) {
    ByteArrayOutputStream ppmStream = new ByteArrayOutputStream();
    c.writePpmStream(ppmStream);
    return ppmStream.toString();
  }

  @Test
  // Scenario: Constructing the PPM header
  public void ppmHeader() {
    Canvas c = new Canvas(5, 3);
    String[] ppmLines = toPpmString(c).split("\n");
    assertThat(ppmLines.length).isAtLeast(3);
    assertThat(ppmLines[0]).isEqualTo("P3");
    assertThat(ppmLines[1]).isEqualTo("5 3");
    assertThat(ppmLines[2]).isEqualTo("255");
  }

  @Test
  // Scenario: Constructing the PPM pixel data
  public void ppmPixels() {
    Canvas c = new Canvas(5, 3);
    c.setPixel(0, 0, Color.create(1.5, 0, 0));
    c.setPixel(2, 1, Color.create(0, 0.5, 0));
    c.setPixel(4, 2, Color.create(-0.5, 0, 1));
    String[] ppmLines = toPpmString(c).split("\n");
    assertThat(ppmLines.length).isAtLeast(6);
    assertThat(ppmLines[3]).isEqualTo("255 0 0 0 0 0 0 0 0 0 0 0 0 0 0");
    assertThat(ppmLines[4]).isEqualTo("0 0 0 0 0 0 0 128 0 0 0 0 0 0 0");
    assertThat(ppmLines[5]).isEqualTo("0 0 0 0 0 0 0 0 0 0 0 0 0 0 255");
  }

  @Test
  // Scenario: Splitting long lines in PPM files
  public void splitLongPpmLines() {
    Canvas c = new Canvas(10, 2);
    c.forEachIndex((x, y) -> c.setPixel(x, y, Color.create(1, 0.8, 0.6)));
    String[] ppmLines = toPpmString(c).split("\n");
    assertThat(ppmLines.length).isAtLeast(7);
    assertThat(ppmLines[3])
        .isEqualTo("255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204");
    assertThat(ppmLines[4]).isEqualTo("153 255 204 153 255 204 153 255 204 153 255 204 153");
    assertThat(ppmLines[5])
        .isEqualTo("255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204");
    assertThat(ppmLines[6]).isEqualTo("153 255 204 153 255 204 153 255 204 153 255 204 153");
  }

  @Test
  // Scenario: PPM files are terminated by a newline character
  public void lastCharIsNewline() {
    Canvas c = new Canvas(5, 3);
    String ppmString = toPpmString(c);
    char lastChar = ppmString.charAt(ppmString.length() - 1);
    assertThat(lastChar).isEqualTo('\n');
  }
}
