package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.scene.Camera;

public class TestCamera extends Camera {
  public void update(Game game, int delta) {
    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_P)) togglePerspective();
  }
}
