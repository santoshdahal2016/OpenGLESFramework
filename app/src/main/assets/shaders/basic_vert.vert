#version 300 es

in vec3 vPosition;
in vec2 textureCords;


uniform mat4 transformationMatrix;

out vec2 pass_textureCoords;

void main() {

gl_Position =  transformationMatrix * vec4(vPosition,1.0);

    pass_textureCoords =  textureCords;


}