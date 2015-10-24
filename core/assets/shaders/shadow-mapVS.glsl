#version 120

attribute vec4 a_position;
attribute vec4 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_mvp;
uniform mat4 u_light_bias_mvp;

varying vec4 v_shadow_coord;
varying vec4 v_position;

void main()
{
    gl_Position =  u_mvp * a_position;
    v_shadow_coord = u_light_bias_mvp * a_position;
    v_position = a_position;
}