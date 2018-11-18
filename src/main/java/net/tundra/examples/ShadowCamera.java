package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.scene.Camera;
import org.joml.Matrix4f;

public final class ShadowCamera extends Camera {
  public ShadowCamera() {
    super();
    // togglePerspective();
  }

  @Override
  public Matrix4f getViewProjectionMatrix(int width, int height) {
    return new Matrix4f()
        .ortho(-10f, 10f, -10f, 10f, 0.01f, 100f)
        .lookAt(getPosition(), getTarget(), getUp());
  }

  @Override
  public void update(Game game, int delta) {}
}
