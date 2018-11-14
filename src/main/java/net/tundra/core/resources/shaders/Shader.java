package net.tundra.core.resources.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import net.tundra.core.TundraException;

public abstract class Shader {
  private int shader;

  public Shader(String filename) throws TundraException {
    String source = readSource(filename);
    shader = glCreateShader(getShaderType());
    glShaderSource(shader, source);
    glCompileShader(shader);
    if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
      System.err.println(glGetShaderInfoLog(shader, 500));
      throw new TundraException("Failed to compile shader '" + filename + "'");
    }
  }

  public void delete() {
    glDeleteShader(shader);
  }

  protected abstract int getShaderType();

  protected int getShader() {
    return shader;
  }

  private static String readSource(String filename) throws TundraException {
    StringBuilder source = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
      String line;
      while ((line = reader.readLine()) != null) {
        source.append(line);
        source.append("\n");
      }
    } catch (IOException e) {
      throw new TundraException("Failed to load shader source from '" + filename + "'", e);
    }
    return source.toString();
  }
}
