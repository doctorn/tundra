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
import net.tundra.core.scene.OrbitalCamera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TestGame extends Game {
  private Model model, model2;
  private Animation android;
  private Camera camera, camera2, active;
  private float angle;
  private FixedLight flash;
  private boolean flashOn;

  public TestGame() {
    super(800, 600, "tundra", false);
  }

  @Override
  public void init() throws TundraException {
    camera = new OrbitalCamera(new Vector3f(0, 0, -1), 10f);
    camera2 = new TestCamera();
    addCamera(camera);
    addCamera(camera2);
    active = camera2;

    addLight(new FixedLight(1, 0, 0, 0, 0, 1));
    addLight(new FixedLight(-1, 0, 0, 1, 0, 0));
    addLight(new FixedLight(0, 10, -4, 1, 1, 1));
    flash = new FixedLight(new Vector3f(0, 1, -2), new Vector3f(1, 1, 0), 0, 0, 1);

    android = new Animation(new SpriteSheet("res/android.png", 24, 24), 0, 3, 5, 3, true, 10);
    android.start();

    // model = new Cube(false);
    model2 = new Plane();
  }

  @Override
  public void update(int delta) throws TundraException {
    angle += 0.001f * delta;
    android.update(delta);
    if (angle > 1f && flashOn) {
      removeLight(flash);
      angle = 0;
      flashOn = !flashOn;
    } else if (angle > 1f && !flashOn) {
      addLight(flash);
      angle = 0;
      flashOn = !flashOn;
    }

    if (getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_C)) {
      if (active == camera) active = camera2;
      else active = camera;
    }

    if (getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_D)) toggleDebug();
    if (getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_L)) toggleLighting();
  }

  @Override
  public void render(Graphics g) throws TundraException {
    g.use(active);
    for (int i = -5; i < 5; i++) {
      Matrix4f transform = new Matrix4f().translate(new Vector3f(i, 0, -2)).scale(0.5f);
      g.drawModel(model2, android.currentFrame(), transform);
    }
    g.drawModel(
        model2,
        new Matrix4f()
            .translate(0, -0.5f, -2f)
            .scale(20f)
            .rotate(-(float) Math.PI / 2f, new Vector3f(1, 0, 0)));
    // g.drawModel(model2, transform);
    // g.drawModelWireframe(model2, transform);
  }

  public static void main(String args[]) throws TundraException {
    TestGame test = new TestGame();
    test.start();
  }
}
