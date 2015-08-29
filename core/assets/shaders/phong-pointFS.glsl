#version 120

uniform vec4 u_light_position;
uniform vec4 u_light_color;
uniform sampler2D u_texture;

varying vec4 v_normal;
varying vec2 v_texCoords;

void main()
{
	vec4 tex_color = texture2D(u_texture, v_texCoords);
	vec4 after_light = (dot(v_normal, u_light_direction)) * tex_color; 
	gl_FragColor = after_light * u_light_color;
}