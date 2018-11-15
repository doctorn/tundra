package net.tundra.examples;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.graphics.Graphics;
import net.tundra.core.resources.models.Model;
import net.tundra.core.resources.shaders.FragmentShader;
import net.tundra.core.resources.shaders.Program;
import net.tundra.core.resources.shaders.VertexShader;
import net.tundra.core.resources.sprites.Sprite;
import net.tundra.core.scene.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class TestGame extends Game {
  private Program program;
  private Model model;
  private Sprite sprite;
  private Camera camera;
  private float angle;

  public TestGame() {
    super(800, 600, "tundra", false);
  }

  @Override
  public void init() throws TundraException {
    camera = new TestCamera();
    VertexShader vertex = new VertexShader("shaders/vert.glsl");
    FragmentShader fragment = new FragmentShader("shaders/frag.glsl");
    program = new Program(vertex, fragment);
    vertex.delete();
    fragment.delete();

    sprite = new Sprite("res/test.png");

    FloatBuffer vertices = BufferUtils.createFloatBuffer(12);
    vertices
        .put(-0.5f)
        .put(-0.5f)
        .put(0)
        .put(-0.5f)
        .put(0.5f)
        .put(0)
        .put(0.5f)
        .put(0.5f)
        .put(0)
        .put(0.5f)
        .put(-0.5f)
        .put(0f)
        .flip();

    FloatBuffer normals = BufferUtils.createFloatBuffer(12);
    normals
        .put(0)
        .put(0)
        .put(1f)
        .put(0)
        .put(0)
        .put(1f)
        .put(0)
        .put(0)
        .put(1f)
        .put(0)
        .put(0)
        .put(1f)
        .flip();

    IntBuffer indices = BufferUtils.createIntBuffer(6);
    indices.put(3).put(1).put(0).put(3).put(2).put(1).flip();

    FloatBuffer textures = BufferUtils.createFloatBuffer(8);
    textures.put(0).put(1).put(0).put(0).put(1).put(0).put(1).put(1).flip();

    model = new Model(vertices, normals, textures, indices);
  }

  @Override
  public void update(int delta) throws TundraException {
    camera.update(delta);
    angle += 0.001f * delta;
  }

  @Override
  public void render(Graphics g) throws TundraException {
    g.activate(program);
    g.use(camera);
    g.drawModel(model, sprite, new Matrix4f().rotate(angle, new Vector3f(0, 1, 0)));
  }

  public static void main(String args[]) throws TundraException {
    TestGame test = new TestGame();
    test.start();
  }
}
