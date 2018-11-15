package net.tundra.core.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Camera {
  private float fov;
  private Vector3f position, target, up;

  public Camera() {
    position = new Vector3f(0, 0, 2);
    target = new Vector3f(0, 0, 0);
    up = new Vector3f(0, 1, 0);
    fov = 45.0f;
  }

  public Matrix4f getViewProjectionMatrix(int width, int height) {
    return new Matrix4f()
        .perspective((float) Math.toRadians(fov), (float) width / height, 0.01f, 100f)
        .lookAt(position, target, up);
  }

  public Vector3f getPosition() {
    return position;
  }

  public abstract void update(int delta);
}
