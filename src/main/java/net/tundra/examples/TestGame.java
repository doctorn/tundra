package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Cube;
import net.tundra.core.resources.models.Model;
import net.tundra.core.resources.models.Plane;
import net.tundra.core.resources.shaders.FragmentShader;
import net.tundra.core.resources.shaders.Program;
import net.tundra.core.resources.shaders.VertexShader;
import net.tundra.core.resources.sprites.Animation;
import net.tundra.core.resources.sprites.SpriteSheet;
import net.tundra.core.scene.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TestGame extends Game {
  private Program program;
  private Model model, model2;
  private Animation android;
  private Camera camera;
  private float angle;

  public TestGame() {
    super(800, 600, "tundra", false);
  }

  @Override
  public void init() throws TundraException {
    camera = new TestCamera();
    VertexShader vertex = new VertexShader("shaders/vert.glsl");
    FragmentShader fragment = new FragmentShader("shaders/frag.glsl");
    program = new Program(vertex, fragment);
    vertex.delete();
    fragment.delete();

    android = new Animation(new SpriteSheet("res/android.png", 24, 24), 0, 3, 5, 3, true, 10);
    android.start();

    model = new Cube(false);
    model2 = new Plane();
  }

  @Override
  public void update(int delta) throws TundraException {
    camera.update(this, delta);
    angle += 0.001f * delta;
    android.update(delta);
  }

  @Override
  public void render(Graphics g) throws TundraException {
    g.activate(program);
    g.use(camera);
    // Matrix4f transform = new Matrix4f().scale(0.5f).rotate(angle, new Vector3f(0, 1, 0));
    // g.drawModel(model2, android.currentFrame(), transform);
    // g.drawModel(model2, transform);
    // g.drawModelWireframe(model2, transform);

    if (getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_P)) camera.togglePerspective();

    g.drawModel(
        model,
        new Matrix4f()
            .scale(0.5f)
            .rotate(angle + (float) Math.PI, new Vector3f(0, 1, 0))
            .rotate(angle, new Vector3f(1, 0, 0)));
  }

  public static void main(String args[]) throws TundraException {
    TestGame test = new TestGame();
    test.start();
  }
}
