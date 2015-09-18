#version 120

uniform sampler2D u_shadow_map;

varying vec4 v_shadow_coord;

void main()
{
	float visibility = 1.0;
	if (texture2D(u_shadow_map, v_shadow_coord.xy).z < v_shadow_coord.z) {
		visibility = 0.5;
	}
	gl_FragColor = vec4(visibility, visibility, visibility, 1);
}