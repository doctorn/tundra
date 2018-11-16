#version 130

#define MAX_LIGHTS 128

in vec3 frag_normal;
in vec3 frag_pos;
in vec2 frag_tex_coord;

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
};

uniform bool lighting;
uniform Light lights[MAX_LIGHTS];
uniform int light_count;
uniform vec3 ambient;
uniform float alpha;

out vec4 colour;

uniform sampler2D tex;

void main() {
  if (texturing) {
    colour = vec4(texture(tex, tex_start + frag_tex_coord * tex_size));
    if (colour.a != 1.0)
      discard;
  } else {
    colour = vec4(1.);
  }

  if (lighting) {
    vec3 temp = ambient;
    for(int i = 0; i < light_count; i++) {
      Light light = lights[i];

      vec3 N = normalize(frag_normal);
      vec3 L = normalize(light.pos - frag_pos);
      vec3 R = 2 * dot(L, N) * N - L;
      vec3 V = normalize(cam_pos - frag_pos);

      float distance = length(light.pos - frag_pos);
      float attentuation = 1.0 / (light.constant + light.linear * distance +
                                       light.quadratic * (distance * distance));

      vec3 diff = light.col * max((dot(N, L)), 0.0) * vec3(colour);
      vec3 spec = light.col * pow(max(dot(V, R), 0.0), alpha) * vec3(colour);

      temp += attentuation * (diff + spec);
    }

    colour = vec4(temp, 0.);
  }
}
