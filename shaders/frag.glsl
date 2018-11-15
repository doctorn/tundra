#version 130

in vec3 frag_normal;
in vec2 frag_tex_coord;

uniform bool texturing;
uniform vec2 tex_start;
uniform vec2 tex_size;

out vec4 colour;

uniform sampler2D tex;

void main() {
  // Temporarily use normal like this so it isn't optimised away
  if (texturing) {
    colour = texture(tex, tex_start + frag_tex_coord * tex_size) + vec4(frag_normal, 0);
    colour -= vec4(frag_normal, 0);
    if (colour.a != 1.0)
      discard;
  } else {
    colour = vec4(1., 1., 1., 1.); 
  }
}
