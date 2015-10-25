const vec4 packFactors = vec4( 256.0 * 256.0 * 256.0,256.0 * 256.0,256.0,1.0);
const vec4 bitMask     = vec4(0.0,1.0/256.0,1.0/256.0,1.0/256.0);

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

float unpack(vec4 packedZValue)
{
	const vec4 unpackFactors = vec4( 1.0 / (256.0 * 256.0 * 256.0), 1.0 / (256.0 * 256.0), 1.0 / 256.0, 1.0 );
	return dot(packedZValue, unpackFactors);
}

vec4 pack(vec4 position) {
    float normalizedDistance  = position.z / position.w;
    normalizedDistance = (normalizedDistance + 1.0) / 2.0;

    vec4 packedValue = vec4(fract(packFactors*normalizedDistance));
    packedValue -= packedValue.xxyz * bitMask;

    return packedValue;
}
