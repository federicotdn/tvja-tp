#version 120

uniform vec4 u_light_position;
uniform vec4 u_light_color;

uniform sampler2D u_texture;
uniform vec4 u_cam_pos;
uniform int u_shininess;

uniform mat4 u_model_mat;
uniform mat4 u_model_rotation_mat;

uniform samplerCube u_shadow_map;

varying vec4 v_shadow_coord;
varying vec4 v_position;
varying vec4 v_normal;
varying vec2 v_texCoords;
varying vec4 v_final_pos;

//%include shaders/utils.glsl

float VectorToDepthValue(vec3 Vec)
{
    vec3 AbsVec = abs(Vec);
    float LocalZcomp = max(AbsVec.x, max(AbsVec.y, AbsVec.z));

    const float f = 100.0;
    const float n = 0.01;
    float NormZComp = (f+n) / (f-n) - (2*f*n)/(f-n)/LocalZcomp;
    return (NormZComp + 1.0) * 0.5;
}

void main()
{
	vec4 tex_color = texture2D(u_texture, v_texCoords);
	vec4 normal_w = normalize(u_model_rotation_mat * v_normal);
	vec4 position_w = u_model_mat * v_position;

	vec4 light_direction = normalize(u_light_position - position_w);
	
	/* DIFFUSE */
	vec4 diffuse_component = get_diffuse_component(normal_w, light_direction, tex_color, u_light_color);

	/* SPECULAR */
	vec4 specular_component = get_specular_component(normal_w, light_direction, tex_color, u_light_color, u_shininess, position_w, u_cam_pos);

	float bias = 0.000005;
	float visibility = 1.0;

	float shadowDepth = unpack(textureCube(u_shadow_map, v_shadow_coord.xyz/v_shadow_coord.w));
	float testDepth   = VectorToDepthValue((u_light_position - v_final_pos).xyz);

	 if (shadowDepth < testDepth)
			visibility = 0.5;

	gl_FragColor = vec4(diffuse_component.xyz * visibility + specular_component.xyz * visibility, 1);
}