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
import net.tundra.core.scene.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Arrays;

public class TestGame extends Game {
  private Program program;
  private Model model, model2;
  private Animation android;
  private Camera camera;
  private float angle;
  private Light[] lights;

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

    lights = new Light[16];

    for(int i = 0; i < lights.length; i++) {
      lights[i] = new Light();
    }

    lights[0] = new Light(1, 0, 0, 0, 0, 1);
    lights[1] = new Light(-1, 0, 0, 1, 0, 0);

    android = new Animation(new SpriteSheet("res/timothy.png", 24, 24), 0, 3, 5, 3, true, 10);
    android.start();

    model = new Cube(false);
    model2 = new Plane();
  }

  @Override
  public void update(int delta) throws TundraException {
    camera.update(delta);
    angle += 0.001f * delta;
    android.update(delta);
  }

  @Override
  public void render(Graphics g) throws TundraException {
    g.activate(program);
    g.use(camera, lights);
    Matrix4f transform = new Matrix4f().scale(0.5f);//.rotate(angle, new Vector3f(0, 1, 0));
    Matrix4f move = new Matrix4f().translate(new Vector3f((float)Math.sin(angle),0,-2));

    //g.drawModel(model2, android.currentFrame(), transform, move);
    g.drawModel(model, transform, move);
    //g.drawModelWireframe(model2, transform, move);

    // g.drawModel(
    //     model,
    //     new Matrix4f()
    //         .scale(0.1f)
    //         .rotate(angle + (float) Math.PI, new Vector3f(0, 1, 0))
    //         .rotate(angle, new Vector3f(1, 0, 0)));
  }

  public static void main(String args[]) throws TundraException {
    TestGame test = new TestGame();
    test.start();
  }
}
