#version 130

in vec2 frag_tex_coord;

uniform bool texturing;
uniform vec2 tex_start;
uniform vec2 tex_size;

uniform sampler2D tex;

void main() {
  if (texturing) {
    vec4 colour = texture(tex, tex_start + frag_tex_coord * tex_size);
    if (colour.a != 1.0)
      discard;
  }
}
