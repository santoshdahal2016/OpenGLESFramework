#version 300 es

precision mediump float;

out vec4 colorOut;

in vec3 oPosition;

void main() {
        colorOut = vec4(oPosition.x+0.6f ,oPosition.y*1.0f ,0.3f,1.0f);


}
