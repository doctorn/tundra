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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.tundra.core.TundraException;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class Model {
  public static final Model PLANE = new Plane(), CUBE = new Cube(false);
  private int vertexArray,
      vertexHandle,
      normalHandle,
      tangentHandle,
      textureHandle,
      materialHandle,
      indexHandle,
      indexCount;
  private boolean solid;
  private List<Material> materials = new ArrayList<>();
  private TriangleIndexVertexArray vertArray;

  public Model(
      FloatBuffer vertices,
      FloatBuffer normals,
      FloatBuffer textures,
      IntBuffer indices,
      boolean solid) {
    FloatBuffer tangents = BufferUtils.createFloatBuffer(vertices.capacity());
    tangents.flip();
    IntBuffer materials = BufferUtils.createIntBuffer(vertices.capacity() / 3);
    materials.flip();
    initGL(vertices, normals, tangents, textures, materials, indices);
    initShape(indices, vertices);
    this.solid = solid;
  }

  private void initGL(
      FloatBuffer vertices,
      FloatBuffer normals,
      FloatBuffer tangents,
      FloatBuffer textures,
      IntBuffer materials,
      IntBuffer indices) {
    indexCount = indices.capacity();

    vertexArray = glGenVertexArrays();
    glBindVertexArray(vertexArray);

    vertexHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    normalHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, normalHandle);
    glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);

    tangentHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, tangentHandle);
    glBufferData(GL_ARRAY_BUFFER, tangents, GL_STATIC_DRAW);

    textureHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, textureHandle);
    glBufferData(GL_ARRAY_BUFFER, textures, GL_STATIC_DRAW);

    materialHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, materialHandle);
    glBufferData(GL_ARRAY_BUFFER, materials, GL_STATIC_DRAW);

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
    List<Integer> materialIndices = new ArrayList<>();
    Map<String, Integer> materialNameToIndex = new HashMap<>();
    int currentMaterial = 0;
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
        } else if (line.startsWith("mtllib ")) {
          materials = new ArrayList<>();
          Map<String, Material> temp = Material.parse(line.split(" ")[1]);
          int i = 0;
          for (String key : temp.keySet()) {
            materialNameToIndex.put(key, i);
            materials.add(temp.get(key));
            i++;
          }
        } else if (line.startsWith("usemtl "))
          currentMaterial = materialNameToIndex.get(line.split(" ")[1]);
        else if (line.startsWith("f ")) {
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
              materialIndices.add(currentMaterial);
            }
          } else
            throw new TundraException("Invalid face definition when reading '" + objFile + "'");
        } else continue;
      }

      FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faceCount * 9);
      FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(faceCount * 9);
      FloatBuffer tangentBuffer = BufferUtils.createFloatBuffer(faceCount * 9);
      FloatBuffer textureBuffer = BufferUtils.createFloatBuffer(faceCount * 6);
      IntBuffer materialBuffer = BufferUtils.createIntBuffer(faceCount * 3);
      IntBuffer indexBuffer = BufferUtils.createIntBuffer(faceCount * 3);

      for (int i = 0; i < faceCount; i++) {
        Vector3f[] faceVertices = new Vector3f[3];
        Vector3f[] faceNormals = new Vector3f[3];
        Vector2f[] faceTextures = new Vector2f[3];
        int[] faceMaterial = new int[3];

        for (int j = 0; j < 3; j++) {
          int index = vertexIndices.get(3 * i + j);
          faceVertices[j] = new Vector3f(vertices.get(index));

          index = textureIndices.get(3 * i + j);
          if (index != -1) faceTextures[j] = new Vector2f(textures.get(index));
          else faceTextures[j] = new Vector2f();

          index = normalIndices.get(3 * i + j);
          if (index != -1) faceNormals[j] = new Vector3f(normals.get(index));
          else faceNormals[j] = new Vector3f(1, 0, 0); // TODO calculate dynamically

          faceMaterial[j] = materialIndices.get(3 * i + j);
        }

        Vector3f tangent = new Vector3f(0, 0, 1);
        Vector3f edge1 = new Vector3f(faceVertices[1]).sub(faceVertices[0]);
        Vector3f edge2 = new Vector3f(faceVertices[2]).sub(faceVertices[0]);
        Vector2f deltaUV1 = new Vector2f(faceTextures[1]).sub(faceTextures[0]);
        Vector2f deltaUV2 = new Vector2f(faceTextures[2]).sub(faceTextures[0]);
        tangent.x = deltaUV2.y * edge1.x - deltaUV1.y * edge2.x;
        tangent.y = deltaUV2.y * edge1.y - deltaUV1.y * edge2.y;
        tangent.z = deltaUV2.y * edge1.z - deltaUV1.y * edge2.z;
        tangent.normalize();

        for (int j = 0; j < 3; j++) {
          faceVertices[j].get(vertexBuffer);
          vertexBuffer.position(vertexBuffer.position() + 3);

          faceTextures[j].get(textureBuffer);
          textureBuffer.position(textureBuffer.position() + 2);

          faceNormals[j].get(normalBuffer);
          normalBuffer.position(normalBuffer.position() + 3);

          tangent.get(tangentBuffer);
          tangentBuffer.position(tangentBuffer.position() + 3);

          materialBuffer.put(faceMaterial[j]);
          indexBuffer.put(3 * i + j);
        }
      }

      vertexBuffer.flip();
      normalBuffer.flip();
      tangentBuffer.flip();
      textureBuffer.flip();
      materialBuffer.flip();
      indexBuffer.flip();

      initGL(vertexBuffer, normalBuffer, tangentBuffer, textureBuffer, materialBuffer, indexBuffer);
      initShape(indexBuffer, vertexBuffer);

      this.solid = true;
    } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
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

  public int getTangents() {
    return tangentHandle;
  }

  public int getTextureCoords() {
    return textureHandle;
  }

  public int getMaterials() {
    return materialHandle;
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

  public boolean materialed() {
    return materials.size() != 0;
  }

  public List<Material> getMaterialDescriptors() {
    return materials;
  }
}
