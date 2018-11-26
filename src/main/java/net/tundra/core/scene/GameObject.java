package net.tundra.core.scene;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;

public abstract class GameObject extends SceneComponent {
  public abstract void render(Game game, Graphics graphics) throws TundraException;
}
