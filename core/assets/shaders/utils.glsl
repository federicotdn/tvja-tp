const vec4 packFactors = vec4(1 ,255.0, 255.0 * 255.0, 255.0 * 255.0 * 255.0);
const vec4 bitMask     = vec4(1.0/255.0,1.0/255.0,1.0/255.0,0.0);

const vec2 poissonDisk[9] = vec2[](
	vec2(0.95581, -0.18159), vec2(0.50147, -0.35807), vec2(0.69607, 0.35559),
	vec2(-0.0036825, -0.59150),	vec2(0.15930, 0.089750), vec2(-0.65031, 0.058189),
	vec2(0.11915, 0.78449),	vec2(-0.34296, 0.51575), vec2(-0.60380, -0.41527)
);

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
	const vec4 unpackFactors = vec4(1, 1/255.0, 1/(255.0 * 255.0), 1/(255.0 * 255.0 * 255.0));
	return dot(packedZValue, unpackFactors);
}

vec4 pack(vec4 position) {
    float normalizedDistance  = position.z / position.w;
    normalizedDistance = (normalizedDistance + 1.0) / 2.0;

    vec4 packedValue = vec4(fract(packFactors*normalizedDistance));
    packedValue -= packedValue.yzww * bitMask;

    return packedValue;
}
