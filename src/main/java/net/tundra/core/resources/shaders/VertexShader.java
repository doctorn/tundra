package net.tundra.core.resources.shaders;

import static org.lwjgl.opengl.GL20.*;

import java.io.InputStream;
import net.tundra.core.TundraException;

public class VertexShader extends Shader {
  public VertexShader(String filename) throws TundraException {
    super(filename);
  }

  public VertexShader(InputStream in) throws TundraException {
    super(in);
  }

  @Override
  protected int getShaderType() {
    return GL_VERTEX_SHADER;
  }
}
