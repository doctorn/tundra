package net.tundra.core.scene;

import java.util.Random;
import net.tundra.core.Game;
import org.joml.Vector3f;

public abstract class ShakableCamera extends Camera {
  private static final Random RANDOM = new Random();

  private float shake;
  private Vector3f shakeVector = new Vector3f();

  public ShakableCamera() {
    super();
  }

  public ShakableCamera(Vector3f position, Vector3f target, Vector3f up, float fov) {
    super(position, target, up, fov);
  }

  public void shake(float shake) {
    this.shake = Math.max(this.shake, shake);
  }

  @Override
  public Vector3f getPosition() {
    return super.getPosition().add(shakeVector);
  }

  @Override
  public Vector3f getTarget() {
    return super.getTarget().add(shakeVector);
  }

  @Override
  public void update(Game game, float delta) {
    if (shake > 0) {
      shakeVector =
          getScreenX()
              .mul(RANDOM.nextFloat() * shake / 5000f)
              .add(getScreenY().mul(RANDOM.nextFloat() * shake / 5000f));
      shake -= delta;
    } else {
      shake = 0;
      shakeVector.x = 0;
      shakeVector.y = 0;
      shakeVector.z = 0;
    }
  }
}
