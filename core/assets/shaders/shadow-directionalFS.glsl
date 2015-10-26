#version 120

uniform vec4 u_light_direction;
uniform vec4 u_light_color;

uniform sampler2D u_texture;
uniform vec4 u_cam_pos;
uniform int u_shininess;

uniform mat4 u_model_mat;
uniform mat4 u_model_rotation_mat;

uniform sampler2D u_shadow_map;

varying vec4 v_position;
varying vec4 v_normal;
varying vec2 v_texCoords;
varying vec4 v_shadow_coord;

//%include shaders/utils.glsl

void main()
{
	vec4 tex_color = texture2D(u_texture, v_texCoords);
	vec4 normal_w = normalize(u_model_rotation_mat * v_normal);

	/* DIFFUSE */
	vec4 diffuse_component = get_diffuse_component(normal_w, u_light_direction, tex_color, u_light_color);

	/* SPECULAR */
	vec4 specular_component = get_specular_component(normal_w, u_light_direction, tex_color, u_light_color, u_shininess, u_model_mat * v_position, u_cam_pos);

	float bias = 0.0005 * tan(acos(clamp(dot(normal_w, u_light_direction), 0, 1)));
	bias = clamp(bias, 0, 0.0005);
    float visibility = 1.0;

	for (int i=0; i < 9; i++)
	{
    	if (unpack(texture2D(u_shadow_map, ((v_shadow_coord.xy/v_shadow_coord.w + poissonDisk[i]/700.0)))) < (v_shadow_coord.z - bias)/v_shadow_coord.w)
    	{
    		visibility -= 0.1;
		}
    }

	gl_FragColor = vec4(diffuse_component.xyz * visibility + specular_component.xyz * visibility, 1);
}