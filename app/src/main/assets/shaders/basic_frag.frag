#version 300 es

precision mediump float;

out vec4 colorOut;

in vec2 pass_textureCoords;

uniform sampler2D textureSampler;


void main() {
//    colorOut = vec4(1.0f ,1.0f ,0,1.0f);

//colorOut = vec4(1.0f*pass_textureCoords.x,1.0f*pass_textureCoords.y,1,1.0f);
colorOut = texture(textureSampler,pass_textureCoords)+vec4(1.0f*pass_textureCoords.x,1.0f*pass_textureCoords.y,1,1.0f);

}
