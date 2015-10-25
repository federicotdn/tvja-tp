#version 120

uniform sampler2D u_texture;

varying vec2 v_texCoords;

//%include shaders/utils.glsl

void main()
{
    float z = unpack(texture2D(u_texture, v_texCoords));
    gl_FragColor = texture2D(u_texture, v_texCoords);
}