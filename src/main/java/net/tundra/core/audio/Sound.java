package net.tundra.core.audio;

import static org.lwjgl.openal.AL10.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.scene.SceneComponent;
import net.tundra.core.scene.Trackable;
import org.joml.Vector3f;
import org.lwjgl.util.WaveData;

public class Sound extends SceneComponent implements Trackable {
  private int id;
  private int buffer;
  private WaveData waveData;
  private Vector3f position;
  private Vector3f velocity;
  private float gain;
  private float pitch;
  private boolean looping;

  public Sound(
      String file, Vector3f position, Vector3f velocity, float gain, float pitch, boolean looping)
      throws TundraException {
    try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
      waveData = WaveData.create(in);
      this.position = position;
      this.velocity = velocity;
      this.gain = gain;
      this.pitch = pitch;
      this.looping = looping;

      this.id = alGenSources();
      this.buffer = alGenBuffers();

      alBufferData(buffer, waveData.format, waveData.data, waveData.samplerate);
    } catch (IOException e) {
      throw new TundraException("Failed to load sound file '" + file + "'", e);
    }
  }

  public Sound(String file) throws TundraException {
    this(file, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1, 1, false);
  }

  public Sound(String file, Vector3f position) throws TundraException {
    this(file, position, new Vector3f(0, 0, 0), 1, 1, false);
  }

  public Sound(String file, boolean looping) throws TundraException {
    this(file, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1, 1, looping);
  }

  public void play() {
    stop();
    alSourcei(id, AL_BUFFER, buffer);
    continuePlaying();
  }

  public void delete() {
    alDeleteSources(id);
  }

  public boolean isPlaying() {
    return alGetSourcei(id, AL_SOURCE_STATE) == AL_PLAYING;
  }

  public void pause() {
    alSourcePause(id);
  }

  public void continuePlaying() {
    alSourcePlay(id);
  }

  public void stop() {
    alSourceStop(id);
  }

  public void setPosition(Vector3f position) {
    this.position = position;
  }

  public void setVelocity(Vector3f velocity) {
    this.velocity = velocity;
  }

  public void setGain(float gain) {
    this.gain = gain;
  }

  public void setPitch(float pitch) {
    this.pitch = pitch;
  }

  public void setLooping(boolean looping) {
    this.looping = looping;
  }

  public Vector3f getPosition() {
    return position;
  }

  public Vector3f getVelocity() {
    return velocity;
  }

  public float getGain() {
    return gain;
  }

  public float getPitch() {
    return pitch;
  }

  public boolean isLooping() {
    return looping;
  }

  public int getId() {
    return id;
  }

  public int getBuffer() {
    return buffer;
  }

  @Override
  public void update(Game game, float delta) throws TundraException {
    alSourcef(id, AL_GAIN, gain);
    alSourcef(id, AL_PITCH, pitch);
    alSource3f(id, AL_POSITION, position.x, position.y, position.z);
    alSource3f(id, AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    alSourcei(id, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
  }

  @Override
  public void die(Game game) {
    stop();
    waveData.dispose();
    alDeleteBuffers(buffer);
  }
}
