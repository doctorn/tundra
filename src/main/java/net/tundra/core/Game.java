package net.tundra.core;

import static org.lwjgl.opengl.GL11.*;

import net.tundra.core.graphics.Graphics;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class Game {
  private Input input;
  private int width, height;
  private String title;
  private boolean fullscreen;
  private Graphics graphics;

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
      input.update();
      update(delta);
      graphics.clear();
      render(graphics);
      checkError();
      Display.update();
    } while (!Display.isCloseRequested());
  }

  private void checkError() throws TundraException {
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

  public Input getInput() {
    return input;
  }

  public abstract void init() throws TundraException;

  public abstract void update(int delta) throws TundraException;

  public abstract void render(Graphics graphics) throws TundraException;
}
