package net.tundra.examples;

import com.bulletphysics.collision.shapes.StaticPlaneShape;
import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Model;
import net.tundra.core.scene.PhysicsObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Floor extends PhysicsObject {
  public Floor() {
    super(
        new Vector3f(0, -4, 0), new StaticPlaneShape(new javax.vecmath.Vector3f(0, 1f, 0), 0f), 0);
  }

  @Override
  public void update(Game game, int delta) throws TundraException {}

  @Override
  public void render(Game game, Graphics graphics) throws TundraException {
    graphics.drawModel(
        Model.PLANE,
        new Matrix4f()
            .translate(getPosition())
            .scale(200f)
            .rotate(-(float) Math.PI / 2f, new Vector3f(1, 0, 0)));
  }
}
