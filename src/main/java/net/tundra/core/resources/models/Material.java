package net.tundra.core.resources.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.tundra.core.TundraException;
import net.tundra.core.resources.sprites.Sprite;
import org.joml.Vector3f;

public class Material {
  private int illuminationMode; // Should always be 3
  boolean mappedDiffuse, mappedSpecular, mappedHighlights, mappedAmbient, mappedAlpha, bumpMapped;
  private Sprite diffuseMap, specularMap, highlightMap, ambientMap, alphaMap, bumpMap;
  private Vector3f diffuse, specular, ambient;
  private float highlight, alpha, bumpParam;

  public Material() {
    illuminationMode = 3;
    mappedDiffuse = false;
    mappedSpecular = false;
    mappedHighlights = false;
    mappedAmbient = false;
    mappedAlpha = false;
    bumpMapped = false;
    diffuse = new Vector3f(1f, 1f, 1f);
    specular = new Vector3f(1f, 1f, 1f);
    ambient = new Vector3f(1f, 1f, 1f);
    highlight = 1f;
    alpha = 1f;
    bumpParam = 1f;
  }

  public int getIlluminationMode() {
    return illuminationMode;
  }

  public void setIlluminationMode(int mode) {
    illuminationMode = mode;
  }

  public void setDiffuse(Vector3f diffuse) {
    this.diffuse = diffuse;
    mappedDiffuse = false;
  }

  public void setDiffuseMap(Sprite sprite) {
    diffuseMap = sprite;
    mappedDiffuse = true;
  }

  public Vector3f getDiffuse() {
    return new Vector3f(diffuse);
  }

  public Sprite getDiffuseMap() {
    return diffuseMap;
  }

  public boolean mappedDiffuse() {
    return mappedDiffuse;
  }

  public void setSpecular(Vector3f specular) {
    this.specular = specular;
    mappedSpecular = false;
  }

  public void setSpecularMap(Sprite sprite) {
    specularMap = sprite;
    mappedSpecular = true;
  }

  public Vector3f getSpecular() {
    return new Vector3f(specular);
  }

  public Sprite getSpecularMap() {
    return specularMap;
  }

  public boolean mappedSpecular() {
    return mappedAmbient;
  }

  public void setAmbient(Vector3f ambient) {
    this.ambient = ambient;
    mappedAmbient = false;
  }

  public void setAmbientMap(Sprite sprite) {
    ambientMap = sprite;
    mappedAmbient = true;
  }

  public Vector3f getAmbient() {
    return new Vector3f(ambient);
  }

  public Sprite getAmbientMap() {
    return ambientMap;
  }

  public boolean mappedAmbient() {
    return mappedAmbient;
  }

  public void setHighlight(float highlight) {
    this.highlight = highlight;
    mappedHighlights = false;
  }

  public void setHighlightMap(Sprite sprite) {
    highlightMap = sprite;
    mappedHighlights = true;
  }

  public float getHighlight() {
    return highlight;
  }

  public Sprite getHighlightMap() {
    return highlightMap;
  }

  public boolean mappedHighlights() {
    return mappedHighlights;
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
    mappedAlpha = false;
  }

  public void setAlphaMap(Sprite sprite) {
    alphaMap = sprite;
    mappedAlpha = true;
  }

  public float getAlpha() {
    return alpha;
  }

  public Sprite getAlphaMap() {
    return alphaMap;
  }

  public boolean mappedAlpha() {
    return mappedAlpha;
  }

  public void setBumpMap(Sprite bumpMap, float bumpParam) {
    this.bumpMap = bumpMap;
    bumpMapped = true;
    this.bumpParam = bumpParam;
  }

  public Sprite getBumpMap() {
    return bumpMap;
  }

  public float getBumpParam() {
    return bumpParam;
  }

  public boolean bumpMapped() {
    return bumpMapped;
  }

  public static Map<String, Material> parse(String mtlFile) throws TundraException {
    Map<String, Material> materials = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(new File(mtlFile)))) {
      String line = reader.readLine();
      while (line != null) {
        if (!line.startsWith("newmtl ")) {
          line = reader.readLine();
          continue;
        }
        String name = line.split(" ")[1];
        Material material = new Material();
        materials.put(name, material);
        while ((line = reader.readLine()) != null && !line.startsWith("newmtl ")) {
          if (line.startsWith("Ns ")) material.setHighlight(Float.parseFloat(line.split(" ")[1]));
          else if (line.startsWith("d ")) material.setAlpha(Float.parseFloat(line.split(" ")[1]));
          else if (line.startsWith("illum "))
            material.setIlluminationMode(Integer.parseInt(line.split(" ")[1]));
          else if (line.startsWith("Ks ")) {
            String[] split = line.split(" ");
            material.setSpecular(
                new Vector3f(
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]),
                    Float.parseFloat(split[3])));
          } else if (line.startsWith("Kd ")) {
            String[] split = line.split(" ");
            material.setDiffuse(
                new Vector3f(
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]),
                    Float.parseFloat(split[3])));
          } else if (line.startsWith("Ka ")) {
            String[] split = line.split(" ");
            material.setAmbient(
                new Vector3f(
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]),
                    Float.parseFloat(split[3])));
          } else if (line.startsWith("map_Ks "))
            material.setSpecularMap(new Sprite(line.split(" ")[1]));
          else if (line.startsWith("map_Kd "))
            material.setDiffuseMap(new Sprite(line.split(" ")[1]));
          else if (line.startsWith("map_Ka "))
            material.setAmbientMap(new Sprite(line.split(" ")[1]));
          else if (line.startsWith("map_Ns "))
            material.setHighlightMap(new Sprite(line.split(" ")[1]));
          else if (line.startsWith("map_d ")) material.setAlphaMap(new Sprite(line.split(" ")[1]));
          else if (line.startsWith("map_Bump ") || line.startsWith("map_bump")) {
            String[] split = line.split(" ");
            if (split.length == 2) material.setBumpMap(new Sprite(split[1]), 1f);
            else material.setBumpMap(new Sprite(split[3]), Float.parseFloat(split[2]));
          }
        }
      }
    } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
      throw new TundraException("Failed to load '" + mtlFile + "'", e);
    }
    return materials;
  }
}
