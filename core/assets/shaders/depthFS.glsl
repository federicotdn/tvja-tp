#version 120

varying vec4 v_final_pos;

void main() {
    gl_FragColor = vec4(v_final_pos.zzz * 0.2, 1);
}