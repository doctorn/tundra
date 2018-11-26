package net.tundra.core.scene;

import net.tundra.core.Game;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class OrbitalCamera extends Camera {
  private Trackable tracking = null;
  private float pitch = 0, yaw = 0, distance;

  public OrbitalCamera(Vector3f target, float distance) {
    super(new Vector3f(0, 0, distance).add(target), target, new Vector3f(0, 1, 0), 45f);
    this.distance = distance;
  }

  public OrbitalCamera(Trackable target, float distance) {
    super(
        new Vector3f(0, 0, distance).add(target.getPosition()),
        target.getPosition(),
        new Vector3f(0, 1, 0),
        45f);
    tracking = target;
    this.distance = distance;
  }

  public void setDistance(float distance) {
    this.distance = distance;
  }

  public float getDistance() {
    return distance;
  }

  @Override
  public void update(Game game, float delta) {
    pitch += game.getInput().getMouseDY() / 100f;
    yaw += game.getInput().getMouseDX() / 100f;
    distance -= game.getInput().getDWheel() / 200f;

    if (tracking != null) setTarget(tracking.getPosition());

    setPosition(
        new Matrix3f()
            .rotate(yaw, new Vector3f(0, 1, 0))
            .rotate(pitch, new Vector3f(1, 0, 0))
            .transform(new Vector3f(0, 0, distance))
            .add(getTarget()));
  }
}
