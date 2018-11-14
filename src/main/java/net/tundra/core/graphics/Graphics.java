package net.tundra.core.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.resources.Program;
import org.lwjgl.BufferUtils;

public class Graphics {
  private Game game;
  private Program active;

  public Graphics(Game game) {
    this.game = game;
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
  }

  public void activate(Program program) {
    active = program;
  }

  public void render() throws TundraException {
    glClearColor(0f, 0f, 0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


    glUseProgram(active.getProgram());

    FloatBuffer vertices = BufferUtils.createFloatBuffer(12);
    vertices
        .put(0)
        .put(0)
        .put(0)
        .put(0)
        .put(0.5f)
        .put(0)
        .put(0.5f)
        .put(0.5f)
        .put(0.5f)
        .put(0.5f)
        .put(0f)
        .put(0f);
    vertices.flip();

    IntBuffer indices = BufferUtils.createIntBuffer(6);
    indices.put(3).put(1).put(0).put(3).put(2).put(1).flip();

    int vertexData = glGenVertexArrays();
    glBindVertexArray(vertexData);

    int vertexHandle = glGenBuffers();

    glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);


    int location = glGetAttribLocation(active.getProgram(), "vertex");
    if (location != -1) {
      glVertexAttribPointer(location, 3, GL_FLOAT, false, 0, 0);
      glEnableVertexAttribArray(location);
    } else throw new TundraException("No location");

    int indexHandle = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexHandle);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    glBindVertexArray(0);
    glUseProgram(0);
    checkError();
  }

  private void checkError() throws TundraException {
    int error = glGetError();
    if (error != GL_NO_ERROR) {
      throw new TundraException("OpenGL errored with error code " + error);
    }
  }
}
