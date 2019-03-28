package raytracer;

import java.io.IOException;

public class AppUtil {
  // Saves the given canvas to the given file.
  // Returns true on success, false on failure.
  public static boolean saveCanvasToPpm(Canvas canvas, String outFileBase) {
    return saveCanvas(outFileBase, "ppm", fn -> canvas.writePpmFile(fn));
  }

  // Saves the given canvas to the given file.
  // Returns true on success, false on failure.
  public static boolean saveCanvasToPng(Canvas canvas, String outFileBase) {
    return saveCanvas(outFileBase, "png", fn -> canvas.writePngFile(fn));
  }

  @FunctionalInterface
  interface FileWriter {
    void write(String outFileName) throws IOException;
  }

  // Saves the given canvas to the given file.
  // Returns true on success, false on failure.
  public static boolean saveCanvas(String outFileBase, String extension, FileWriter writeFile) {
    String outFileName = String.format("%s.%s", outFileBase, extension);
    try {
      writeFile.write(outFileName);
      System.out.println("Saved to file: " + outFileName);
      return true;
    } catch (IOException e) {
      System.err.println("Can't open image file " + outFileName);
      return false;
    }
  }
}
