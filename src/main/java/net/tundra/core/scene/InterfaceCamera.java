package net.tundra.core.scene;

import net.tundra.core.Game;
import org.joml.Matrix4f;

public final class InterfaceCamera extends Camera {
  public InterfaceCamera() {
    super();
    togglePerspective();
  }

  @Override
  public Matrix4f getViewProjectionMatrix(int width, int height) {
    return new Matrix4f().ortho(0, width, 0, height, -1f, 1f);
  }

  @Override
  public void update(Game game, float delta) {}
}
