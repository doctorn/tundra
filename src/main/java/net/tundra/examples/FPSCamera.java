package net.tundra.examples;

import static org.lwjgl.input.Keyboard.*;

import net.tundra.core.Game;
import net.tundra.core.scene.Camera;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class FPSCamera extends Camera {
  private float pitch = 0, yaw = 0;

  public FPSCamera(Vector3f position) {
    super();
    setPosition(position);
  }

  public void update(Game game, int delta) {
    pitch -= game.getInput().getMouseDY() / 100f;
    yaw -= game.getInput().getMouseDX() / 100f;

    Vector3f velocity = new Vector3f();
    if (game.getInput().isKeyDown(KEY_W)) velocity.add(getForward());
    if (game.getInput().isKeyDown(KEY_S)) velocity.sub(getForward());
    if (game.getInput().isKeyDown(KEY_D)) velocity.add(getRight());
    if (game.getInput().isKeyDown(KEY_A)) velocity.sub(getRight());

    if (velocity.length() != 0) {
      velocity.normalize();
      setPosition(getPosition().add(velocity.mul(0.01f * delta)));
    }

    if (getPosition().x > 19.5f) setPosition(new Vector3f(19.5f, getPosition().y, getPosition().z));
    if (getPosition().x < -19.5f)
      setPosition(new Vector3f(-19.5f, getPosition().y, getPosition().z));
    if (getPosition().z > 19.5f) setPosition(new Vector3f(getPosition().x, getPosition().y, 19.5f));
    if (getPosition().z < -19.5f)
      setPosition(new Vector3f(getPosition().x, getPosition().y, -19.5f));

    setTarget(
        new Matrix3f()
            .rotate(yaw, new Vector3f(0, 1, 0))
            .rotate(pitch, new Vector3f(1, 0, 0))
            .transform(new Vector3f(0, 0, 1))
            .add(getPosition()));
  }

  private Vector3f getForward() {
    return getTarget().sub(getPosition()).mul(new Vector3f(1, 0, 1)).normalize();
  }

  private Vector3f getRight() {
    return getForward().cross(getUp());
  }
}
