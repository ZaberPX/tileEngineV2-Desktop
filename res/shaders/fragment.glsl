#version 430

in vec2 texcoord2;

out vec4 outColor;

uniform sampler2D tex;
uniform vec4 tint;

void main() {
    outColor = tint * texture(tex, texcoord2);
}
