#version 120

attribute vec4 v_position;
attribute vec4 v_normal;
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec4 v_cam_pos;
varying vec4 v_light_pos;

void main()
{
	vec4 N = normalize( v_normal );
	vec4 L = normalize( v_light_pos - v_position );
	float NdotL = max( dot( N, L ), 0 );
	vec4 Diffuse =  NdotL;
	gl_FragColor = Diffuse;
}