#version 120

uniform float u_view_far;
uniform vec4 u_cam_pos;
uniform mat4 u_model_mat;

varying vec4 v_position;

void main() {
	vec4 final_pos = u_model_mat * v_position;
    gl_FragColor = vec4(length(final_pos.xyz - u_cam_pos.xyz) / u_view_far);
}