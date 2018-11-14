#version 130

in vec2 frag_texcoord;

out vec4 colour;

uniform sampler2D tex;

void main() {
  colour = texture(tex, frag_texcoord);
}
