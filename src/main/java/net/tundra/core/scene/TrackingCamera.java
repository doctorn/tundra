package net.tundra.core.scene;

import net.tundra.core.Game;
import org.joml.Vector3f;

public class TrackingCamera extends Camera {
  private Trackable tracked;
  private Vector3f offset;

  public TrackingCamera(Trackable tracked, Vector3f offset) {
    super();
    this.tracked = tracked;
    this.offset = offset;
  }

  @Override
  public void update(Game game, float delta) {
    setPosition(tracked.getPosition().add(offset));
  }
}
