package raytracer;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

// Describes a 2d double-valued matrix.
public class Matrix {

  private int numRows;
  private int numCols;
  // Matrix values in row-major order.
  private double values[];
  // Default cell value, used for out-of-bounds cells.
  private static final double DEFAULT_CELL = 0.0;
  private static final int DEFAULT_MATRIX_SIZE = 4;

  private Matrix(int numRows, int numCols) {
    this.numRows = numRows;
    this.numCols = numCols;
    this.values = new double[numRows * numCols];
  }

  public static Matrix create(int numRows, int numCols) {
    return new Matrix(numRows, numCols);
  }

  // initValues is the complete matrix in row-major order.
  public static Matrix create(int numRows, int numCols, double initValues[]) {
    Matrix m = new Matrix(numRows, numCols);
    m.init(initValues);
    return m;
  }

  // Returns the identity matrix.
  public static Matrix identity() {
    return identity(DEFAULT_MATRIX_SIZE);
  }

  // Returns the identity matrix of the given size.
  public static Matrix identity(int size) {
    Matrix m = new Matrix(size, size);
    for (int i = 0; i < size; ++i) {
      m.set(i, i, 1);
    }
    return m;
  }

  // Fluently append translation to this transform (premultiply).
  public Matrix translate(double x, double y, double z) {
    return translation(x, y, z).times(this);
  }

  public static Matrix translation(double x, double y, double z) {
    Matrix t = identity();
    t.set(0, 3, x);
    t.set(1, 3, y);
    t.set(2, 3, z);
    return t;
  }

  // Fluently append scale to this transform (premultiply).
  public Matrix scale(double x, double y, double z) {
    return scaling(x, y, z).times(this);
  }

  public Matrix scale(double s) {
    return scale(s, s, s);
  }

  public static Matrix scaling(double x, double y, double z) {
    Matrix s = identity();
    s.set(0, 0, x);
    s.set(1, 1, y);
    s.set(2, 2, z);
    return s;
  }

  public static Matrix scaling(double s) {
    return scaling(s, s, s);
  }

  // Fluently append rotateX to this transform (premultiply).
  public Matrix rotateX(double angle) {
    return rotationX(angle).times(this);
  }

  public static Matrix rotationX(double angle) {
    return rotation(angle, 1, 2);
  }

  // Fluently append rotateY to this transform (premultiply).
  public Matrix rotateY(double angle) {
    return rotationY(angle).times(this);
  }

  public static Matrix rotationY(double angle) {
    return rotation(angle, 2, 0);
  }

  // Fluently append rotateZ to this transform (premultiply).
  public Matrix rotateZ(double angle) {
    return rotationZ(angle).times(this);
  }

  public static Matrix rotationZ(double angle) {
    return rotation(angle, 0, 1);
  }

  private static Matrix rotation(double angle, int index1, int index2) {
    double cosa = Math.cos(angle);
    double sina = Math.sin(angle);
    Matrix r = identity();
    r.set(index1, index1, cosa);
    r.set(index2, index2, cosa);
    r.set(index1, index2, -sina);
    r.set(index2, index1, sina);
    return r;
  }

  // Fluently append shear to this transform (premultiply).
  public Matrix shear(double xy, double xz, double yx, double yz, double zx, double zy) {
    return shearing(xy, xz, yx, yz, zx, zy).times(this);
  }

  public static Matrix shearing(double xy, double xz, double yx, double yz, double zx, double zy) {
    Matrix s = identity();
    s.set(0, 1, xy);
    s.set(0, 2, xz);
    s.set(1, 0, yx);
    s.set(1, 2, yz);
    s.set(2, 0, zx);
    s.set(2, 1, zy);
    return s;
  }

  public static Matrix viewTransform(Tuple fromP, Tuple toP, Tuple upV) {
    Tuple forwardV = toP.minus(fromP).normalize();
    Tuple leftV = forwardV.cross(upV.normalize());
    Tuple trueUpV = leftV.cross(forwardV);

    Matrix orientation =
        Matrix.create(
            4,
            4,
            new double[] {
              leftV.x(),
              leftV.y(),
              leftV.z(),
              0,
              trueUpV.x(),
              trueUpV.y(),
              trueUpV.z(),
              0,
              -forwardV.x(),
              -forwardV.y(),
              -forwardV.z(),
              0,
              0,
              0,
              0,
              1
            });
    Matrix translation = Matrix.translation(-fromP.x(), -fromP.y(), -fromP.z());
    return orientation.times(translation);
  }

  public static Matrix fromTuple(Tuple t) {
    Matrix m = new Matrix(4, 1);
    m.set(0, 0, t.x());
    m.set(1, 0, t.y());
    m.set(2, 0, t.z());
    m.set(3, 0, t.w());
    return m;
  }

  public Tuple toTuple() {
    if (numRows != 4 || numCols != 1) {
      throw new IllegalArgumentException("Matrix must be of size [4,1] to convert to Tuple.");
    }
    return Tuple.create(get(0, 0), get(1, 0), get(2, 0), get(3, 0));
  }

  private void init(double[] initValues) {
    if (initValues.length != values.length) {
      throw new IllegalArgumentException(
          String.format(
              "initValues has incorrect length - expecting %d, received %d",
              values.length, initValues.length));
    }
    for (int i = 0; i < values.length; ++i) {
      values[i] = initValues[i];
    }
  }

  public int getNumRows() {
    return numRows;
  }

  public int getNumCols() {
    return numCols;
  }

  private IntStream rowStream() {
    return IntStream.range(0, numRows);
  }

  public void forEachRow(IntConsumer consumer) {
    rowStream().forEach(consumer);
  }

  private IntStream colStream() {
    return IntStream.range(0, numCols);
  }

  public void forEachCol(IntConsumer consumer) {
    colStream().forEach(consumer);
  }

  @FunctionalInterface
  public interface IntBiConsumer {
    void accept(int i1, int i2);
  }

  public void forEachCell(IntBiConsumer consumer) {
    forEachCol(col -> forEachRow(row -> consumer.accept(row, col)));
  }

  private boolean isIndexInRange(int row, int col) {
    return (row >= 0 && row < numRows && col >= 0 && col < numCols);
  }

  private int index(int row, int col) {
    // row-major order.
    return row * numCols + col;
  }

  public double get(int row, int col) {
    if (isIndexInRange(row, col)) {
      return values[index(row, col)];
    } else {
      throw new IndexOutOfBoundsException(
          String.format("(%d, %d) ouf of bounds for matrix", row, col));
    }
  }

  public void set(int row, int col, double value) {
    if (isIndexInRange(row, col)) {
      values[index(row, col)] = value;
    } else {
      throw new IndexOutOfBoundsException(
          String.format("(%d, %d) ouf of bounds for matrix", row, col));
    }
  }

  public Matrix times(Matrix b) {
    if (getNumCols() != b.getNumRows()) {
      throw new IllegalArgumentException("Incompatible matrix size for times()");
    }
    Matrix product = new Matrix(getNumRows(), b.getNumCols());

    // for each cell, multiply my row by b's column
    final int size = getNumCols();
    product.forEachCell(
        (row, col) -> {
          double value = 0.0;
          for (int i = 0; i < size; ++i) {
            value += get(row, i) * b.get(i, col);
          }
          product.set(row, col, value);
        });
    return product;
  }

  public Tuple times(Tuple t) {
    return times(Matrix.fromTuple(t)).toTuple();
  }

  public Matrix transpose() {
    Matrix t = new Matrix(numCols, numRows);
    forEachCell((fromRow, fromCol) -> t.set(fromCol, fromRow, get(fromRow, fromCol)));
    return t;
  }

  public double determinant() {
    if (numRows != numCols) {
      throw new IllegalArgumentException("Determinant requires square matrix");
    }
    if (numRows == 2) {
      return get(0, 0) * get(1, 1) - get(0, 1) * get(1, 0);
    }
    return colStream().mapToDouble(col -> get(0, col) * cofactor(0, col)).sum();
  }

  // Minor is determiniant of the submatrix.
  public double minor(int row, int col) {
    return submatrix(row, col).determinant();
  }

  public double cofactor(int row, int col) {
    double phase = ((row + col) % 2 == 1) ? -1 : 1;
    return minor(row, col) * phase;
  }

  // Returns matrix removing row and col.
  public Matrix submatrix(int removeRow, int removeCol) {
    if (!isIndexInRange(removeRow, removeCol)) {
      throw new IndexOutOfBoundsException(
          String.format("(%d, %d) ouf of bounds for matrix", removeRow, removeCol));
    }
    Matrix s = Matrix.create(numRows - 1, numCols - 1);
    s.forEachCell(
        (row, col) -> {
          // Skip one row/col if at or beyond removed row/col.
          int rowOffset = (row >= removeRow) ? 1 : 0;
          int colOffset = (col >= removeCol) ? 1 : 0;
          s.set(row, col, get(row + rowOffset, col + colOffset));
        });
    return s;
  }

  // Returns a new matrix with the given row zeroed.
  // Used to zero the 'w' row of a 4x4 matrix.
  public Matrix zeroRow(int row) {
    Matrix m = Matrix.create(numRows, numCols, values);
    m.forEachCol(col -> m.set(row, col, 0));
    return m;
  }

  public boolean isInvertible() {
    return determinant() != 0.0;
  }

  public Matrix invert() {
    if (numRows != numCols) {
      throw new IllegalArgumentException("Invert requires square matrix");
    }
    double determinant = determinant();
    if (determinant == 0.0) {
      throw new IllegalArgumentException("Can't invert non-invertible matrix");
    }
    Matrix i = Matrix.create(numCols, numRows);
    forEachCell(
        (row, col) -> {
          i.set(row, col, cofactor(col, row) / determinant);
        });
    return i;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    Matrix other = (Matrix) obj;
    return (this.numRows == other.numRows)
        && (this.numCols == other.numCols)
        && Arrays.equals(this.values, other.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(numRows, numCols, values);
  }
}
