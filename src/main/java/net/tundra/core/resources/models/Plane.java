package net.tundra.core.resources.models;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

public class Plane extends Model {
  public Plane() {
    super(vertices(), normals(), textures(), indices());
  }

  private static FloatBuffer vertices() {
    FloatBuffer vertices = BufferUtils.createFloatBuffer(12);
    vertices
        .put(
            new float[] {
              -1, -1, 0, // 0
              -1, 1, 0, // 1
              1, 1, 0, // 2
              1, -1, 0, // 3
            })
        .flip();
    return vertices;
  }

  private static FloatBuffer normals() {
    FloatBuffer normals = BufferUtils.createFloatBuffer(12);
    normals
        .put(
            new float[] {
              0, 0, -1, // 0
              0, 0, -1, // 1
              0, 0, -1, // 2
              0, 0, -1, // 3
            })
        .flip();
    return normals;
  }

  private static FloatBuffer textures() {
    FloatBuffer textures = BufferUtils.createFloatBuffer(8);
    textures
        .put(
            new float[] {
              0, 1, // 0
              0, 0, // 1
              1, 0, // 2
              1, 1 // 3
            })
        .flip();
    return textures;
  }

  private static IntBuffer indices() {
    IntBuffer indices = BufferUtils.createIntBuffer(6);
    indices
        .put(
            new int[] {
              3, 2, 0,
              2, 1, 0
            })
        .flip();
    return indices;
  }
}
