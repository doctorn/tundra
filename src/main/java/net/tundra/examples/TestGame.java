package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.FragmentShader;
import net.tundra.core.resources.Program;
import net.tundra.core.resources.VertexShader;

public class TestGame extends Game {
  private Program program;

  public TestGame() {
    super(800, 600, "tundra", false);
  }

  @Override
  public void init() throws TundraException {
    VertexShader vertex = new VertexShader("shaders/vert.glsl");
    FragmentShader fragment = new FragmentShader("shaders/frag.glsl");
    program = new Program(vertex, fragment);
    vertex.delete();
    fragment.delete();
  }

  @Override
  public void update(int delta) throws TundraException {}

  @Override
  public void render(Graphics g) throws TundraException {
    g.activate(program);
  }

  public static void main(String args[]) throws TundraException {
    TestGame test = new TestGame();
    test.start();
  }
}
