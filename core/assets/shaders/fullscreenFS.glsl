#version 120

uniform sampler2D u_texture;

varying vec2 v_texCoords;

//%include shaders/utils.glsl

void main()
{
    float z = unpack(texture2D(u_texture, v_texCoords));
    if (z > 1) {

    }
    gl_FragColor = vec4(z,z,z,1);
}