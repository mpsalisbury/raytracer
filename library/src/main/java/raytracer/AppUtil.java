package raytracer;

import java.io.File;
import java.io.IOException;

// Utilities for RayTracing applications.
public class AppUtil {

  private static final String OUTDIR = "images";

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
    void write(File outFile) throws IOException;
  }

  // Saves the given canvas to the given file.
  // Returns true on success, false on failure.
  public static boolean saveCanvas(String outFileBase, String extension, FileWriter writeFile) {
    String outFileName = String.format("%s.%s", outFileBase, extension);
    File outFile = new File(OUTDIR, outFileName);
    ensureDirectoryExists(outFile.getParentFile());
    try {
      writeFile.write(outFile);
      System.out.println("Saved to file: " + outFile);
      return true;
    } catch (IOException e) {
      System.err.println("Can't open image file " + outFile);
      return false;
    }
  }

  private static void ensureDirectoryExists(File outDir) {
    // Null parent directory is okay -- using current directory.
    if (outDir == null) {
      return;
    }
    if (outDir.exists()) {
      return;
    }
    outDir.mkdirs();
  }
}
