#version 130

in vec3 vertex;
in vec2 tex_coord;

uniform mat4 mvp_matrix;

out vec2 frag_tex_coord;

void main() {
  frag_tex_coord = tex_coord;
  gl_Position = mvp_matrix * vec4(vertex, 1.0);
}
