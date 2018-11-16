package net.tundra.core.scene;

import net.tundra.core.Game;
import org.joml.Vector3f;

public class TrackingLight extends Light {
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
  public void update(Game game, int delta) {
    setPosition(tracked.getPosition());
    if (tracked instanceof SceneComponent) {
      if (((SceneComponent) tracked).dying()) kill();
    }
  }
}
