#version 120

uniform vec4 u_light_direction;
uniform vec4 u_light_color;
uniform vec4 u_ambient_color;

uniform sampler2D u_texture;
uniform vec4 u_cam_pos;

uniform mat4 u_model_mat;
uniform mat4 u_model_rotation_mat;

varying vec4 v_position;
varying vec4 v_normal;
varying vec2 v_texCoords;

void main()
{
	vec4 tex_color = texture2D(u_texture, v_texCoords);
	vec4 normal_w = normalize(u_model_rotation_mat * v_normal);

	/* DIFFUSE */
	vec4 after_light = max(0, (dot(normal_w, normalize(u_light_direction)))) * tex_color;
	vec4 diffuse_component = after_light * u_light_color;

	/* AMBIENT */
	vec4 ambient_component = u_ambient_color;

	/* SPECULAR */
	vec4 world_pos = u_model_mat * v_position;
	vec4 v_vec = normalize(u_cam_pos - world_pos);
	vec4 r_vec = reflect(-normalize(u_light_direction), normal_w);

	vec4 after_light_spec = max(0, pow(dot(r_vec, v_vec), 3)) * tex_color;
	vec4 specular_component = after_light_spec * u_light_color;
	
	vec4 final_color = vec4(ambient_component.xyz + diffuse_component.xyz + specular_component.xyz, 1);

	gl_FragColor = final_color;
}