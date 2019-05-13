package raytracer;

// A Transformable Intersectable with a Material.
public interface Shape extends Intersectable {
  public Matrix transform();

  public void setTransform(Matrix transform);

  public Material material();

  public void setMaterial(Material m);
}
