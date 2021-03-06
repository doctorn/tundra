package net.tundra.core.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import net.tundra.core.Game;
import net.tundra.core.TundraException;
import net.tundra.core.resources.models.Material;
import net.tundra.core.resources.models.Model;
import net.tundra.core.resources.shaders.FragmentShader;
import net.tundra.core.resources.shaders.Program;
import net.tundra.core.resources.shaders.VertexShader;
import net.tundra.core.resources.sprites.Font;
import net.tundra.core.resources.sprites.Sprite;
import net.tundra.core.scene.Camera;
import net.tundra.core.scene.InterfaceCamera;
import net.tundra.core.scene.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class Graphics {
  private static final int MAX_LIGHTS = 64;
  public static final int SHADOW_WIDTH = 4096, SHADOW_HEIGHT = 4096;
  public static final Camera INTERFACE_CAMERA = new InterfaceCamera();

  private Game game;
  private Program program, shadows;
  private List<Draw> scene = new ArrayList<>();
  private List<Draw> external = new ArrayList<>();
  private Vector4f colour = new Vector4f(1f, 1f, 1f, 1f);

  private int depthBuffer, depthMap;

  public Graphics(Game game) throws TundraException {
    this.game = game;
    glClearColor(0f, 0f, 0f, 1.0f);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    VertexShader vertex = new VertexShader(getClass().getResourceAsStream("vert.glsl"));
    FragmentShader fragment = new FragmentShader(getClass().getResourceAsStream("frag.glsl"));
    program = new Program(vertex, fragment);
    vertex.delete();
    fragment.delete();

    vertex = new VertexShader(getClass().getResourceAsStream("shadow_vert.glsl"));
    fragment = new FragmentShader(getClass().getResourceAsStream("shadow_frag.glsl"));
    shadows = new Program(vertex, fragment);
    vertex.delete();
    fragment.delete();

    depthBuffer = glGenFramebuffers();
    depthMap = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, depthMap);
    glTexImage2D(
        GL_TEXTURE_2D,
        0,
        GL_DEPTH_COMPONENT,
        Graphics.SHADOW_WIDTH,
        Graphics.SHADOW_HEIGHT,
        0,
        GL_DEPTH_COMPONENT,
        GL_FLOAT,
        (ByteBuffer) null);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
    FloatBuffer colour = BufferUtils.createFloatBuffer(4);
    colour.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
    glTexParameter(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, colour);
    glBindTexture(GL_TEXTURE_2D, 0);
  }

  public void setClearColour(Vector3f colour) {
    glClearColor(colour.x, colour.y, colour.z, 1.0f);
  }

  public void setColour(Vector3f colour) {
    this.colour = new Vector4f(colour.x, colour.y, colour.z, 1f);
  }

  public void setColour(Vector4f colour) {
    this.colour = colour;
  }

  public void drawModelWireframe(Model model, Matrix4f transform) throws TundraException {
    scene.add(new Draw(false, true, false, 1f, model, null, transform, colour));
  }

  public void drawModel(Model model, Matrix4f transform) throws TundraException {
    drawModel(model, null, transform);
  }

  public void drawModelFlash(Model model, Matrix4f transform, Vector4f colour)
      throws TundraException {
    drawModelFlash(model, null, transform, colour);
  }

  public void drawModel(Model model, Sprite texture, Matrix4f transform) throws TundraException {
    scene.add(
        new Draw(
            game.getCurrentState().lightingEnabled(),
            false,
            false,
            1f,
            model,
            texture,
            transform,
            colour));
  }

  public void drawModelFlash(Model model, Sprite texture, Matrix4f transform, Vector4f colour)
      throws TundraException {
    scene.add(
        new Draw(
            game.getCurrentState().lightingEnabled(),
            false,
            true,
            1f,
            model,
            texture,
            transform,
            colour));
  }

  public void drawString(String string, Font font, int x, int y) throws TundraException {
    for (int i = 0; i < string.length(); i++)
      drawImage(font.getCharacter(string.charAt(i)), x + font.getCharacterWidth() * i, y);
  }

  public void drawString(String string, Font font, int x, int y, float opacity)
      throws TundraException {
    for (int i = 0; i < string.length(); i++)
      drawImage(font.getCharacter(string.charAt(i)), x + font.getCharacterWidth() * i, y, opacity);
  }

  public void drawStringFlash(String string, Font font, int x, int y, Vector4f colour)
      throws TundraException {
    for (int i = 0; i < string.length(); i++)
      drawImageFlash(
          font.getCharacter(string.charAt(i)), x + font.getCharacterWidth() * i, y, colour);
  }

  public void drawImage(Sprite sprite, int x, int y) throws TundraException {
    external.add(
        new Draw(
            false,
            false,
            false,
            1f,
            Model.PLANE,
            sprite,
            new Matrix4f()
                .translate(x, game.getHeight() - y, 0)
                .scale(sprite.getWidth() / 2f, sprite.getHeight() / 2f, 1)
                .translate(1, -1, 0),
            colour));
  }

  public void drawImage(Sprite sprite, int x, int y, float opacity) throws TundraException {
    external.add(
        new Draw(
            false,
            false,
            false,
            opacity,
            Model.PLANE,
            sprite,
            new Matrix4f()
                .translate(x, game.getHeight() - y, 0)
                .scale(sprite.getWidth() / 2f, sprite.getHeight() / 2f, 1)
                .translate(1, -1, 0),
            colour));
  }

  public void drawImageFlash(Sprite sprite, int x, int y, Vector4f colour) throws TundraException {
    external.add(
        new Draw(
            false,
            false,
            true,
            1f,
            Model.PLANE,
            sprite,
            new Matrix4f()
                .translate(x, game.getHeight() - y, 0)
                .scale(sprite.getWidth() / 2f, sprite.getHeight() / 2f, 1)
                .translate(1, -1, 0),
            colour));
  }

  public void drawImage(Sprite sprite, int x, int y, int width, int height) throws TundraException {
    external.add(
        new Draw(
            false,
            false,
            false,
            1f,
            Model.PLANE,
            sprite,
            new Matrix4f()
                .translate(x, game.getHeight() - y, 0)
                .scale(width / 2f, height / 2f, 1)
                .translate(1, -1, 0),
            colour));
  }

  public void drawImage(Sprite sprite, int x, int y, int width, int height, float opacity)
      throws TundraException {
    external.add(
        new Draw(
            false,
            false,
            false,
            opacity,
            Model.PLANE,
            sprite,
            new Matrix4f()
                .translate(x, game.getHeight() - y, 0)
                .scale(width / 2f, height / 2f, 1)
                .translate(1, -1, 0),
            colour));
  }

  public void drawImageFlash(Sprite sprite, int x, int y, int width, int height, Vector4f colour)
      throws TundraException {
    external.add(
        new Draw(
            false,
            false,
            true,
            1f,
            Model.PLANE,
            sprite,
            new Matrix4f()
                .translate(x, game.getHeight() - y, 0)
                .scale(width / 2f, height / 2f, 1)
                .translate(1, -1, 0),
            colour));
  }

  public void drawRect(int x, int y, int width, int height) throws TundraException {
    external.add(
        new Draw(
            false,
            true,
            false,
            1f,
            Model.PLANE,
            null,
            new Matrix4f()
                .translate(x, game.getHeight() - y, 0)
                .scale(width / 2f, height / 2f, 1f)
                .translate(1, -1, 0),
            colour));
  }

  public void fillRect(int x, int y, int width, int height) throws TundraException {
    external.add(
        new Draw(
            false,
            false,
            false,
            1f,
            Model.PLANE,
            null,
            new Matrix4f()
                .translate(x, game.getHeight() - y, 0)
                .scale(width / 2f, height / 2f, 1f)
                .translate(1, -1, 0),
            colour));
  }

  public void render() throws TundraException {
    Matrix4f shadowMatrix = null;
    if (game.getCurrentState().shadowMapping()) {
      glUseProgram(shadows.getProgram());
      glBindFramebuffer(GL_FRAMEBUFFER, depthBuffer);
      glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap, 0);
      glDrawBuffer(GL_NONE);
      glReadBuffer(GL_NONE);
      glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
      glClear(GL_DEPTH_BUFFER_BIT);
      shadowMatrix =
          game.getCurrentState()
              .getShadowCamera()
              .getViewProjectionMatrix(SHADOW_WIDTH, SHADOW_HEIGHT);
      for (Draw draw : scene) draw.shadowMap(shadowMatrix);
      glBindTexture(GL_TEXTURE_2D, 0);
      glBindFramebuffer(GL_FRAMEBUFFER, 0);
      glViewport(0, 0, game.getWidth(), game.getHeight());
    }

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glUseProgram(program.getProgram());
    program.uniform("shadow_mapping", game.getCurrentState().shadowMapping());
    if (game.getCurrentState().shadowMapping()) {
      program.uniform("shadow_dir", game.getCurrentState().getShadowCamera().getLook().mul(-1));
      program.uniform("shadow_vp_matrix", shadowMatrix);
    }
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, depthMap);
    program.uniform("depth_map", 1);
    program.uniform("cam_pos", game.getCurrentState().getCamera().getPosition());
    List<Light> lights = game.getCurrentState().getLights();
    for (int i = 0; i < lights.size() && i < MAX_LIGHTS; i++)
      program.uniform("lights[" + i + "]", lights.get(i));
    program.uniform("light_count", lights.size());
    for (Draw draw : scene)
      draw.execute(
          game.getCurrentState()
              .getCamera()
              .getViewProjectionMatrix(game.getWidth(), game.getHeight()));
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, 0);
    glActiveTexture(GL_TEXTURE0);
    glUseProgram(0);

    glDisable(GL_DEPTH_TEST);
    glUseProgram(program.getProgram());
    program.uniform("cam_pos", INTERFACE_CAMERA.getPosition());
    program.uniform("shadow_mapping", false);
    for (Draw draw : external)
      draw.execute(INTERFACE_CAMERA.getViewProjectionMatrix(game.getWidth(), game.getHeight()));
    glEnable(GL_DEPTH_TEST);
    glUseProgram(0);

    scene = new ArrayList<>();
    external = new ArrayList<>();
  }

  private class Draw {
    private boolean lighting, wireframe, flash;
    private float opacity;
    private Model model;
    private Matrix4f transform;
    private Sprite texture;
    private Vector4f colour;

    public Draw(
        boolean lighting,
        boolean wireframe,
        boolean flash,
        float opacity,
        Model model,
        Sprite texture,
        Matrix4f transform,
        Vector4f colour) {
      this.lighting = lighting;
      this.wireframe = wireframe;
      this.flash = flash;
      this.opacity = opacity;
      this.model = model;
      this.transform = transform;
      this.texture = texture;
      this.colour = colour;
    }

    public void shadowMap(Matrix4f vpMatrix) throws TundraException {
      if (lighting) {
        Matrix4f mvpMatrix = new Matrix4f();
        vpMatrix.mul(transform, mvpMatrix);
        if (!mvpMatrix.testSphere(0, 0, 0, model.getBoundingRadius())) return;
        if (model.solid()) glCullFace(GL_FRONT);
        if (texture != null) {
          shadows.uniform("texturing", true);
          glActiveTexture(GL_TEXTURE0);
          glBindTexture(GL_TEXTURE_2D, texture.getTexture());
          shadows.uniform("tex", 0);
          shadows.uniform("tex_start", texture.getStartVector());
          shadows.uniform("tex_size", texture.getSizeVector());
        } else {
          shadows.uniform("texturing", false);
          glActiveTexture(GL_TEXTURE0);
          glBindTexture(GL_TEXTURE_2D, 0);
        }
        shadows.uniform("mvp_matrix", mvpMatrix);
        glBindBuffer(GL_ARRAY_BUFFER, model.getVertices());
        shadows.attrib("vertex", 3);
        glBindBuffer(GL_ARRAY_BUFFER, model.getTextureCoords());
        shadows.attrib("tex_coord", 2);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, model.getIndices());
        glDrawElements(GL_TRIANGLES, model.getIndexCount(), GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
        if (model.solid()) glCullFace(GL_BACK);
      }
    }

    public void execute(Matrix4f vpMatrix) throws TundraException {
      Matrix4f mvpMatrix = new Matrix4f();
      vpMatrix.mul(transform, mvpMatrix);
      if (!mvpMatrix.testSphere(0, 0, 0, model.getBoundingRadius())) return;

      if (wireframe) {
        glPolygonMode(GL_FRONT, GL_LINE);
        glPolygonMode(GL_BACK, GL_LINE);
        glDisable(GL_CULL_FACE);
      }

      program.uniform("col", colour);
      program.uniform("mvp_matrix", mvpMatrix);
      program.uniform("model_matrix", transform);
      Matrix4f transformInverse = new Matrix4f();
      transform.invert(transformInverse);
      transformInverse.transpose();
      program.uniform("model_matrix_inverse", transformInverse);
      program.uniform("lighting", lighting);
      program.uniform("flash", flash);
      program.uniform("opacity", opacity);

      glBindVertexArray(model.getModel());
      glBindBuffer(GL_ARRAY_BUFFER, model.getVertices());
      program.attrib("vertex", 3);
      glBindBuffer(GL_ARRAY_BUFFER, model.getNormals());
      program.attrib("normal", 3);
      if (model.materialed()) {
        glBindBuffer(GL_ARRAY_BUFFER, model.getTangents());
        program.attrib("tangent", 3);
        glBindBuffer(GL_ARRAY_BUFFER, model.getMaterials());
        program.attrib("material", 1);
      }
      if (model.materialed() || texture != null) {
        glBindBuffer(GL_ARRAY_BUFFER, model.getTextureCoords());
        program.attrib("tex_coord", 2);
      }
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, model.getIndices());

      program.uniform("materialed", model.materialed());
      if (model.materialed()) {
        for (int i = 0; i < model.getMaterialDescriptors().size(); i++) {
          program.uniform("texturing", false);
          program.uniform("current_material", i);

          Material current = model.getMaterialDescriptors().get(i);
          program.uniform("material", current);

          glActiveTexture(GL_TEXTURE2);
          if (current.mappedDiffuse())
            glBindTexture(GL_TEXTURE_2D, current.getDiffuseMap().getTexture());
          else glBindTexture(GL_TEXTURE_2D, 0);
          glActiveTexture(GL_TEXTURE3);
          if (current.mappedSpecular())
            glBindTexture(GL_TEXTURE_2D, current.getSpecularMap().getTexture());
          else glBindTexture(GL_TEXTURE_2D, 0);
          glActiveTexture(GL_TEXTURE4);
          if (current.mappedAmbient())
            glBindTexture(GL_TEXTURE_2D, current.getAmbientMap().getTexture());
          else glBindTexture(GL_TEXTURE_2D, 0);
          glActiveTexture(GL_TEXTURE5);
          if (current.mappedHighlights())
            glBindTexture(GL_TEXTURE_2D, current.getHighlightMap().getTexture());
          else glBindTexture(GL_TEXTURE_2D, 0);
          glActiveTexture(GL_TEXTURE6);
          if (current.bumpMapped()) glBindTexture(GL_TEXTURE_2D, current.getBumpMap().getTexture());
          else glBindTexture(GL_TEXTURE_2D, 0);

          glDrawElements(GL_TRIANGLES, model.getIndexCount(), GL_UNSIGNED_INT, 0);
        }

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE5);
        glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_2D, 0);
      } else {
        if (texture != null) {
          program.uniform("texturing", true);
          glActiveTexture(GL_TEXTURE0);
          glBindTexture(GL_TEXTURE_2D, texture.getTexture());
          program.uniform("tex", 0);
          program.uniform("tex_start", texture.getStartVector());
          program.uniform("tex_size", texture.getSizeVector());
        } else {
          program.uniform("texturing", false);
          glActiveTexture(GL_TEXTURE0);
          glBindTexture(GL_TEXTURE_2D, 0);
        }
        glDrawElements(GL_TRIANGLES, model.getIndexCount(), GL_UNSIGNED_INT, 0);
      }

      if (wireframe) {
        glEnable(GL_CULL_FACE);
        glPolygonMode(GL_FRONT, GL_FILL);
        glPolygonMode(GL_BACK, GL_FILL);
      }

      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D, 0);
      glBindVertexArray(0);
    }
  }
}
