package net.tundra.core.resources.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import net.tundra.core.TundraException;
import net.tundra.core.resources.models.Material;
import net.tundra.core.scene.Light;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.BufferUtils;

public class Program {
  private int program;

  public Program(VertexShader vertex, FragmentShader fragment) throws TundraException {
    program = glCreateProgram();
    glAttachShader(program, vertex.getShader());
    glAttachShader(program, fragment.getShader());
    glLinkProgram(program);
    glBindFragDataLocation(program, 0, "colour");
    if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
      System.err.println(glGetProgramInfoLog(program, 500));
      throw new TundraException("Failed to link program");
    }
  }

  public int getProgram() {
    return program;
  }

  public void uniform(String name, float value) throws TundraException {
    glUniform1f(getUniformLocation(name), value);
    checkError();
  }

  public void uniform(String name, Vector2f value) throws TundraException {
    glUniform2f(getUniformLocation(name), value.x, value.y);
    checkError();
  }

  public void uniform(String name, Vector3f value) throws TundraException {
    glUniform3f(getUniformLocation(name), value.x, value.y, value.z);
    checkError();
  }

  public void uniform(String name, Vector4f value) throws TundraException {
    glUniform4f(getUniformLocation(name), value.x, value.y, value.z, value.w);
    checkError();
  }

  public void uniform(String name, boolean value) throws TundraException {
    glUniform1i(getUniformLocation(name), value ? 1 : 0);
    checkError();
  }

  public void uniform(String name, int value) throws TundraException {
    glUniform1i(getUniformLocation(name), value);
    checkError();
  }

  public void uniform(String name, Vector2i value) throws TundraException {
    glUniform2i(getUniformLocation(name), value.x, value.y);
    checkError();
  }

  public void uniform(String name, Vector3i value) throws TundraException {
    glUniform3i(getUniformLocation(name), value.x, value.y, value.z);
    checkError();
  }

  public void uniform(String name, Vector4i value) throws TundraException {
    glUniform4i(getUniformLocation(name), value.x, value.y, value.z, value.w);
    checkError();
  }

  public void uniform(String name, Matrix3f value) throws TundraException {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
    value.get(buffer);
    glUniformMatrix3(getUniformLocation(name), false, buffer);
    checkError();
  }

  public void uniform(String name, Matrix4f value) throws TundraException {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    value.get(buffer);
    glUniformMatrix4(getUniformLocation(name), false, buffer);
    checkError();
  }

  public void uniform(String name, Light value) throws TundraException {
    uniform(name + ".pos", value.getPosition());
    uniform(name + ".dir", value.getDirection());
    uniform(name + ".col", value.getColour());
    uniform(name + ".constant", value.getConstant());
    uniform(name + ".linear", value.getLinear());
    uniform(name + ".quadratic", value.getQuadratic());
    uniform(name + ".quadratic", value.getQuadratic());
    uniform(name + ".on", value.active());
    uniform(name + ".shadow_mapped", value.shadowMapped());
    uniform(name + ".directional", value.directional());
  }

  public void uniform(String name, Material value) throws TundraException {
    uniform(name + ".map_diff", value.mappedDiffuse());
    uniform(name + ".map_spec", value.mappedSpecular());
    uniform(name + ".map_highlight", value.mappedHighlights());
    uniform(name + ".map_amb", value.mappedAmbient());
    uniform(name + ".map_bump", value.bumpMapped());
    uniform(name + ".diff_map", 2);
    uniform(name + ".spec_map", 3);
    uniform(name + ".amb_map", 4);
    uniform(name + ".highlight_map", 5);
    uniform(name + ".bump_map", 6);
    uniform(name + ".bump_param", value.getBumpParam());
    uniform(name + ".diff", value.getDiffuse());
    uniform(name + ".spec", value.getSpecular());
    uniform(name + ".amb", value.getAmbient());
    uniform(name + ".highlight", value.getHighlight());
  }

  public void attrib(String name, int size) throws TundraException {
    int location = getAttribLocation(name);
    glVertexAttribPointer(location, size, GL_FLOAT, false, 0, 0);
    glEnableVertexAttribArray(location);
  }

  private void checkError() throws TundraException {
    int error = glGetError();
    if (error != GL_NO_ERROR) {
      throw new TundraException("OpenGL errored with error code " + error);
    }
  }

  private int getUniformLocation(String name) throws TundraException {
    int location = glGetUniformLocation(program, name);
    if (location == -1) throw new TundraException("No such uniform '" + name + "'");
    return location;
  }

  private int getAttribLocation(String name) throws TundraException {
    int location = glGetAttribLocation(program, name);
    if (location == -1) throw new TundraException("No such attribute '" + name + "'");
    return location;
  }
}
