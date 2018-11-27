package net.tundra.core.scene;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Model;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class PhysicsObject extends GameObject implements Trackable {
  private RigidBody body;
  private Model model;
  private Vector3f scale;

  public PhysicsObject(
      Vector3f position, Model model, Quaternionf rotation, Vector3f scale, float mass) {
    DefaultMotionState motionState =
        new DefaultMotionState(
            new Transform(
                new javax.vecmath.Matrix4f(
                    new javax.vecmath.Quat4f(rotation.x, rotation.y, rotation.z, rotation.w),
                    new javax.vecmath.Vector3f(position.x, position.y, position.z),
                    1f)));
    this.scale = scale;
    this.model = model;
    CollisionShape shape = model.getCollisionShape();
    shape.setLocalScaling(new javax.vecmath.Vector3f(scale.x, scale.y, scale.z));
    javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f();
    shape.calculateLocalInertia(mass, inertia);
    RigidBodyConstructionInfo constructionInfo =
        new RigidBodyConstructionInfo(mass, motionState, shape, inertia);
    constructionInfo.linearDamping = 0.2f;
    constructionInfo.friction = 0.5f;
    constructionInfo.angularDamping = 0.2f;
    body = new RigidBody(constructionInfo);
  }

  public RigidBody getBody() {
    return body;
  }

  public Quaternionf getRotation() {
    javax.vecmath.Quat4f rotation = new javax.vecmath.Quat4f();
    body.getMotionState().getWorldTransform(new Transform()).getRotation(rotation);
    return new Quaternionf(rotation.x, rotation.y, rotation.z, rotation.w);
  }

  public void renderDebug(Game game, Graphics graphics) throws TundraException {
    graphics.setColour(new Vector3f(0.996f, 0.502f, 0.098f));
    graphics.drawModelWireframe(
        model, new Matrix4f().translate(getPosition()).rotate(getRotation()).scale(scale));
  }

  @Override
  public void die(Game game) {
    game.getDynamicsWorld().removeRigidBody(body);
  }

  @Override
  public Vector3f getPosition() {
    javax.vecmath.Vector3f position =
        body.getMotionState().getWorldTransform(new Transform()).origin;
    return new Vector3f(position.x, position.y, position.z);
  }
}
