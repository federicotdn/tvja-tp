#version 120

uniform sampler2D u_texture;

varying vec2 v_texCoords;

void main()
{
    vec4 tex_color = texture2D(u_texture, v_texCoords);
}