package net.tundra.examples;

import net.tundra.core.scene.Trackable;
import net.tundra.core.scene.TrackingCamera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShadowCamera extends TrackingCamera {
  public ShadowCamera(Trackable tracked, Vector3f offset) {
    super(tracked, offset);
  }

  @Override
  public Matrix4f getViewProjectionMatrix(int width, int height) {
    return new Matrix4f()
        .ortho(-20f, 20f, -20f, 20f, 0.01f, 100f)
        .lookAt(getPosition(), getTarget(), getUp());
  }
}
