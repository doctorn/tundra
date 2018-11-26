package net.tundra.core.scene;

import net.tundra.core.Game;

public class Event extends SceneComponent {
  private Runnable action;
  private float timeout, current;
  private boolean repeating;

  public Event(Runnable action, int timeout, boolean repeating) {
    this.action = action;
    this.timeout = timeout;
    this.current = timeout;
    this.repeating = repeating;
  }

  @Override
  public void update(Game game, float delta) {
    current -= delta;
    while (current < 0) {
      action.run();
      if (repeating) current += timeout;
      else {
        kill();
        break;
      }
    }
  }
}
