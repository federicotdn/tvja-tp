vec4 get_diffuse_component(vec4 normal, vec4 light_direction, vec4 texture_color, vec4 light_color)
{
	vec4 after_light = max(0, (dot(normal, normalize(light_direction)))) * texture_color;
	return after_light * light_color;
}

vec4 get_specular_component(vec4 normal, vec4 light_direction, vec4 texture_color, vec4 light_color, int shininess, vec4 vertex, vec4 cam_pos)
{
	vec4 v_vec = normalize(cam_pos - vertex);
	vec4 r_vec = reflect(-normalize(light_direction), normal);

	vec4 after_light_spec = max(0, pow(dot(r_vec, v_vec), shininess)) * texture_color;
	return after_light_spec * light_color;
}