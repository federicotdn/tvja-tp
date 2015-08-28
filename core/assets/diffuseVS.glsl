#version 120

attribute vec4 a_position;
attribute vec4 a_normal;
attribute vec2 a_texCoord0;
attribute vec4 a_cam_pos;
attribute vec4 a_light_pos;

uniform mat4 u_mvp;

varying vec4 v_position;
varying vec4 v_normal;
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec4 v_cam_pos;
varying vec4 v_light_pos;

void main()
{
    v_color = vec4(1, 1, 1, 1);
    v_texCoords = a_texCoord0;
    v_cam_pos = a_cam_pos;
    v_light_pos = a_light_pos;
    
    v_position = a_position;
    v_normal = a_normal;
    
    gl_Position =  u_mvp * a_position;
}

