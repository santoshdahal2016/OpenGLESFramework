#version 300 es

in vec3 vPosition;


uniform mat4 transformationMatrix;

out vec3 oPosition;

void main() {

    gl_Position =  transformationMatrix * vec4(vPosition,1.0);

    oPosition = vPosition;


}