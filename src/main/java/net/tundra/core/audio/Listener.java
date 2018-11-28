package net.tundra.core.audio;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.scene.SceneComponent;
import net.tundra.core.scene.Trackable;
import org.joml.Vector3f;
import static org.lwjgl.openal.AL10.*;

public class Listener extends SceneComponent implements Trackable{

  private Vector3f position;
  private Vector3f velocity;
  private Vector3f orientation;

  private float gain;

  public Listener(Vector3f pos, float gain) {
    this.position = pos;
    this.velocity = new Vector3f(0,0,0);
    this.orientation = new Vector3f(0,0,0);
    this.gain = gain;

  }

  public Vector3f getPosition() {
    return position;
  }

  public Vector3f getVelocity() {
    return velocity;
  }

  public Vector3f getOrientation() {
    return orientation;
  }

  public float getGain() {
    return gain;
  }

  public void setPosition(Vector3f position) {
    this.position = position;
  }

  public void setVelocity(Vector3f velocity) {
    this.velocity = velocity;
  }

  public void setOrientation(Vector3f orientation) {
    this.orientation = orientation;
  }

  public void setGain(float gain) {
    this.gain = gain;
  }

  @Override
  public void update(Game game, float delta) throws TundraException {

    alListener3f(AL_POSITION, position.x, position.y, position.z);
    alListener3f(AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    alListener3f(AL_ORIENTATION, orientation.x, orientation.y, orientation.z);
  }
}
