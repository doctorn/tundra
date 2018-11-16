package net.tundra.core.resources.models;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Model {
  public static final Model CUBE = new Cube(false);
  public static final Model PLANE = new Plane();
  private int vertexArray, vertexHandle, normalHandle, textureHandle, indexHandle, indexCount;

  public Model(FloatBuffer vertices, FloatBuffer normals, FloatBuffer textures, IntBuffer indices) {
    indexCount = indices.capacity();

    vertexArray = glGenVertexArrays();
    glBindVertexArray(vertexArray);

    vertexHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    normalHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, normalHandle);
    glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);

    textureHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, textureHandle);
    glBufferData(GL_ARRAY_BUFFER, textures, GL_STATIC_DRAW);

    indexHandle = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexHandle);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
  }

  public int getModel() {
    return vertexArray;
  }

  public int getVertices() {
    return vertexHandle;
  }

  public int getNormals() {
    return normalHandle;
  }

  public int getTextureCoords() {
    return textureHandle;
  }

  public int getIndices() {
    return indexHandle;
  }

  public int getIndexCount() {
    return indexCount;
  }
}
