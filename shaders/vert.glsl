#version 130

in vec3 vertex;
in vec3 normal;
in vec2 tex_coord;

uniform mat4 mvp_matrix;
uniform mat4 model_matrix;
uniform mat4 model_matrix_inverse;

out vec3 frag_normal;
out vec3 frag_pos;
out vec2 frag_tex_coord;

void main() {
  gl_Position = mvp_matrix * vec4(vertex, 1.0);
  frag_tex_coord = tex_coord;
  frag_normal = mat3(model_matrix_inverse) * normal;
  vec4 frag_pos_hom = model_matrix * vec4(vertex, 1.0);
  frag_pos = vec3(frag_pos_hom) / frag_pos_hom.w;
}
