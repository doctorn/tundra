package net.tundra.core;

import net.tundra.core.graphics.Graphics;

public abstract class GameObject {
  public abstract void update(Game game, int delta);

  public abstract void render(Game game, Graphics graphics);
}
