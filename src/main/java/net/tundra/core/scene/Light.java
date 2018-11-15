package net.tundra.core.scene;

import org.joml.Vector3f;

public class Light {

  private Vector3f position = new Vector3f(0,0,0);
  private Vector3f colour = new Vector3f(0,0,0);

  private float constant = 1f;
  private float linear = 1f;
  private float quadratic = 1f;

  public Light() {

  }

  public Light (float x, float y, float z, float r, float g, float b) {
    position = new Vector3f(x, y, z);
    colour = new Vector3f(r, g, b);


    constant = 1f;
    linear = 0.045f;
    quadratic = 0.0075f;
  }

  public Light (Vector3f pos, Vector3f col, float con, float lin, float quad) {
    position = pos;
    colour = col;

    constant = con;
    linear = lin;
    quadratic = quad;
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

  public Vector3f getPosition() {
    return position;
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
}
