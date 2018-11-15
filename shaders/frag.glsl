#version 130

in vec3 frag_normal;
in vec2 frag_tex_coord;

out vec4 colour;

uniform sampler2D tex;

void main() {
  // Temporarily use normal like this so it isn't optimised away
  colour = texture(tex, frag_tex_coord) + vec4(frag_normal, 0);
  colour -= vec4(frag_normal, 0);
}
