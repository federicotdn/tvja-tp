#version 120

uniform vec4 u_light_position;
uniform vec4 u_light_color;

uniform sampler2D u_texture;
uniform vec4 u_cam_pos;
uniform int u_shininess;

uniform mat4 u_model_mat;
uniform mat4 u_model_rotation_mat;

varying vec4 v_position;
varying vec4 v_normal_w;
varying vec2 v_texCoords;

//%include shaders/utils.glsl

void main()
{
	vec4 tex_color = texture2D(u_texture, v_texCoords);
	vec4 position_w = u_model_mat * v_position;

	vec4 light_direction = normalize(u_light_position - position_w);
	
	/* DIFFUSE */
	vec4 diffuse_component = get_diffuse_component(v_normal_w, light_direction, tex_color, u_light_color);

	/* SPECULAR */
	vec4 specular_component = get_specular_component(v_normal_w, light_direction, tex_color, u_light_color, u_shininess, position_w, u_cam_pos);
	
	gl_FragColor = vec4(diffuse_component.xyz + specular_component.xyz, 1);
}