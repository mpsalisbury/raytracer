package raytracer;

import com.google.common.collect.ArrayTable;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;

public class Canvas {
  // Index is [0..width-1][0..height-1]
  private int width;
  private int height;
  private ArrayTable<Integer, Integer, Color> pixels;

  public Canvas(int width, int height) {
    initialize(width, height);
  }

  private void initialize(int width, int height) {
    this.width = width;
    this.height = height;
    List<Integer> xRange = IntStream.range(0, width).boxed().collect(Collectors.toList());
    List<Integer> yRange = IntStream.range(0, height).boxed().collect(Collectors.toList());
    this.pixels = ArrayTable.create(xRange, yRange);
    forEachIndex((x, y) -> pixels.set(x, y, Color.BLACK));
  }

  public int width() {
    return width;
  }

  public int height() {
    return height;
  }

  private IntStream xIndexStream() {
    return IntStream.range(0, width);
  }

  private IntStream yIndexStream() {
    return IntStream.range(0, height);
  }

  public void forEachIndex(BiConsumer<Integer, Integer> callback) {
    yIndexStream().forEach(y -> xIndexStream().forEach(x -> callback.accept(x, y)));
  }

  private boolean indexInRange(int x, int y) {
    return (x >= 0 && x < width && y >= 0 && y < height);
  }

  public Color pixel(int x, int y) {
    if (indexInRange(x, y)) {
      return pixels.get(x, y);
    } else {
      return Color.BLACK;
    }
  }

  public void setPixel(int x, int y, Color c) {
    if (indexInRange(x, y)) {
      pixels.set(x, y, c);
    }
  }

  // Returns the ppm value for the given color channel value.
  private int toChannelByte(double channel) {
    if (channel <= 0.0) {
      return 0;
    }
    if (channel >= 1.0) {
      return 255;
    }
    return (int) (channel * 256.0);
  }

  private void writeToRaster(WritableRaster raster) {
    forEachIndex(
        (x, y) -> {
          Color c = pixels.get(x, y);
          int[] colorArray =
              new int[] {toChannelByte(c.red()), toChannelByte(c.green()), toChannelByte(c.blue())};
          raster.setPixel(x, y, colorArray);
        });
  }

  private RenderedImage toImage() {
    BufferedImage image = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);
    writeToRaster(image.getRaster());
    return image;
  }

  public void writePngFile(File outFile) throws IOException {
    RenderedImage image = toImage();
    ImageIO.write(image, "png", outFile);
  }

  // Returns the ppm string format of the given color.
  private String toPpmPixel(Color c) {
    return String.format(
        "%d %d %d", toChannelByte(c.red()), toChannelByte(c.green()), toChannelByte(c.blue()));
  }

  // Writes this canvas to the given PPM file.
  public void writePpmFile(File outFile) throws IOException {
    FileOutputStream outfile = new FileOutputStream(outFile);
    writePpmStream(outfile);
    outfile.close();
  }

  // Writes this canvas to the given PPM stream.
  public void writePpmStream(OutputStream outStream) {
    PrintStream out = new PrintStream(outStream);
    out.println("P3");
    out.println(String.format("%d %d", width(), height()));
    out.println("255");

    yIndexStream()
        .forEach(
            y ->
                out.println(
                    addLinebreaksToLongLines(
                        xIndexStream()
                            .mapToObj(x -> pixels.get(x, y))
                            .map(c -> toPpmPixel(c))
                            .collect(Collectors.joining(" ")))));
    out.flush();
  }

  // Split line into multiple lines no longer than 70 characters.
  private String addLinebreaksToLongLines(String input) {
    final int lengthLimit = 70;
    StringBuilder output = new StringBuilder();
    int currentIndex = 0;
    int lastIndex = input.length();
    while (lastIndex - currentIndex > lengthLimit) {
      // find last whole word at < lengthLimit characters
      int spaceIndex = currentIndex + lengthLimit;
      while (input.charAt(spaceIndex) != ' ') {
        --spaceIndex;
      }
      output.append(input.substring(currentIndex, spaceIndex));
      output.append("\n");
      currentIndex = spaceIndex + 1;
    }
    output.append(input.substring(currentIndex));
    return output.toString();
  }
}
