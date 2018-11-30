package net.tundra.examples;

import com.bulletphysics.collision.dispatch.CollisionObject;
import net.tundra.core.Game;
import net.tundra.core.GameState;
import net.tundra.core.TundraException;
import net.tundra.core.audio.Listener;
import net.tundra.core.audio.Sound;
import net.tundra.core.audio.TrackingListener;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Model;
import net.tundra.core.resources.sprites.Animation;
import net.tundra.core.resources.sprites.Font;
import net.tundra.core.resources.sprites.SpriteSheet;
import net.tundra.core.scene.Camera;
import net.tundra.core.scene.FixedLight;
import net.tundra.core.scene.OrbitalCamera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TestGameState extends GameState {
  private Animation android;
  private OrbitalCamera camera;
  private FPSCamera camera2;
  private Listener listener1;
  private TrackingListener trackingListener;
  private Font font;
  public static Model MONKEY, DODGE, DREDD;
  private Box player;

  @Override
  public void init(Game game) throws TundraException {
    MONKEY = new Model("res/suzanne.obj");
    // DODGE = new Model("res/dodge-challenger_model.obj");
    DREDD = new Model("res/vader.obj");
    // toggleDebug();

    player = new Box(game, new Vector3f(0, -3.5f, 0), new Vector3f(1f, 0f, 1f));
    player.getBody().setActivationState(CollisionObject.DISABLE_DEACTIVATION);
    addObject(player);
    camera = new OrbitalCamera(new Vector3f(-5, -1.5f, 5), 10f);
    camera2 = new FPSCamera(player, new Vector3f(-0.06f, 0.06f, 0));
    addCamera(camera);
    addCamera(camera2);
    activate(camera);

    listener1 = new Listener(new Vector3f(0, 0, 0), 1);
    addListener(listener1);
    activateListener(listener1);
    trackingListener = new TrackingListener(1, camera, new Vector3f(0, 0, 0));
    addListener(trackingListener);

    FixedLight main = new FixedLight(new Vector3f(-1f, -1f, -1f), new Vector3f(0.6f, 0.6f, 0.6f));
    addLight(new FixedLight(4, 0, 0, 0, 0, 1));
    addLight(new FixedLight(-4, 0, 0, 1, 0, 0));
    // addLight(new FixedLight(-2, 0, -4, 1, 1, 0));
    addLight(main);
    android = new Animation(new SpriteSheet("res/android.png", 24, 24), 0, 3, 5, 3, true, 10);
    android.start();

    SpriteSheet fontSheet = new SpriteSheet("res/font.png", 20, 22);
    font = new Font(fontSheet);
    game.getInput().setMouseGrabbed(true);

    Camera shadow = new ShadowCamera(player, new Vector3f(25, 25, 25));
    enableShadowMapping(shadow, main);
    addCamera(shadow);

    setGravity(new Vector3f(0, -10, 0));

    addObject(new Floor());

    /*
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        for (int k = 0; k < 8; k++)
          addObject(
              new Box(game, new Vector3f(5f + i * 0.41f, -4f + 0.41f + j * 0.4f, 5f + k * 0.4f)));
      }
    }
    */

    // Interpolator lerping = lerp(10000, f -> camera.setDistance(f), camera.getDistance(), 50f);
    // every(1000, () -> toggleLighting());
    // after(2000, () -> lerping.kill());
  }

  @Override
  public void update(Game game, float delta) throws TundraException {
    android.update(delta);

    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_C)) {
      if (getCamera() == camera) activate(camera2);
      else activate(camera);
    }

    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_R)) toggleRendering();
    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_V))
      getCamera().togglePerspective();
    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_T)) toggleDebug();
    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_L)) toggleLighting();
    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_P)) togglePhysics();
    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_1)) game.setTimescale(0.1f);
    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_2)) game.setTimescale(1f);
    if (game.getInput().isKeyDown(org.lwjgl.input.Keyboard.KEY_3)) camera2.shake(100f);

    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_M)) {
      Sound sound = new Sound("res/beep.wav");
      addSound(sound);
      after(1000, sound::kill);
      sound.play();
    }

    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_N)) {
      Sound sound =
          new Sound("res/beep.wav", new Vector3f(-5, 0, 1), new Vector3f(1, 0, 0), 1, 0.05f, true);
      lerp(1000, t -> sound.setPosition(new Vector3f(t, 0, 0)), -5, 5);
      after(2000, sound::kill);
      addSound(sound);
      sound.play();
    }

    if (game.getInput().isKeyPressed(org.lwjgl.input.Keyboard.KEY_COMMA)) {
      activateListener(getListener() == listener1 ? trackingListener : listener1);
    }

    if (game.getInput().isMouseButtonPressed(0))
      addObject(
          new Box(game, camera2.getPosition().add(camera2.getLook().mul(0.5f)), camera2.getLook()));
  }

  @Override
  public void render(Game game, Graphics graphics) throws TundraException {
    /*for (int i = -5; i < 5; i++) {
      Vector3f position = new Vector3f(i, -3.5f, -2);
      Matrix4f transform =
          new Matrix4f()
              .translate(position)
              .scale(0.5f)
              .lookAlong(getCamera().getLookAlong(), getCamera().getUp());
      if (i != 4) graphics.drawModel(Model.PLANE, android.currentFrame(), transform);
      else
        graphics.drawModelFlash(
            Model.PLANE, android.currentFrame(), transform, new Vector4f(1f, 1f, 1f, 1f));
    }*/

    graphics.drawModel(DREDD, new Matrix4f().translate(new Vector3f(-5f, -2f, 5f)));
    // graphics.setColour(new Vector4f(0f, 0f, 0f, 0.5f));
    // graphics.fillRect(0, 0, getWidth() / 2, getHeight() / 2);

    graphics.setColour(new Vector3f(1f, 1f, 1f));

    // graphics.drawString(game.getFPS() + " FPS", font, 10, 100);
    // graphics.drawString(getLights().size() + " LIGHTS", font, 10, 135);
  }

  public int getID() {
    return 1;
  }
}
