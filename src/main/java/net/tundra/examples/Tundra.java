package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.GameState;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.sprites.Sprite;
import org.joml.Vector3f;

public class Tundra extends GameState {
  private static Sprite LOGO;

  @Override
  public void init(Game game) throws TundraException {
    LOGO = new Sprite("res/tundra.png").scale(5f);
    setLighting(false);
    after(
        3000,
        () -> {
          game.enterState(1);
        });
  }

  @Override
  public void update(Game game, float delta) throws TundraException {}

  @Override
  public void render(Game game, Graphics graphics) throws TundraException {
    graphics.setClearColour(new Vector3f(1f, 1f, 1f));
    graphics.drawImage(
        LOGO,
        game.getWidth() / 2 - LOGO.getWidth() / 2,
        game.getHeight() / 2 - LOGO.getHeight() / 2);
  }

  @Override
  public int getID() {
    return 0;
  }
}
