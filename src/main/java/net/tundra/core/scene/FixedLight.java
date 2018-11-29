package net.tundra.core.scene;

import net.tundra.core.Game;
import org.joml.Vector3f;

public final class FixedLight extends Light {
  public FixedLight() {
    super();
  }

  public FixedLight(float x, float y, float z, float r, float g, float b) {
    super(x, y, z, r, g, b);
  }

  public FixedLight(Vector3f direction, Vector3f colour) {
    super(direction, colour);
  }

  public FixedLight(
      Vector3f position, Vector3f colour, float constant, float linear, float quadratic) {
    super(position, colour, constant, linear, quadratic);
  }

  public FixedLight(
      Vector3f position,
      Vector3f colour,
      Vector3f direction,
      float constant,
      float linear,
      float quadratic,
      float theta) {
    super(position, colour, direction, constant, linear, quadratic, theta);
  }

  @Override
  public void update(Game game, float delta) {}
}
