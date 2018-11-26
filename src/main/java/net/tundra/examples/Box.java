package net.tundra.examples;

import com.bulletphysics.collision.shapes.BoxShape;
import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Model;
import net.tundra.core.scene.PhysicsObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Box extends PhysicsObject {
  private boolean directional = false;

  public Box(Game game, Vector3f position, Vector3f direction) {
    super(position, new BoxShape(new javax.vecmath.Vector3f(0.2f, 0.2f, 0.2f)), 1f);
    // game.addLight(new TrackingLight(this, new Vector3f(0, 1, 1)));
    directional = true;
    getBody()
        .applyCentralImpulse(
            new javax.vecmath.Vector3f(30 * direction.x, 30 * direction.y, 30 * direction.z));
    // game.after(5000, () -> kill());
  }

  public Box(Game game, Vector3f position) {
    super(position, new BoxShape(new javax.vecmath.Vector3f(0.2f, 0.2f, 0.2f)), 1f);
    // game.addLight(new TrackingLight(this, new Vector3f(0, 1, 1)));
    // game.after(5000, () -> kill());
  }

  @Override
  public void update(Game game, int delta) throws TundraException {}

  @Override
  public void render(Game game, Graphics graphics) throws TundraException {
    // boolean lighting = game.lightingEnabled();
    // game.setLighting(false);
    if (directional) graphics.setColour(new Vector3f(0.2f, 0.2f, 0.2f));
    else graphics.setColour(new Vector3f(0.8f, 0.8f, 0.8f));
    graphics.drawModel(
        Model.CUBE, new Matrix4f().translate(getPosition()).scale(0.2f).rotate(getRotation()));
    // game.setLighting(lighting);
  }
}
