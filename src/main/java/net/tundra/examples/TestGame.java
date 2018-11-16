package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Model;
import net.tundra.core.resources.models.Plane;
import net.tundra.core.resources.sprites.Animation;
import net.tundra.core.resources.sprites.SpriteSheet;
import net.tundra.core.scene.Camera;
import net.tundra.core.scene.FixedLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TestGame extends Game {
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

    addLight(new FixedLight(1, 0, 0, 0, 0, 1));
    addLight(new FixedLight(-1, 0, 0, 1, 0, 0));

    android = new Animation(new SpriteSheet("res/android.png", 24, 24), 0, 3, 5, 3, true, 10);
    android.start();

    // model = new Cube(false);
    model2 = new Plane();
  }

  @Override
  public void update(int delta) throws TundraException {
    angle += 0.001f * delta;
    android.update(delta);
  }

  @Override
  public void render(Graphics g) throws TundraException {
    g.use(camera);
    Matrix4f transform =
        new Matrix4f().scale(0.5f).translate(new Vector3f((float) Math.sin(angle), 0, -2));

    g.drawModel(model2, android.currentFrame(), transform);
    // g.drawModel(model2, transform);
    // g.drawModelWireframe(model2, transform);
  }

  public static void main(String args[]) throws TundraException {
    TestGame test = new TestGame();
    test.start();
  }
}
