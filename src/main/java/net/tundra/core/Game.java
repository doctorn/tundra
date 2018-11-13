package net.tundra.core;

import net.tundra.core.graphics.Graphics;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class Game {
  private int width, height;
  private String title;
  private boolean fullscreen;
  private Graphics graphics;

  public Game(int width, int height, String title, boolean fullscreen) {
    this.width = width;
    this.height = height;
    this.title = title;
    this.fullscreen = fullscreen;
    this.graphics = new Graphics(this);
  }

  public void start() throws TundraException {
    try {
      if (!fullscreen) Display.setDisplayMode(new DisplayMode(width, height));
      else Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
      Display.setTitle(title);
      Display.setFullscreen(fullscreen);
      Display.create();
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
      update(delta);
      render(graphics);
      graphics.render();
      Display.update();
    } while (!Display.isCloseRequested());
  }

  public abstract void init() throws TundraException;

  public abstract void update(int delta) throws TundraException;

  public abstract void render(Graphics g) throws TundraException;
}
