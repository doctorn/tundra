package net.tundra.core.scene;

import java.util.function.Consumer;
import net.tundra.core.Game;

public class Interpolator extends SceneComponent {
  private float timeout, current = 0, start, end;
  private Consumer<Float> action;

  public Interpolator(Consumer<Float> action, float start, float end, int timeout) {
    this.timeout = timeout;
    this.action = action;
    this.start = start;
    this.end = end;
  }

  @Override
  public void update(Game game, float delta) {
    current += delta;
    if (current > timeout) {
      action.accept(end);
      kill();
    } else action.accept(start + (end - start) * current / timeout);
  }
}
