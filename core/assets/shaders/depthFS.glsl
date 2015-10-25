#version 120

varying vec4 v_position;

//%include shaders/utils.glsl

void main() {
    gl_FragColor = ((v_position/v_position.w + 1)/2).zzzz;
}