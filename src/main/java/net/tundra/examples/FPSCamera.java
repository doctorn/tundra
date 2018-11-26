package net.tundra.examples;

import static org.lwjgl.input.Keyboard.*;

import net.tundra.core.Game;
import net.tundra.core.scene.PhysicsObject;
import net.tundra.core.scene.ShakableCamera;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class FPSCamera extends ShakableCamera {
  private float pitch = 0, yaw = 0;
  private PhysicsObject tracking;
  private boolean jumping = false;

  public FPSCamera(PhysicsObject target) {
    super();
    tracking = target;
    setPosition(target.getPosition());
  }

  @Override
  public void update(Game game, float delta) {
    super.update(game, delta);
    pitch -= game.getInput().getMouseDY() / 100f;
    yaw -= game.getInput().getMouseDX() / 100f;

    Vector3f velocity = new Vector3f();
    if (game.getInput().isKeyDown(KEY_W)) velocity.add(getForward());
    if (game.getInput().isKeyDown(KEY_S)) velocity.sub(getForward());
    if (game.getInput().isKeyDown(KEY_D)) velocity.add(getRight());
    if (game.getInput().isKeyDown(KEY_A)) velocity.sub(getRight());
    if (game.getInput().isKeyPressed(KEY_SPACE) && !jumping) {
      tracking.getBody().applyCentralImpulse(new javax.vecmath.Vector3f(0, 5f, 0));
      jumping = true;
      game.after(
          2000,
          () -> {
            jumping = false;
          });
    }

    if (velocity.length() != 0) {
      velocity.normalize();
      velocity.mul(20f);
      tracking
          .getBody()
          .applyCentralForce(new javax.vecmath.Vector3f(velocity.x, velocity.y, velocity.z));
    }

    setPosition(tracking.getPosition());
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
