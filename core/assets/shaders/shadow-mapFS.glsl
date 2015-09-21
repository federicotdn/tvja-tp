#version 120

uniform sampler2D u_shadow_map;
uniform float u_view_far;
uniform mat4 u_model_mat;
uniform vec4 u_light_pos;

varying vec4 v_shadow_coord;
varying vec4 v_position;

void main()
{
	vec4 final_pos = u_model_mat * v_position;
	float visibility = 0.5;
	float f_distance = length(final_pos.xyz - u_light_pos.xyz) / u_view_far;

	vec3 depth = (v_shadow_coord.xyz / v_shadow_coord.w)*0.5+0.5;

	if (texture2D(u_shadow_map, depth.xy).z > f_distance - 0.005) {
		gl_FragColor = vec4(0.2, 0.2, 0.2, 1);
	} else {
		gl_FragColor = vec4(0, 0, 0, 1);
	}
}