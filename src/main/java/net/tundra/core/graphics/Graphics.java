package net.tundra.core.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.resources.models.Model;
import net.tundra.core.resources.shaders.Program;
import net.tundra.core.resources.sprites.Sprite;
import net.tundra.core.scene.Camera;
import org.joml.Matrix4f;

public class Graphics {
  private Game game;
  private Program program;
  private Camera camera;

  public Graphics(Game game) {
    this.game = game;
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
  }

  public void activate(Program program) {
    this.program = program;
  }

  public void use(Camera camera) {
    this.camera = camera;
  }

  public void drawModelWireframe(Model model, Matrix4f transform) throws TundraException {
    glPolygonMode(GL_FRONT, GL_LINE);
    glPolygonMode(GL_BACK, GL_LINE);
    glDisable(GL_CULL_FACE);
    drawModel(model, transform);
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

  public void clear() {
    glClearColor(0f, 0f, 0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }
}
