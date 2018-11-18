#version 130

#define MAX_LIGHTS 64

in vec3 frag_normal;
in vec3 frag_pos;
in vec2 frag_tex_coord;
in vec3 frag_pos_light_space;

uniform vec3 cam_pos;

uniform bool texturing;
uniform vec2 tex_start;
uniform vec2 tex_size;

struct Light {
    vec3 pos;
    vec3 col;

    float constant;
    float linear;
    float quadratic;

    bool on;
    bool shadow_mapped;
};

uniform bool lighting;
uniform Light lights[MAX_LIGHTS];

uniform bool shadow_mapping;
uniform sampler2D depth_map;

uniform int light_count;
uniform vec3 ambient;
uniform float alpha;

out vec4 colour;

uniform sampler2D tex;

float shadow(vec3 normal, vec3 light_dir) {
  vec3 proj = frag_pos_light_space * 0.5 + 0.5;
  if (proj.z > 1.)
    return 0.;
  float current = proj.z;
  float bias = 0.005 * tan(acos(dot(normal, light_dir)));
  bias = clamp(bias, 0, 0.01);
  float shadow = 0.0;
  vec2 size = 1.0 / textureSize(depth_map, 0);
  for(int i = -1; i < 1; i++) {
      for(int j = -1; j < 1; j++) {
          float depth = texture(depth_map, proj.xy + vec2(i, j) * size).r;
          shadow += current - bias > depth ? 1.0 : 0.0;
      }
  }
  return shadow / 9.;
}

void main() {
  if (texturing) {
    colour = texture(tex, tex_start + frag_tex_coord * tex_size);
    if (colour.a != 1.0)
      discard;
  } else {
    colour = vec4(1.);
  }

  if (lighting) {
    vec3 temp = ambient;
    for(int i = 0; i < light_count; i++) {
      Light light = lights[i];
      if (light.on) {
        vec3 N = normalize(frag_normal);
        vec3 L = normalize(light.pos - frag_pos);
        vec3 V = normalize(cam_pos - frag_pos);
        vec3 H = normalize(L + V);

        float distance = length(light.pos - frag_pos);
        float attentuation = 1.0 / (light.constant + light.linear * distance +
                                         light.quadratic * (distance * distance));

        vec3 diff = light.col * max((dot(N, L)), 0.0) * vec3(colour);
        vec3 spec = light.col * pow(max(dot(N, H), 0.0), alpha) * vec3(colour);

        float shadow_scalar = 1.;
        if (light.shadow_mapped)
          shadow_scalar = 1. - shadow(N, L);
        temp += attentuation * (diff + spec) * shadow_scalar;
      }
    }

    float gamma = 2.2;
    colour = vec4(pow(temp, vec3(1. / gamma)), 1.);
  }
}
