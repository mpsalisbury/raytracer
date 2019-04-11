package raytracer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class World {

  private static final int DEFAULT_MAX_BOUNCES = 4;

  private List<Light> lights = new ArrayList<>();
  // TODO: Use Group?
  private List<Shape> shapes = new ArrayList<>();


  public Iterable<Light> getLights() {
    return lights;
  }

  public void addLight(Light light) {
    lights.add(light);
  }

  public void clearLights() {
    lights.clear();
  }

  public Iterable<Shape> getShapes() {
    return shapes;
  }

  public void addShape(Shape shape) {
    shapes.add(shape);
  }

  public Intersections intersect(Ray ray) {
    //return new Intersections(shapes.stream().flatMap(s -> s.intersectStream(ray)));
    return Intersections.create(shapes.stream().flatMap(s -> s.intersectStream(ray)));
  }

  public Color shadeHit(Intersection i, int remainingBounces) {
    Material material = i.material();

    Color surfaceC = Color.BLACK;
    for (Light light : lights) {
      //      boolean shadow = isShadowed(i.point(), light);
      Color visibleLightC = visibleLightColor(i.point(), light);
      surfaceC =
          surfaceC.plus(material.lighting(light, i.point(), i.eyev(), i.normalv(), visibleLightC));
    }
    Color reflectedC = reflectedColor(i, remainingBounces);
    Color refractedC = refractedColor(i, remainingBounces);

    if (material.reflectivity() > 0 && material.transparency() > 0) {
      double reflectance = i.schlickReflectance();
      return surfaceC.plus(reflectedC.times(reflectance)).plus(refractedC.times(1 - reflectance));
    } else {
      return surfaceC.plus(reflectedC).plus(refractedC);
    }
  }

  public Color reflectedColor(Intersection i, int remainingBounces) {
    if (remainingBounces <= 0) {
      return Color.BLACK;
    }
    double reflectivity = i.material().reflectivity();
    if (reflectivity == 0) {
      Color c = Color.BLACK;
    }
    Ray r = Ray.create(i.point(), i.reflectv());
    Color reflectColor = colorAt(r.bumpForward(), remainingBounces - 1);
    return reflectColor.times(reflectivity);
  }

  public Color refractedColor(Intersection i, int remainingBounces) {
    if (remainingBounces <= 0) {
      return Color.BLACK;
    }
    double transparency = i.material().transparency();
    if (transparency == 0) {
      return Color.BLACK;
    }
    if (i.isTotalInternalReflection()) {
      return Color.BLACK;
    }

    // TODO: This should incorporate object's internal color.

    Ray refractRay = Ray.create(i.point(), i.refractv()).bumpForward();
    return colorAt(refractRay, remainingBounces - 1).times(i.material().transparency());
  }

  public Color colorAt(Ray ray) {
    return colorAt(ray, DEFAULT_MAX_BOUNCES);
  }

  public Color colorAt(Ray ray, int remainingBounces) {
    Optional<Intersection> i = intersect(ray).hit();
    if (i.isPresent()) {
      return shadeHit(i.get(), remainingBounces);
    } else {
      return Color.BLACK;
    }
  }

  private static final double EPSILON = 1.0e-5;

  // Return color of light visible at point due to intervening
  // objects and their transparency.
  public Color visibleLightColor(Tuple point, Light light) {
    Tuple lightToPoint = point.minus(light.position());
    double distanceToPoint = lightToPoint.magnitude();
    Ray rayToPoint = Ray.create(light.position(), lightToPoint.normalize());
    List<Intersection> hits = intersect(rayToPoint).shadowHits().collect(Collectors.toList());
    Color c = light.intensity();
    for (Intersection i : hits) {
      if (i.t() < distanceToPoint - EPSILON) {
        // object surface is between point and light, changes light color.
        Material m = i.material();
        // TODO: this should use the object's internal color, not surface color.
        // Sqrt because we hit the object twice, once at each surface.
        Color objectC = m.pattern().colorAt(i.point()).times(m.transparency()).sqrt();
        c = c.times(objectC);
      }
    }
    return c;
  }

  public boolean isShadowed(Tuple point, Light light) {
    Tuple lightToPoint = point.minus(light.position());
    double distanceToPoint = lightToPoint.magnitude();
    Ray rayToPoint = Ray.create(light.position(), lightToPoint.normalize());
    Optional<Intersection> hit = intersect(rayToPoint).shadowHit();
    return hit.isPresent() && hit.get().t() < distanceToPoint - EPSILON;
  }
}
