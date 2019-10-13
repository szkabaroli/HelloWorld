#version 120

varying vec2 inUV;

void main(void) {
    vec4 position = gl_Vertex;
    gl_Position = position;
    inUV = gl_MultiTexCoord0.st;
}