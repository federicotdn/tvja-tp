#version 120

uniform vec4 u_light_position;
uniform vec4 u_light_direction;
uniform float u_cutoff_angle;
uniform int u_shininess;

uniform vec4 u_light_color;

uniform sampler2D u_texture;
uniform vec4 u_cam_pos;

uniform mat4 u_model_mat;
uniform mat4 u_model_rotation_mat;

varying vec4 v_position;
varying vec4 v_normal;
varying vec2 v_texCoords;

//%include shaders/utils.glsl

void main()
{
	vec4 tex_color = texture2D(u_texture, v_texCoords);
	vec4 normal_w = normalize(u_model_rotation_mat * v_normal);
	vec4 position_w = u_model_mat * v_position;

	vec4 light_direction_surface = normalize(u_light_position - position_w);

	float cutoff_multiplier = 1.0;
	if ((dot(light_direction_surface, u_light_direction)) < u_cutoff_angle) {
		cutoff_multiplier = 0.0001;
	}
	
	/* DIFFUSE */
	vec4 diffuse_component = get_diffuse_component(normal_w, u_light_direction, tex_color, u_light_color);

	/* SPECULAR */
	vec4 specular_component = get_specular_component(normal_w, u_light_direction, tex_color, u_light_color, u_shininess, u_model_mat * v_position, u_cam_pos);

	gl_FragColor = vec4(diffuse_component.xyz * cutoff_multiplier + specular_component.xyz * cutoff_multiplier, 1);
}