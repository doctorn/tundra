package net.tundra.core.scene;

import net.tundra.core.Game;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Camera {
  private float fov;
  private boolean perspective;
  private Vector3f position, target, up;

  public Camera() {
    position = new Vector3f(0, 0, 2);
    target = new Vector3f(0, 0, 0);
    up = new Vector3f(0, 1, 0);
    fov = 45.0f;
    perspective = true;
  }

  public Camera(Vector3f position, Vector3f target, Vector3f up, float fov) {
    this.position = position;
    this.target = target;
    this.up = up;
    this.fov = fov;
  }

  public Matrix4f getViewProjectionMatrix(int width, int height) {
    if (perspective) {
      return new Matrix4f()
          .perspective((float) Math.toRadians(fov), (float) width / height, 0.01f, 100f)
          .lookAt(position, target, up);
    } else {
      return new Matrix4f()
          .ortho(-(float) width / height, (float) width / height, -1, 1, 0.01f, 100f)
          .lookAt(position, target, up);
    }
  }

  public void setPosition(Vector3f position) {
    this.position = position;
  }

  public void togglePerspective() {
    perspective = !perspective;
  }

  public void setFOV(float fov) {
    this.fov = fov;
  }

  public void setTarget(Vector3f target) {
    this.target = target;
  }

  public void setUp(Vector3f up) {
    this.up = up;
  }

  public Vector3f getPosition() {
    return new Vector3f(position);
  }

  public Vector3f getTarget() {
    return new Vector3f(target);
  }

  public Vector3f getUp() {
    return new Vector3f(up);
  }

  public boolean perspectiveEnabled() {
    return perspective;
  }

  public float getFOV() {
    return fov;
  }

  public abstract void update(Game game, int delta);
}
