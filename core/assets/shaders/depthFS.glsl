#version 120

varying vec4 v_final_pos;

//%include shaders/utils.glsl

void main() {
    gl_FragColor = pack(v_final_pos);
}