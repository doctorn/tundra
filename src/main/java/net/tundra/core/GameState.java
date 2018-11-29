package net.tundra.core;

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

public abstract class GameState {
  private boolean debug = false,
      rendering = true,
      lighting = true,
      shadowMapping = false,
      physics = false;
  private List<Light> lights = new ArrayList<>(), bufferedLights = new ArrayList<>();
  private List<Camera> cameras = new ArrayList<>(), bufferedCameras = new ArrayList<>();
  private List<GameObject> objects = new ArrayList<>(), bufferedObjects = new ArrayList<>();
  private List<SceneComponent> events = new ArrayList<>(), bufferedEvents = new ArrayList<>();
  private List<Sound> sounds = new ArrayList<>(), bufferedSounds = new ArrayList<>();
  private List<Listener> listeners = new ArrayList<>(), bufferedListeners = new ArrayList<>();
  private Listener activeListener;
  private Camera active = Graphics.INTERFACE_CAMERA, shadowCamera;
  private Light shadowLight;
  private DynamicsWorld dynamics;

  public void initDefault(Game game) throws TundraException {
    initPhysics();
    init(game);
  }

  private void initPhysics() {
    BroadphaseInterface broadphase = new DbvtBroadphase();
    CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
    CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
    ConstraintSolver solver = new SequentialImpulseConstraintSolver();
    dynamics = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
  }

  public abstract void init(Game game) throws TundraException;

  public void updateDefault(Game game, float delta) throws TundraException {
    if (physics) dynamics.stepSimulation(delta / 1000f);
    for (GameObject object : objects) object.update(game, delta);
    for (Light light : lights) light.update(game, delta);
    for (Sound sound : sounds) sound.update(game, delta);
    for (Camera camera : cameras) camera.update(game, delta);
    for (SceneComponent event : events) event.update(game, delta);
    if (activeListener != null) activeListener.update(game, delta);

    objects.addAll(bufferedObjects);
    lights.addAll(bufferedLights);
    sounds.addAll(bufferedSounds);
    cameras.addAll(bufferedCameras);
    events.addAll(bufferedEvents);
    listeners.addAll(bufferedListeners);

    bufferedObjects = new ArrayList<>();
    bufferedLights = new ArrayList<>();
    bufferedSounds = new ArrayList<>();
    bufferedCameras = new ArrayList<>();
    bufferedListeners = new ArrayList<>();
    bufferedEvents = new ArrayList<>();

    cleanup(game, objects);
    cleanup(game, lights);
    cleanup(game, cameras);
    cleanup(game, events);
    cleanup(game, sounds);
    cleanup(game, listeners);

    update(game, delta);
  }

  public abstract void update(Game game, float delta) throws TundraException;

  public void renderDefault(Game game, Graphics graphics) throws TundraException {
    graphics.setColour(new Vector3f(1f, 1f, 1f));
    if (rendering) {
      for (GameObject object : objects) object.render(game, graphics);
      render(game, graphics);
    }
    if (debug) {
      for (Light light : lights) light.renderDebug(game, graphics);
      for (Camera camera : cameras) camera.renderDebug(game, graphics);
      for (GameObject object : objects) {
        if (object instanceof PhysicsObject) ((PhysicsObject) object).renderDebug(game, graphics);
      }
    }
  }

  public abstract void render(Game grame, Graphics graphics) throws TundraException;

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

  public List<Light> getLights() {
    return new ArrayList<>(lights);
  }

  public void addLight(Light light) {
    bufferedLights.add(light);
  }

  public void addCamera(Camera camera) {
    bufferedCameras.add(camera);
  }

  public void addSound(Sound sound) {
    bufferedSounds.add(sound);
  }

  public void addListener(Listener listener) {
    bufferedListeners.add(listener);
  }

  public void addObject(GameObject object) {
    bufferedObjects.add(object);
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

  public void toggleRendering() {
    rendering = !rendering;
  }

  public boolean lightingEnabled() {
    return lighting;
  }

  public void setLighting(boolean lighting) {
    this.lighting = lighting;
  }

  public Event after(int timeout, Runnable action) {
    Event event = new Event(action, timeout, false);
    bufferedEvents.add(event);
    return event;
  }

  public Event every(int timeout, Runnable action) {
    Event event = new Event(action, timeout, true);
    bufferedEvents.add(event);
    return event;
  }

  public Interpolator lerp(int timeout, Consumer<Float> action, float start, float end) {
    Interpolator event = new Interpolator(action, start, end, timeout);
    bufferedEvents.add(event);
    return event;
  }

  public Interpolator lerp(
      int timeout, Consumer<Float> action, float start, float end, Runnable callback) {
    Interpolator event = new Interpolator(action, start, end, timeout, callback);
    bufferedEvents.add(event);
    return event;
  }

  public void activate(Camera camera) {
    active = camera;
  }

  public Camera getCamera() {
    return active;
  }

  public void activateListener(Listener listener) {
    activeListener = listener;
  }

  public Listener getListener() {
    return activeListener;
  }

  public void setGravity(Vector3f gravity) {
    dynamics.setGravity(new javax.vecmath.Vector3f(gravity.x, gravity.y, gravity.z));
  }

  public DynamicsWorld getDynamicsWorld() {
    return dynamics;
  }

  private void cleanup(Game game, List<? extends SceneComponent> components) {
    Iterator<? extends SceneComponent> it = components.iterator();
    while (it.hasNext()) {
      SceneComponent next = it.next();
      if (next.dying()) {
        next.die(game);
        it.remove();
      }
    }
  }

  public abstract int getID();
}
