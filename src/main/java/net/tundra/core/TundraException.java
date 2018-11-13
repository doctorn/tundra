package net.tundra.core;

public class TundraException extends Exception {
  private static final long serialVersionUID = 1l;

  public TundraException(String message) {
    super(message);
  }

  public TundraException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
