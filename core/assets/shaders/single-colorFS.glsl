#version 120

uniform vec4 u_ambient_color;

void main() {
    gl_FragColor = u_ambient_color;
}