#version 120

uniform vec4 u_light_direction;
uniform vec4 u_light_color;

uniform sampler2D u_texture;
uniform vec4 u_cam_pos;
uniform int u_shininess;

uniform mat4 u_model_mat;

varying vec4 v_position;
varying vec4 v_normal_w;
varying vec2 v_texCoords;

//%include shaders/utils.glsl

void main()
{
	vec4 tex_color = texture2D(u_texture, v_texCoords);

	/* DIFFUSE */
	vec4 diffuse_component = get_diffuse_component(v_normal_w, u_light_direction, tex_color, u_light_color);

	/* SPECULAR */
	vec4 specular_component = get_specular_component(v_normal_w, u_light_direction, tex_color, u_light_color, u_shininess, u_model_mat * v_position, u_cam_pos);

	gl_FragColor = vec4(diffuse_component.xyz + specular_component.xyz, 1);
}