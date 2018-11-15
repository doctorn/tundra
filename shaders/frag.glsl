#version 130

#define NUM_LIGHTS 16

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

uniform Light lights[NUM_LIGHTS];
uniform vec3 ambient;
uniform float alpha;

out vec4 colour;

uniform sampler2D tex;

void main() {
  // Temporarily use normal like this so it isn't optimised away

  if (texturing) {
    float value = 0;
    for(int i = 0; i < NUM_LIGHTS; i++) {
        value *= lights[i].constant;
    }
    colour = texture(tex, tex_start + frag_tex_coord * tex_size) *length(frag_normal);
    if (colour.a != 1.0)
      discard;
  } else {
    colour = vec4(1., 1., 1., 1.); 
  }


  vec3 c;
  if (texturing) {
    c = vec3(texture(tex, tex_start + frag_tex_coord * tex_size));
  } else {
    c = vec3(1.);
  }

  vec3 tempColour = ambient;

  for(int i = 0; i < NUM_LIGHTS; i++) {
    Light light = lights[i];

    vec3 N = normalize(frag_normal);
    vec3 L = normalize(light.pos - frag_pos);
    vec3 R = 2 * dot(L, N) * N - L;
    vec3 V = normalize(cam_pos - frag_pos);

    float distance = length(light.pos - frag_pos);
    float attentuation = 1.0 / (light.constant + light.linear * distance +
                                     light.quadratic * (distance * distance));

    vec3 diff = light.col * max((dot(N, L)), 0.0) * c;
    vec3 spec = light.col * pow(max(dot(V, R), 0.0), alpha)  * c;

    tempColour += attentuation * (diff + spec);
  }

  colour = vec4(tempColour, 0.);


}
