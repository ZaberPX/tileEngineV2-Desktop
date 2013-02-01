#version 430

in vec2 position;

out vec2 texcoord2;

uniform vec2 texcoord;
uniform float depth;
uniform mat4 model;
uniform mat4 view;

void main() {
    texcoord2 = texcoord;
    gl_Position = view * model * vec4(position, -depth, 1.0);
}
