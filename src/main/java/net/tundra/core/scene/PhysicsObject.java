package net.tundra.core.scene;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import net.tundra.core.Game;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class PhysicsObject extends GameObject implements Trackable {
  private RigidBody body;

  public PhysicsObject(Vector3f position, CollisionShape shape, float mass) {
    DefaultMotionState motionState =
        new DefaultMotionState(
            new Transform(
                new javax.vecmath.Matrix4f(
                    new javax.vecmath.Quat4f(0, 0, 0, 1),
                    new javax.vecmath.Vector3f(position.x, position.y, position.z),
                    1f)));
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
