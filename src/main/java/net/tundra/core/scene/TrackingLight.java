package net.tundra.core.scene;

import org.joml.Vector3f;

public abstract class TrackingLight extends Light {
  private Trackable tracked;

  public TrackingLight(Trackable tracked, Vector3f colour) {
    this(tracked, colour, 1f, 1f, 1f);
  }

  public TrackingLight(
      Trackable tracked, Vector3f colour, float constant, float linear, float quadratic) {
    super(tracked.getPosition(), colour, constant, linear, quadratic);
    this.tracked = tracked;
  }

  @Override
  public Vector3f getPosition() {
    return tracked.getPosition();
  }
}
