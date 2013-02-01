#version 430

in vec2 position;
in vec2 texcoord;

out vec2 texcoord2;

uniform mat3 textureTransform;
uniform float depth;
uniform mat4 model;
uniform mat4 view;

void main() {
    texcoord2 = textureTransform * vec3(texcoord, 1.0);
    gl_Position = view * model * vec4(position, -depth, 1.0);
}
