package net.tundra.core.scene;

import net.tundra.core.Game;
import net.tundra.core.TundraException;

public abstract class SceneComponent {
  private boolean dying = false;

  public void kill() {
    dying = true;
  }

  public boolean dying() {
    return dying;
  }

  public abstract void update(Game game, int delta) throws TundraException;
}
