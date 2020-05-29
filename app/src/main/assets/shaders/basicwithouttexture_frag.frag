#version 300 es

precision mediump float;

out vec4 colorOut;

in vec3 oPosition;

void main() {
        colorOut = vec4(1.0f*oPosition.x ,1.0f*oPosition.y ,1.0f,1.0f);


}
