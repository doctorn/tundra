package net.tundra.core.resources.models;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import net.tundra.core.TundraException;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class Model {
  public static final Model PLANE = new Plane(), CUBE = new Cube(false);
  private int vertexArray, vertexHandle, normalHandle, textureHandle, indexHandle, indexCount;
  private boolean solid;
  private TriangleIndexVertexArray vertArray;

  public Model(
      FloatBuffer vertices,
      FloatBuffer normals,
      FloatBuffer textures,
      IntBuffer indices,
      boolean solid) {
    initGL(vertices, normals, textures, indices);
    initShape(indices, vertices);
    this.solid = solid;
  }

  private void initGL(
      FloatBuffer vertices, FloatBuffer normals, FloatBuffer textures, IntBuffer indices) {
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

  private void initShape(IntBuffer indices, FloatBuffer vertices) {
    vertices.rewind();
    indices.rewind();
    IndexedMesh indexedMesh = new IndexedMesh();
    indexedMesh.numTriangles = indices.capacity() / 3;
    indexedMesh.triangleIndexBase =
        BufferUtils.createByteBuffer(Integer.BYTES * indices.capacity());
    indexedMesh.triangleIndexBase.asIntBuffer().put(indices).flip();
    indexedMesh.triangleIndexStride = 3 * Integer.BYTES;
    indexedMesh.numVertices = vertices.capacity() / 3;
    indexedMesh.vertexBase = BufferUtils.createByteBuffer(Float.BYTES * vertices.capacity());
    indexedMesh.vertexBase.asFloatBuffer().put(vertices).flip();
    indexedMesh.vertexStride = 3 * Float.BYTES;

    vertArray = new TriangleIndexVertexArray();
    vertArray.addIndexedMesh(indexedMesh);
  }

  public Model(String objFile) throws TundraException {
    List<Vector3f> vertices = new ArrayList<>();
    List<Vector2f> textures = new ArrayList<>();
    List<Vector3f> normals = new ArrayList<>();
    List<Integer> vertexIndices = new ArrayList<>();
    List<Integer> textureIndices = new ArrayList<>();
    List<Integer> normalIndices = new ArrayList<>();
    int faceCount = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(new File(objFile)))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("v ")) {
          String[] split = line.split(" ");
          if (split.length == 4) {
            vertices.add(
                new Vector3f(
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]),
                    Float.parseFloat(split[3])));
          } else if (split.length == 5) {
            float w = Float.parseFloat(split[4]);
            vertices.add(
                new Vector3f(
                        Float.parseFloat(split[1]),
                        Float.parseFloat(split[2]),
                        Float.parseFloat(split[3]))
                    .mul(1f / w));
          } else
            throw new TundraException("Invalid vertex definition when reading '" + objFile + "'");
        } else if (line.startsWith("vt ")) {
          String[] split = line.split(" ");
          if (split.length == 3) {
            textures.add(new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2])));
          } else
            throw new TundraException(
                "Invalid texture coordinate definition when reading '" + objFile + "'");
        } else if (line.startsWith("vn ")) {
          String[] split = line.split(" ");
          if (split.length == 4) {
            normals.add(
                new Vector3f(
                        Float.parseFloat(split[1]),
                        Float.parseFloat(split[2]),
                        Float.parseFloat(split[3]))
                    .normalize());
          } else
            throw new TundraException("Invalid normal definition when reading '" + objFile + "'");
        } else if (line.startsWith("f ")) {
          faceCount++;
          String[] split = line.split(" ");
          if (split.length == 4) {
            for (int i = 1; i < 4; i++) {
              if (split[i].contains("/")) {
                String[] subSplit = split[i].split("/");
                if (subSplit.length == 3) {
                  vertexIndices.add(Integer.parseInt(subSplit[0]) - 1);
                  if (subSplit[1].equals("")) textureIndices.add(-1);
                  else textureIndices.add(Integer.parseInt(subSplit[1]) - 1);
                  normalIndices.add(Integer.parseInt(subSplit[2]) - 1);
                } else if (subSplit.length == 2) {
                  vertexIndices.add(Integer.parseInt(subSplit[0]) - 1);
                  textureIndices.add(Integer.parseInt(subSplit[1]) - 1);
                  normalIndices.add(-1);
                } else
                  throw new TundraException(
                      "Invalid face definition when reading '" + objFile + "'");
              } else {
                vertexIndices.add(Integer.parseInt(split[i]) - 1);
                textureIndices.add(-1);
                normalIndices.add(-1);
              }
            }
          } else
            throw new TundraException("Invalid face definition when reading '" + objFile + "'");
        } else continue;
      }

      FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faceCount * 9);
      FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(faceCount * 9);
      FloatBuffer textureBuffer = BufferUtils.createFloatBuffer(faceCount * 6);
      IntBuffer indexBuffer = BufferUtils.createIntBuffer(faceCount * 3);

      for (int i = 0; i < faceCount; i++) {
        for (int j = 0; j < 3; j++) {
          int index = vertexIndices.get(3 * i + j);
          Vector3f vertex = new Vector3f(vertices.get(index));
          vertex.get(vertexBuffer);
          vertexBuffer.position(vertexBuffer.position() + 3);

          index = textureIndices.get(3 * i + j);
          if (index != -1) {
            Vector2f texture = new Vector2f(textures.get(index));
            texture.get(textureBuffer);
          } else {
            new Vector2f().get(textureBuffer);
          }
          textureBuffer.position(textureBuffer.position() + 2);

          index = normalIndices.get(3 * i + j);
          if (index != -1) {
            Vector3f normal = new Vector3f(normals.get(index));
            normal.get(normalBuffer);
          } else new Vector3f(1f, 0f, 0f).get(normalBuffer);
          normalBuffer.position(normalBuffer.position() + 3);

          indexBuffer.put(3 * i + j);
        }
      }

      vertexBuffer.flip();
      normalBuffer.flip();
      textureBuffer.flip();
      indexBuffer.flip();

      initGL(vertexBuffer, normalBuffer, textureBuffer, indexBuffer);
      initShape(indexBuffer, vertexBuffer);

      this.solid = true;
    } catch (IOException | NumberFormatException e) {
      throw new TundraException("Failed to load '" + objFile + "'", e);
    }
  }

  public CollisionShape getCollisionShape() {
    return new BvhTriangleMeshShape(vertArray, false);
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

  public boolean solid() {
    return solid;
  }
}
