package net.tundra.core;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Input {
  private static final int KEYS = 256;
  private int mouseButtons;

  private boolean[] currentKeys;
  private boolean[] previousKeys;
  private boolean[] mouseEvents;

  private int previousMouseX, previousMouseY, currentMouseX, currentMouseY;

  public Input() {
    Keyboard.enableRepeatEvents(true);

    currentKeys = new boolean[KEYS];
    previousKeys = new boolean[KEYS];

    mouseButtons = Mouse.getButtonCount();
    mouseEvents = new boolean[mouseButtons];

    previousMouseX = getMouseX();
    previousMouseY = getMouseY();
  }

  public void update() {
    previousMouseX = currentMouseX;
    previousMouseY = currentMouseY;
    currentMouseX = getMouseX();
    currentMouseY = getMouseY();

    Keyboard.poll();
    currentKeys = new boolean[KEYS];
    previousKeys = new boolean[KEYS];

    while (Keyboard.next()) {
      if (Keyboard.getEventKeyState()) {
        if (Keyboard.isRepeatEvent()) previousKeys[Keyboard.getEventKey()] = true;
        else currentKeys[Keyboard.getEventKey()] = true;
      }
    }

    Mouse.poll();
    mouseEvents = new boolean[mouseButtons];

    while (Mouse.next()) {
      if (Mouse.getEventButtonState()) mouseEvents[Mouse.getEventButton()] = true;
    }
  }

  public boolean isKeyPressed(int key) {
    return currentKeys[key] && !previousKeys[key];
  }

  public boolean isKeyDown(int key) {
    return Keyboard.isKeyDown(key);
  }

  public boolean isMouseButtonPressed(int button) {
    return mouseEvents[button];
  }

  public boolean isMouseButtonDown(int button) {
    return Mouse.isButtonDown(button);
  }

  public int getMouseX() {
    return Mouse.getX();
  }

  public int getMouseY() {
    return Display.getHeight() - Mouse.getY() - 1;
  }

  public int getMouseDX() {
    return getMouseX() - previousMouseX;
  }

  public int getMouseDY() {
    return getMouseY() - previousMouseY;
  }

  public int getDWheel() {
    return Mouse.getDWheel();
  }

  public void setMouseGrabbed(boolean grabbed) {
    Mouse.setGrabbed(grabbed);
  }

  public boolean isMouseGrabbed() {
    return Mouse.isGrabbed();
  }

  public float getMouseGLX() {
    return (float) (getMouseX() - Display.getWidth() / 2)
        / (Math.min(Display.getWidth(), Display.getHeight()));
  }

  public float getMouseGLY() {
    return (float) (-getMouseY() + Display.getHeight() / 2)
        / (Math.min(Display.getWidth(), Display.getHeight()));
  }

  public float getMaxGLX() {
    return (float) Display.getWidth() / (2 * Math.min(Display.getWidth(), Display.getHeight()));
  }

  public float getMaxGLY() {
    return (float) Display.getHeight() / (2 * Math.min(Display.getWidth(), Display.getHeight()));
  }

  public boolean isMouseInArea(int x, int y, int width, int height) {
    if (getMouseX() < x || getMouseX() > x + width) return false;
    if (getMouseY() < y || getMouseY() > y + height) return false;
    return true;
  }
}
