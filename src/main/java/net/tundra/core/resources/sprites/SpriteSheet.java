package net.tundra.core.resources.sprites;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import net.tundra.core.TundraException;

public class SpriteSheet {
  private int texture;
  private int width, height;
  private int spriteWidth, spriteHeight;
  private boolean hasAlpha = false;
  private String file;

  public SpriteSheet(String file, int spriteWidth, int spriteHeight) throws TundraException {
    this.spriteHeight = spriteHeight;
    this.spriteWidth = spriteWidth;
    this.file = file;

    try {
      BufferedImage image = ImageIO.read(new File(file));

      width = image.getWidth();
      height = image.getHeight();
      ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);
      for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
          Color color = new Color(image.getRGB(i, j), true);
          if (color.getAlpha() < 255) hasAlpha = true;
          buffer.put((i + j * width) * 4 + 0, (byte) color.getRed());
          buffer.put((i + j * width) * 4 + 1, (byte) color.getGreen());
          buffer.put((i + j * width) * 4 + 2, (byte) color.getBlue());
          buffer.put((i + j * width) * 4 + 3, (byte) color.getAlpha());
        }
      }

      buffer.flip();
      buffer.limit(buffer.capacity());

      texture = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, texture);

      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
      glGenerateMipmap(GL_TEXTURE_2D);
      glBindTexture(GL_TEXTURE_2D, 0);
    } catch (IOException e) {
      throw new TundraException("Failed to create spritesheet '" + file + "'", e);
    }
  }

  public SpriteSheet(SpriteSheet other) {
    texture = other.texture;
    spriteWidth = other.spriteWidth;
    spriteHeight = other.spriteHeight;
    width = other.width;
    height = other.height;
    hasAlpha = other.hasAlpha;
  }

  public void enableSmoothing() {
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glGenerateMipmap(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, 0);
  }

  public Sprite getSprite(int coordX, int coordY) {
    return new Sprite(this, coordX, coordY);
  }

  public SpriteSheet scale(float sf) {
    SpriteSheet scaled = new SpriteSheet(this);
    scaled.spriteWidth = Math.round(spriteWidth * sf);
    scaled.spriteHeight = Math.round(spriteHeight * sf);
    scaled.width = Math.round(width * sf);
    scaled.height = Math.round(height * sf);
    return scaled;
  }

  public int getTexture() {
    return texture;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getSpriteWidth() {
    return spriteWidth;
  }

  public int getSpriteHeight() {
    return spriteHeight;
  }

  public boolean HasAlpha() {
    return hasAlpha;
  }

  public String getFile() {
    return file;
  }

  public int getRows() {
    return height / spriteHeight;
  }

  public int getColumns() {
    return width / spriteWidth;
  }

  public void setSpriteWidth(int spriteWidth) {
    this.spriteWidth = spriteWidth;
  }

  public void setSpriteHeight(int spriteHeight) {
    this.spriteHeight = spriteHeight;
  }
}
