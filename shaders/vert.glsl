#version 130

in vec3 vertex;
in vec3 normal;
in vec2 tex_coord;

uniform mat4 mvp_matrix;

out vec3 frag_normal;
out vec2 frag_tex_coord;

void main() {
  gl_Position = mvp_matrix * vec4(vertex, 1.0);
  frag_tex_coord = tex_coord;
  frag_normal = normal;
}
