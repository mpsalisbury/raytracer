package raytracer;

// A Transformable Intersectable with a Material.
public interface Shape extends Intersectable {
  public Matrix transform();

  public void setTransform(Matrix transform);

  public Material material();

  public void setMaterial(Material m);

  // Returns a shape that geometrically bounds this shape but is simpler to detect hits against.
  //  public Shape boundingShape();
}
