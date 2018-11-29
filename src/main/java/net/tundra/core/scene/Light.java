package net.tundra.core.scene;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Model;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Light extends SceneComponent implements Trackable {
  private Vector3f position = new Vector3f(0, 0, 0);
  private Vector3f direction = new Vector3f(0, 0, 0);
  private Vector3f colour = new Vector3f(0, 0, 0);

  private float constant = 1f, linear = 1f, quadratic = 1f;
  private float theta;

  private boolean active = true, shadowMapped = false, directional = false, spotlight = false;

  public Light() {}

  public Light(float x, float y, float z, float r, float g, float b) {
    position = new Vector3f(x, y, z);
    colour = new Vector3f(r, g, b);

    constant = 1f;
    linear = 0.045f;
    quadratic = 0.0075f;
  }

  public Light(Vector3f direction, Vector3f colour) {
    this.direction = new Vector3f(direction);
    this.colour = new Vector3f(colour);
    directional = true;
    spotlight = false;
  }

  public Light(
      Vector3f position,
      Vector3f colour,
      Vector3f direction,
      float constant,
      float linear,
      float quadratic,
      float theta) {
    this.position = new Vector3f(position);
    this.direction = new Vector3f(direction);
    this.colour = new Vector3f(colour);

    this.constant = constant;
    this.linear = linear;
    this.quadratic = quadratic;

    this.theta = theta;
    spotlight = true;
    directional = false;
  }

  public Light(Vector3f position, Vector3f colour, float constant, float linear, float quadratic) {
    this.position = new Vector3f(position);
    this.colour = new Vector3f(colour);

    this.constant = constant;
    this.linear = linear;
    this.quadratic = quadratic;
  }

  public Vector3f getColour() {
    return colour;
  }

  public float getConstant() {
    return constant;
  }

  public float getLinear() {
    return linear;
  }

  public float getQuadratic() {
    return quadratic;
  }

  public float getTheta() {
    return theta;
  }

  @Override
  public Vector3f getPosition() {
    return new Vector3f(position);
  }

  public void setPosition(Vector3f position) {
    this.position = position;
  }

  public void setAmbient(Vector3f colour) {
    this.colour = colour;
  }

  public void setConstant(float constant) {
    this.constant = constant;
  }

  public void setLinear(float linear) {
    this.linear = linear;
  }

  public void setQuadratic(float quadratic) {
    this.quadratic = quadratic;
  }

  public void enableShadowMapping() {
    shadowMapped = true;
  }

  public void disableShadowMapping() {
    shadowMapped = false;
  }

  public boolean shadowMapped() {
    return shadowMapped;
  }

  public boolean directional() {
    return directional;
  }

  public boolean spotlight() {
    return spotlight;
  }

  public void setDirectional(Vector3f direction) {
    this.direction = new Vector3f(direction);
    directional = true;
    spotlight = false;
  }

  public void setUndirectional() {
    directional = false;
  }

  public Vector3f getDirection() {
    return new Vector3f(direction);
  }

  public void setDirection(Vector3f direction) {
    this.direction = new Vector3f(direction);
  }

  public void makeSpotlight(Vector3f direction, float theta) {
    this.direction = new Vector3f(direction);
    this.theta = theta;
    spotlight = true;
    directional = false;
  }

  public void renderDebug(Game game, Graphics graphics) throws TundraException {
    graphics.setColour(new Vector3f(0.976f, 0.29f, 0.463f));
    graphics.drawModelWireframe(Model.CUBE, new Matrix4f().translate(position).scale(0.2f));
  }

  public void toggle() {
    active = !active;
  }

  public boolean active() {
    return active;
  }
}
