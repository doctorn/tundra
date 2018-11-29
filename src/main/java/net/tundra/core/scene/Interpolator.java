package net.tundra.core.scene;

import java.util.function.Consumer;
import net.tundra.core.Game;

public class Interpolator extends SceneComponent {
  private float timeout, current = 0, start, end;
  private Consumer<Float> action;
  private Runnable callback;

  public Interpolator(
      Consumer<Float> action, float start, float end, int timeout, Runnable callback) {
    this.timeout = timeout;
    this.action = action;
    this.start = start;
    this.end = end;
    this.callback = callback;
  }

  public Interpolator(Consumer<Float> action, float start, float end, int timeout) {
    this(action, start, end, timeout, null);
  }

  @Override
  public void update(Game game, float delta) {
    current += delta;
    if (current > timeout) {
      action.accept(end);
      kill();
    } else action.accept(start + (end - start) * current / timeout);
  }

  @Override
  public void die(Game game) {
    if (callback != null) callback.run();
  }
}
