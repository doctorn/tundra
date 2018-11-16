package net.tundra.core.scene;

import net.tundra.core.Game;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class OrbitalCamera extends Camera {
  private float pitch = 0, yaw = 0, distance;

  public OrbitalCamera(Vector3f target, float distance) {
    super(new Vector3f(0, 0, distance).add(target), target, new Vector3f(0, 1, 0), 45f);
    this.distance = distance;
  }

  @Override
  public void update(Game game, int delta) {
    pitch += game.getInput().getMouseDY() / 100f;
    yaw += game.getInput().getMouseDX() / 100f;
    distance -= game.getInput().getDWheel() / 200f;

    setPosition(
        new Matrix3f()
            .rotate(yaw, new Vector3f(0, 1, 0))
            .rotate(pitch, new Vector3f(1, 0, 0))
            .transform(new Vector3f(0, 0, distance))
            .add(getTarget()));
  }
}
