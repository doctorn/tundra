package net.tundra.core;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.scene.Camera;
import net.tundra.core.scene.GameObject;
import net.tundra.core.scene.Light;
import net.tundra.core.scene.SceneComponent;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class Game {
  private Input input;
  private int width, height;
  private String title;
  private boolean fullscreen, debug = false, lighting = true, shadowMapping = false;
  private Graphics graphics;
  private List<Light> lights = new ArrayList<>();
  private List<Camera> cameras = new ArrayList<>();
  private List<GameObject> objects = new ArrayList<>();
  private Camera shadowCamera;
  private Light shadowLight;

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
      init();
    } catch (LWJGLException e) {
      throw new TundraException("Failed to initialise game window", e);
    }

    loop();
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
      input.update();

      for (GameObject object : objects) object.update(this, delta);
      for (Light light : lights) light.update(this, delta);
      for (Camera camera : cameras) camera.update(this, delta);

      update(delta);

      cleanup(objects);
      cleanup(lights);
      cleanup(cameras);

      for (GameObject object : objects) object.render(this, graphics);
      render(graphics);
      if (debug) {
        for (Light light : lights) light.renderDebug(this, graphics);
        for (Camera camera : cameras) camera.renderDebug(this, graphics);
      }
      graphics.render();

      checkError();
      Display.update();
    } while (!Display.isCloseRequested());
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

  public void addObject(GameObject object) {
    objects.add(object);
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

  private void cleanup(List<? extends SceneComponent> components) {
    Iterator<? extends SceneComponent> it = components.iterator();
    while (it.hasNext()) {
      if (it.next().dying()) it.remove();
    }
  }

  public abstract void init() throws TundraException;

  public abstract void update(int delta) throws TundraException;

  public abstract void render(Graphics graphics) throws TundraException;
}
