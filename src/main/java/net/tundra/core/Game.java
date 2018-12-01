package net.tundra.core;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.sprites.Sprite;
import org.joml.Vector4f;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class Game {
  private Input input;
  private int width, height;
  private String title;
  private float timescale = 1f;
  private boolean fullscreen;
  private Graphics graphics;
  private List<GameState> states = new ArrayList<>();
  private GameState currentState;
  private boolean changingState = false;
  private float maskOpacity;

  private int fps = 0, frameCount = 0, cumulativeDelta = 0, delta;

  public Game(int width, int height, String title, boolean fullscreen) {
    this.width = width;
    this.height = height;
    this.title = title;
    this.fullscreen = fullscreen;
  }

  public abstract void initStates() throws TundraException;

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
      initStates();
    } catch (LWJGLException e) {
      throw new TundraException("Failed to initialise game window", e);
    }

    loop();
  }

  public void loop() throws TundraException {
    long timestamp = System.currentTimeMillis();
    do {
      delta = (int) (System.currentTimeMillis() - timestamp);
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
      currentState.updateDefault(this, realDelta);
      currentState.renderDefault(this, graphics);
      if (changingState) {
        graphics.setColour(new Vector4f(0f, 0f, 0f, maskOpacity));
        graphics.fillRect(0, 0, getWidth(), getHeight());
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

  public GameState getCurrentState() {
    return currentState;
  }

  public void addState(GameState state) throws TundraException {
    Iterator<GameState> iter = states.iterator();
    while (iter.hasNext()) {
      if (iter.next().getID() == state.getID()) iter.remove();
    }
    state.initDefault(this);
    states.add(state);
  }

  public void enterState(int id) {
    if (!changingState) {
      for (GameState state : states) {
        if (state.getID() == id) {
          if (currentState == null) currentState = state;
          else {
            changingState = true;
            currentState.lerp(
                750,
                f -> {
                  maskOpacity = f;
                },
                0f,
                1f,
                () -> {
                  currentState = state;
                  state.lerp(
                      750,
                      f -> {
                        maskOpacity = f;
                      },
                      1f,
                      0f,
                      () -> {
                        changingState = false;
                      });
                });
          }
          return;
        }
      }
    }
    throw new RuntimeException(new TundraException("No state with id '" + id + "'"));
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

  public int getTrueDelta() {
    return delta;
  }

  public void setTimescale(float timescale) {
    this.timescale = timescale;
  }

  public float getTimescale() {
    return timescale;
  }

  public Input getInput() {
    return input;
  }

  public void setCursor(Sprite sprite, int x, int y) throws TundraException {
    try {
      Mouse.setNativeCursor(sprite.toCursor(x, y));
    } catch (LWJGLException e) {
      throw new TundraException("Failed to change cursor", e);
    }
  }
}
