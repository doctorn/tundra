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
import net.tundra.core.scene.InterfaceCamera;
import net.tundra.core.scene.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Graphics {
  private static final Camera INTERFACE_CAMERA = new InterfaceCamera();

  private Game game;
  private Program program;
  private Camera camera;
  private Light[] lights;

  public Graphics(Game game) {
    this.game = game;
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
  }

  public void activate(Program program) {
    this.program = program;
  }

  public void use(Camera camera, Light[] lights) {
    this.camera = camera;
    this.lights = lights;
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
    program.uniform("model_matrix", transform);
    program.uniform("cam_pos", camera.getPosition());
    program.uniform("ambient", new Vector3f(0.2f, 0.2f, 0.2f));
    program.uniform("alpha", 1f);
    for (int i = 0; i < lights.length; i++) {
      program.uniform("lights[" + i + "]", lights[i]);
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

  public void clear() {
    glClearColor(0f, 0f, 0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }
}
