package net.tundra.core.scene;

import org.joml.Vector3f;

public abstract class TrackingCamera extends Camera {
  private Trackable tracked;
  private Vector3f offset;

  public TrackingCamera(Trackable tracked, Vector3f offset) {
    super();
    this.tracked = tracked;
    this.offset = offset;
  }

  @Override
  public Vector3f getPosition() {
    return tracked.getPosition().add(offset);
  }

  @Override
  public Vector3f getTarget() {
    return tracked.getPosition();
  }
}
