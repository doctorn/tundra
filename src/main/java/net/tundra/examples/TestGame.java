package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;

public class TestGame extends Game {
  public TestGame() {
    super(800, 600, "tundra", false);
  }

  @Override
  public void init() throws TundraException {}

  @Override
  public void update(int delta) throws TundraException {}

  @Override
  public void render(Graphics g) throws TundraException {}

  public static void main(String args[]) throws TundraException {
    TestGame test = new TestGame();
    test.start();
  }
}
