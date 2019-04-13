package projects.chapter4;

import raytracer.AppUtil;
import raytracer.Canvas;
import raytracer.Color;
import raytracer.Matrix;
import raytracer.Tuple;

public class Clock {

  public static void main(String[] args) {
    Tuple handTip = Tuple.point(0, 1, 0);
    Matrix rotation = Matrix.rotationZ(Math.PI / 6);

    Canvas canvas = new Canvas(200, 200);
    Matrix transform = Matrix.scaling(80, 80, 0).translate(100, 100, 0);
    for (int i = 0; i < 12; ++i) {
      Tuple hourMark = transform.times(handTip);
      canvas.setPixel((int) hourMark.x(), (int) hourMark.y(), Color.WHITE);
      transform = transform.times(rotation);
    }

    AppUtil.saveCanvasToPng(canvas, "clock");
  }
}
