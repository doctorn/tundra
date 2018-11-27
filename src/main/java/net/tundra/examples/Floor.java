package net.tundra.examples;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Model;
import net.tundra.core.scene.PhysicsObject;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Floor extends PhysicsObject {
  public Floor() {
    super(
        new Vector3f(0, -4, 0),
        Model.PLANE,
        new Quaternionf().rotateX(-(float) Math.PI / 2f),
        new Vector3f(200f, 200f, 1f),
        0f);
  }

  @Override
  public void update(Game game, float delta) throws TundraException {}

  @Override
  public void render(Game game, Graphics graphics) throws TundraException {
    graphics.setColour(new Vector3f(1f, 1f, 1f));
    graphics.drawModel(
        Model.PLANE, new Matrix4f().translate(getPosition()).rotate(getRotation()).scale(200f));
  }
}
