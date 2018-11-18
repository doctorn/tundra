package net.tundra.core.scene;

import net.tundra.core.Game;

public class TrackingCamera extends Camera {
  private Trackable tracked;

  public TrackingCamera(Trackable tracked) {
    super();
    this.tracked = tracked;
  }

  @Override
  public void update(Game game, int delta) {
    setPosition(tracked.getPosition());
  }
}
