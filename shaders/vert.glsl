#version 130

#define MAX_LIGHTS 64

in vec3 vertex;
in vec3 tangent;
in vec3 normal;
in vec2 tex_coord;
in int material;

uniform mat4 mvp_matrix;
uniform mat4 model_matrix;
uniform mat4 model_matrix_inverse;

uniform bool shadow_mapping;
uniform mat4 shadow_vp_matrix;

uniform bool materialed;

out vec3 frag_normal;
out vec3 frag_pos;
out vec2 frag_tex_coord;
out vec3 frag_pos_light_space;
out mat3 tbn_matrix;
flat out int frag_material;

void main() {
  gl_Position = mvp_matrix * vec4(vertex, 1.0);
  frag_material = material;
  frag_tex_coord = tex_coord;
  frag_normal = mat3(model_matrix_inverse) * normal;
  vec4 frag_pos_hom = model_matrix * vec4(vertex, 1.0);
  frag_pos = vec3(frag_pos_hom) / frag_pos_hom.w;

  if (shadow_mapping) {
      vec4 frag_pos_light_space_hom = shadow_vp_matrix * model_matrix * vec4(vertex, 1.0);
      frag_pos_light_space = vec3(frag_pos_light_space_hom) / frag_pos_light_space_hom.w;
  }

  if (materialed) {
    vec3 corrected_normal = normalize(normal);
    vec3 corrected_tangent = 
      normalize(normalize(tangent) 
          - dot(normalize(tangent), corrected_normal) * corrected_normal);
    vec3 corrected_bitangent = normalize(cross(corrected_normal, corrected_tangent));
    tbn_matrix = mat3(corrected_tangent, corrected_bitangent, corrected_normal);
  }
}
