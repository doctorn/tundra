package net.tundra.core;

import static org.lwjgl.opengl.GL11.*;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.lwjgl.openal.AL;
import net.tundra.core.audio.Listener;
import net.tundra.core.audio.Sound;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.scene.Camera;
import net.tundra.core.scene.Event;
import net.tundra.core.scene.GameObject;
import net.tundra.core.scene.Interpolator;
import net.tundra.core.scene.Light;
import net.tundra.core.scene.PhysicsObject;
import net.tundra.core.scene.SceneComponent;
import org.joml.Vector3f;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class Game {
  private Input input;
  private int width, height;
  private String title;
  private float timescale = 1f;
  private boolean fullscreen,
      debug = false,
      lighting = true,
      shadowMapping = false,
      physics = false;
  private Graphics graphics;
  private List<Light> lights = new ArrayList<>();
  private List<Camera> cameras = new ArrayList<>();
  private List<GameObject> objects = new ArrayList<>();
  private List<SceneComponent> events = new ArrayList<>();
  private Camera active, shadowCamera;
  private Light shadowLight;
  private DynamicsWorld dynamics;

  private List<Sound> sounds = new ArrayList<>();
  private List<Listener> listeners = new ArrayList<>();
  private Listener activeListener;

  private int fps = 0, frameCount = 0, cumulativeDelta = 0;

  public Game(int width, int height, String title, boolean fullscreen) {
    this.width = width;
    this.height = height;
    this.title = title;
    this.fullscreen = fullscreen;
  }

  public void start() throws TundraException {
    try {
      if (!fullscreen) Display.setDisplayMode(new DisplayMode(width, height));
      else Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
      Display.setTitle(title);
      Display.setFullscreen(fullscreen);
      Display.create();
      graphics = new Graphics(this);
      input = new Input();
      AL.create();
      initPhysics();
      init();
    } catch (LWJGLException e) {
      throw new TundraException("Failed to initialise game window", e);
    }

    loop();
  }

  public void initPhysics() {
    BroadphaseInterface broadphase = new DbvtBroadphase();
    CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
    CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
    ConstraintSolver solver = new SequentialImpulseConstraintSolver();
    dynamics = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
  }

  public void loop() throws TundraException {
    long timestamp = System.currentTimeMillis();
    do {
      int delta = (int) (System.currentTimeMillis() - timestamp);
      timestamp += delta;
      frameCount++;
      cumulativeDelta += delta;
      if (frameCount == 10) {
        frameCount = 0;
        fps = 10000 / cumulativeDelta;
        cumulativeDelta = 0;
      }

      float realDelta = (float) delta * timescale;

      input.update();
      if (physics) dynamics.stepSimulation(realDelta / 1000f);
      for (GameObject object : objects) object.update(this, realDelta);
      for (Light light : lights) light.update(this, realDelta);
      for (Listener listener : listeners) listener.update(this, realDelta);
      activeListener.update(this, realDelta);
      for (Sound sound : sounds) sound.update(this, realDelta);
      for (Camera camera : cameras) camera.update(this, realDelta);
      for (SceneComponent event : events) event.update(this, realDelta);
      update(realDelta);

      cleanup(objects);
      cleanup(lights);
      cleanup(cameras);
      cleanup(events);
      cleanup(sounds);
      cleanup(listeners);

      graphics.setColour(new Vector3f(1f, 1f, 1f));
      for (GameObject object : objects) object.render(this, graphics);
      render(graphics);
      if (debug) {
        for (Light light : lights) light.renderDebug(this, graphics);
        for (Camera camera : cameras) camera.renderDebug(this, graphics);
        for (GameObject object : objects) {
          if (object instanceof PhysicsObject) ((PhysicsObject) object).renderDebug(this, graphics);
        }
      }
      graphics.render();

      checkError();
      Display.update();
    } while (!Display.isCloseRequested());
    AL.destroy();
  }

  public void checkError() throws TundraException {
    int error = glGetError();
    if (error != GL_NO_ERROR) {
      throw new TundraException("OpenGL errored with error code " + error);
    }
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getFPS() {
    return fps;
  }

  public void enableShadowMapping(Camera camera, Light light) {
    if (shadowLight != null) shadowLight.disableShadowMapping();
    light.enableShadowMapping();
    shadowLight = light;
    shadowCamera = camera;
    shadowMapping = true;
  }

  public void disableShadowMapping() {
    shadowMapping = false;
    shadowLight.disableShadowMapping();
  }

  public boolean shadowMapping() {
    return shadowMapping;
  }

  public Camera getShadowCamera() {
    return shadowCamera;
  }

  public Input getInput() {
    return input;
  }

  public List<Light> getLights() {
    return lights;
  }

  public void addLight(Light light) {
    lights.add(light);
  }

  public void addCamera(Camera camera) {
    cameras.add(camera);
  }

  public void addSound(Sound sound) {
    sounds.add(sound);
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void addObject(GameObject object) {
    objects.add(object);
    if (object instanceof PhysicsObject) {
      PhysicsObject physicsObject = (PhysicsObject) object;
      dynamics.addRigidBody(physicsObject.getBody());
    }
  }

  public void togglePhysics() {
    physics = !physics;
  }

  public void toggleDebug() {
    debug = !debug;
  }

  public void toggleLighting() {
    lighting = !lighting;
  }

  public boolean lightingEnabled() {
    return lighting;
  }

  public void setLighting(boolean lighting) {
    this.lighting = lighting;
  }

  public Event after(int timeout, Runnable action) {
    Event event = new Event(action, timeout, false);
    events.add(event);
    return event;
  }

  public Event every(int timeout, Runnable action) {
    Event event = new Event(action, timeout, true);
    events.add(event);
    return event;
  }

  public Interpolator lerp(int timeout, Consumer<Float> action, float start, float end) {
    Interpolator event = new Interpolator(action, start, end, timeout);
    events.add(event);
    return event;
  }

  public void activate(Camera camera) {
    active = camera;
  }

  public void activateListener(Listener listener) {
    activeListener = listener;
  }

  public Listener getListener() {
    return activeListener;
  }

  public Camera getCamera() {
    return active;
  }

  public void setGravity(Vector3f gravity) {
    dynamics.setGravity(new javax.vecmath.Vector3f(gravity.x, gravity.y, gravity.z));
  }

  public void setTimescale(float timescale) {
    this.timescale = timescale;
  }

  public float getTimescale() {
    return timescale;
  }

  public DynamicsWorld getDynamicsWorld() {
    return dynamics;
  }

  private void cleanup(List<? extends SceneComponent> components) {
    Iterator<? extends SceneComponent> it = components.iterator();
    while (it.hasNext()) {
      SceneComponent next = it.next();
      if (next.dying()) {
        next.die(this);
        it.remove();
      }
    }
  }

  public abstract void init() throws TundraException;

  public abstract void update(float delta) throws TundraException;

  public abstract void render(Graphics graphics) throws TundraException;
}
