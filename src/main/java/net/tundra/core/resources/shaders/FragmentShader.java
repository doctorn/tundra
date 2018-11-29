package net.tundra.core.resources.shaders;

import static org.lwjgl.opengl.GL20.*;

import java.io.InputStream;
import net.tundra.core.TundraException;

public class FragmentShader extends Shader {
  public FragmentShader(String filename) throws TundraException {
    super(filename);
  }

  public FragmentShader(InputStream in) throws TundraException {
    super(in);
  }

  @Override
  protected int getShaderType() {
    return GL_FRAGMENT_SHADER;
  }
}
