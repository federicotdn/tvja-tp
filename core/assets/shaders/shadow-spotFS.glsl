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
	vec4 position_w = u_model_mat * v_position;

	vec4 light_direction_surface = normalize(u_light_position - position_w);

	float cutoff_multiplier = 1.0;

	if ((dot(light_direction_surface, u_light_direction)) < u_cutoff_angle)
	{
		cutoff_multiplier = 0.0001;
	} 
	else 
	{
		//float bias = 0.005*tan(acos(dot(normal_w, u_light_direction)));
		//bias = clamp(bias, 0,0.01);
		float bias = 0.0005 * tan(acos(clamp(dot(normal_w, u_light_direction), 0, 1)));
		bias = clamp (bias, 0, 0.0005);
        float visibility = 1.0;

		for (int i=0; i < 9; i++)
		{
        	if (unpack(texture2D( u_shadow_map, ((v_shadow_coord.xy/v_shadow_coord.w + poissonDisk[i]/700.0)) ))  <  (v_shadow_coord.z - bias)/v_shadow_coord.w)
        	{
        		visibility -= 0.1;
			}
        }

      	cutoff_multiplier *= visibility;
	}

	/* DIFFUSE */
	vec4 diffuse_component = get_diffuse_component(normal_w, u_light_direction, tex_color, u_light_color);

	/* SPECULAR */
	vec4 specular_component = get_specular_component(normal_w, u_light_direction, tex_color, u_light_color, u_shininess, u_model_mat * v_position, u_cam_pos);

	gl_FragColor = vec4(diffuse_component.xyz * cutoff_multiplier + specular_component.xyz * cutoff_multiplier, 1);
}