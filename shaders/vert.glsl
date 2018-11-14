#version 130

in vec3 vertex;
in vec2 texcoord;

out vec2 frag_texcoord;

void main() {
  gl_Position = vec4(vertex, 1.0);
  frag_texcoord = texcoord;
}
