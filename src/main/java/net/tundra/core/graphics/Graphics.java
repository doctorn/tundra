package net.tundra.core.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.resources.models.Model;
import net.tundra.core.resources.shaders.FragmentShader;
import net.tundra.core.resources.shaders.Program;
import net.tundra.core.resources.shaders.VertexShader;
import net.tundra.core.resources.sprites.Font;
import net.tundra.core.resources.sprites.Sprite;
import net.tundra.core.scene.Camera;
import net.tundra.core.scene.InterfaceCamera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Graphics {
  private static final Camera INTERFACE_CAMERA = new InterfaceCamera();
  private static final int MAX_LIGHTS = 128;

  private Game game;
  private Program program;
  private Camera camera;

  public Graphics(Game game) throws TundraException {
    this.game = game;
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);

    VertexShader vertex = new VertexShader("shaders/vert.glsl");
    FragmentShader fragment = new FragmentShader("shaders/frag.glsl");
    program = new Program(vertex, fragment);
    vertex.delete();
    fragment.delete();
  }

  public void use(Camera camera) {
    this.camera = camera;
  }

  public void drawModelWireframe(Model model, Matrix4f transform) throws TundraException {
    glPolygonMode(GL_FRONT, GL_LINE);
    glPolygonMode(GL_BACK, GL_LINE);
    glDisable(GL_CULL_FACE);
    boolean temp = game.lightingEnabled();
    game.setLighting(false);
    drawModel(model, transform);
    game.setLighting(temp);
    glEnable(GL_CULL_FACE);
    glPolygonMode(GL_FRONT, GL_FILL);
    glPolygonMode(GL_BACK, GL_FILL);
  }

  public void drawModel(Model model, Matrix4f transform) throws TundraException {
    drawModel(model, null, transform);
  }

  public void drawModel(Model model, Sprite texture, Matrix4f transform) throws TundraException {
    glUseProgram(program.getProgram());
    program.uniform(
        "mvp_matrix",
        camera.getViewProjectionMatrix(game.getWidth(), game.getHeight()).mul(transform));
    program.uniform("model_matrix", transform);
    Matrix4f transformInverse = new Matrix4f();
    transform.invert(transformInverse);
    transformInverse.transpose();
    program.uniform("model_matrix_inverse", transformInverse);
    program.uniform("cam_pos", camera.getPosition());
    program.uniform("ambient", new Vector3f(0.05f, 0.05f, 0.05f));
    program.uniform("alpha", 100f);
    program.uniform("lighting", game.lightingEnabled());
    if (game.lightingEnabled()) {
      for (int i = 0; i < game.getLights().size() && i < MAX_LIGHTS; i++)
        program.uniform("lights[" + i + "]", game.getLights().get(i));
      program.uniform("light_count", game.getLights().size());
    }

    if (texture != null) {
      program.uniform("texturing", true);
      glBindTexture(GL_TEXTURE_2D, texture.getTexture());
      program.uniform("tex_start", texture.getStartVector());
      program.uniform("tex_size", texture.getSizeVector());
    } else {
      program.uniform("texturing", false);
      glBindTexture(GL_TEXTURE_2D, 0);
    }
    glBindVertexArray(model.getModel());
    glBindBuffer(GL_ARRAY_BUFFER, model.getVertices());
    program.attrib("vertex", 3);
    glBindBuffer(GL_ARRAY_BUFFER, model.getNormals());
    program.attrib("normal", 3);
    glBindBuffer(GL_ARRAY_BUFFER, model.getTextureCoords());
    program.attrib("tex_coord", 2);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, model.getIndices());
    glDrawElements(GL_TRIANGLES, model.getIndexCount(), GL_UNSIGNED_INT, 0);
    glBindTexture(GL_TEXTURE_2D, 0);
    glBindVertexArray(0);
    glUseProgram(0);
  }

  public void drawString(String string, Font font, int x, int y) throws TundraException {
    for (int i = 0; i < string.length(); i++)
      drawImage(font.getCharacter(string.charAt(i)), x + font.getCharacterWidth() * i, y);
  }

  public void drawImage(Sprite sprite, int x, int y) throws TundraException {
    boolean temp = game.lightingEnabled();
    game.setLighting(false);
    glDisable(GL_DEPTH_TEST);
    Camera previous = camera;
    camera = INTERFACE_CAMERA;
    drawModel(
        Model.PLANE,
        sprite,
        new Matrix4f()
            .translate(x, game.getHeight() - y, 0)
            .scale(sprite.getWidth() / 2f, sprite.getHeight() / 2f, 1)
            .translate(1, -1, 0));
    camera = previous;
    game.setLighting(temp);
    glEnable(GL_DEPTH_TEST);
  }

  public void clear() {
    glClearColor(0f, 0f, 0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }
}
