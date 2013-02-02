#version 330

in vec2 position;
in vec2 texcoord;

out vec2 texcoord2;

uniform mat3 trans;
uniform float depth;
uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;

void main() {
    vec3 trans2 = trans * vec3(texcoord, 1.0);
    texcoord2 = vec2(trans2.x, trans2.y);
    gl_Position = proj * view * model * vec4(position, 1.0, 1.0);
    gl_Position.z = -depth;
}
