package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Model;
import net.tundra.core.scene.GameObject;
import net.tundra.core.scene.Trackable;
import net.tundra.core.scene.TrackingLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Bullet extends GameObject implements Trackable {
  private Vector3f position, direction;

  public Bullet(Game game, Vector3f position, Vector3f direction) {
    this.position = position;
    this.direction = direction.normalize();
    game.addLight(new TrackingLight(this, new Vector3f(0, 1, 1)));
  }

  @Override
  public void update(Game game, int delta) throws TundraException {
    position.add(new Vector3f(direction).mul(0.03f * delta));
    if (Math.abs(position.x) > 20 || Math.abs(position.y) > 4 || Math.abs(position.z) > 20) kill();
  }

  @Override
  public void render(Game game, Graphics graphics) throws TundraException {
    boolean lighting = game.lightingEnabled();
    game.setLighting(false);
    graphics.drawModel(Model.CUBE, new Matrix4f().translate(position).scale(0.05f));
    game.setLighting(lighting);
  }

  @Override
  public Vector3f getPosition() {
    return position;
  }
}
