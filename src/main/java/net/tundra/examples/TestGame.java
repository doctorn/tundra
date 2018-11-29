package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.TundraException;

public class TestGame extends Game {
  public TestGame() {
    super(1920, 1080, "tundra", false);
  }

  @Override
  public void initStates() throws TundraException {
    addState(new Tundra());
    addState(new TestGameState());
    enterState(0);
  }

  public static void main(String args[]) throws TundraException {
    TestGame test = new TestGame();
    test.start();
  }
}
