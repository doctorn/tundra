package net.tundra.core.resources.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import net.tundra.core.TundraException;
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

  public void set(String name, float value) throws TundraException {
    glUniform1f(getUniformLocation(name), value);
    checkError();
  }

  public void set(String name, Vector2f value) throws TundraException {
    glUniform2f(getUniformLocation(name), value.x, value.y);
    checkError();
  }

  public void set(String name, Vector3f value) throws TundraException {
    glUniform3f(getUniformLocation(name), value.x, value.y, value.z);
    checkError();
  }

  public void set(String name, Vector4f value) throws TundraException {
    glUniform4f(getUniformLocation(name), value.x, value.y, value.z, value.w);
    checkError();
  }

  public void set(String name, int value) throws TundraException {
    glUniform1i(getUniformLocation(name), value);
    checkError();
  }

  public void set(String name, Vector2i value) throws TundraException {
    glUniform2i(getUniformLocation(name), value.x, value.y);
    checkError();
  }

  public void set(String name, Vector3i value) throws TundraException {
    glUniform3i(getUniformLocation(name), value.x, value.y, value.z);
    checkError();
  }

  public void set(String name, Vector4i value) throws TundraException {
    glUniform4i(getUniformLocation(name), value.x, value.y, value.z, value.w);
    checkError();
  }

  public void set(String name, Matrix3f value) throws TundraException {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
    value.get(buffer);
    glUniformMatrix3(getUniformLocation(name), false, buffer);
    checkError();
  }

  public void set(String name, Matrix4f value) throws TundraException {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    value.get(buffer);
    glUniformMatrix4(getUniformLocation(name), false, buffer);
    checkError();
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
}
