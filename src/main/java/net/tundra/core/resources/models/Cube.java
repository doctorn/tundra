package net.tundra.core.resources.models;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

public class Cube extends Model {
  public Cube(boolean mapped) {
    super(vertices(), normals(), textures(mapped), indices());
  }

  private static FloatBuffer vertices() {
    FloatBuffer vertices = BufferUtils.createFloatBuffer(24);
    vertices
        .put(
            new float[] {
              -1, -1, -1, // 0
              1, -1, -1, // 1
              1, 1, -1, // 2
              -1, 1, -1, // 3
              -1, -1, 1, // 4
              1, -1, 1, // 5
              1, 1, 1, // 6
              -1, 1, 1 // 7
            })
        .flip();
    return vertices;
  }

  private static FloatBuffer normals() {
    FloatBuffer normals = BufferUtils.createFloatBuffer(24);
    normals.put(
      new float[] {
        -1, -1, -1, // 0
        1, -1, -1, // 1
        1, 1, -1, // 2
        -1, 1, -1, // 3
        -1, -1, 1, // 4
        1, -1, 1, // 5
        1, 1, 1, // 6
        -1, 1, 1 // 7
      });
    normals.flip();
    return normals;
  }

  private static FloatBuffer textures(boolean mapped) {
    FloatBuffer textures = BufferUtils.createFloatBuffer(16);
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
              3, 7, 6, // Top
              3, 6, 2,
              1, 2, 5, // Right
              2, 6, 5,
              4, 3, 0, // Left
              7, 3, 4,
              1, 4, 0, // Bottom
              1, 5, 4,
              6, 7, 4, // Back
              5, 6, 4
            })
        .flip();
    return indices;
  }
}
