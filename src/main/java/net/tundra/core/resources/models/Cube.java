package net.tundra.core.resources.models;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

public class Cube extends Model {
  public Cube(boolean mapped) {
    super(vertices(), normals(), textures(mapped), indices(), true);
  }

  private static FloatBuffer vertices() {
    FloatBuffer vertices = BufferUtils.createFloatBuffer(72);
    vertices
        .put(
            new float[] {
              // Front
              -1, -1, -1,
              1, -1, -1,
              1, 1, -1,
              -1, 1, -1,
              // Left
              -1, -1, -1,
              -1, 1, -1,
              -1, 1, 1,
              -1, -1, 1,
              // Right
              1, -1, -1,
              1, 1, -1,
              1, 1, 1,
              1, -1, 1,
              // Top
              -1, 1, -1,
              1, 1, -1,
              1, 1, 1,
              -1, 1, 1,
              // Bottom
              -1, -1, -1,
              1, -1, -1,
              1, -1, 1,
              -1, -1, 1,
              // Back
              -1, -1, 1,
              1, -1, 1,
              1, 1, 1,
              -1, 1, 1
            })
        .flip();
    return vertices;
  }

  private static FloatBuffer normals() {
    FloatBuffer normals = BufferUtils.createFloatBuffer(72);
    normals.put(
        new float[] {
          // Front
          0, 0, -1,
          0, 0, -1,
          0, 0, -1,
          0, 0, -1,
          // Left
          -1, 0, 0,
          -1, 0, 0,
          -1, 0, 0,
          -1, 0, 0,
          // Right
          1, 0, 0,
          1, 0, 0,
          1, 0, 0,
          1, 0, 0,
          // Top
          0, 1, 0,
          0, 1, 0,
          0, 1, 0,
          0, 1, 0,
          // Bottom
          0, -1, 0,
          0, -1, 0,
          0, -1, 0,
          0, -1, 0,
          // Back
          0, 0, 1,
          0, 0, 1,
          0, 0, 1,
          0, 0, 1,
        });
    normals.flip();
    return normals;
  }

  private static FloatBuffer textures(boolean mapped) {
    FloatBuffer textures = BufferUtils.createFloatBuffer(48);
    textures.flip();
    return textures;
  }

  private static IntBuffer indices() {
    IntBuffer indices = BufferUtils.createIntBuffer(36);
    indices
        .put(
            new int[] {
              0, 3, 2, // Front
              0, 2, 1,
              5, 4, 7, // Left
              6, 5, 7,
              11, 8, 9, // Right
              11, 9, 10,
              12, 15, 13, // Top
              13, 15, 14,
              17, 19, 16, // Bottom
              18, 19, 17,
              22, 23, 20, // Back
              21, 22, 20,
            })
        .flip();
    return indices;
  }
}
