#version 430

layout(location=0) in vec3 position;
layout(location=1) in vec2 textureCoord;
layout(location=2) in vec3 normal;

uniform mat4 projectionView, model;
uniform vec3 camPosition;

out vec4 inWorldPosition;
out vec2 inTextureCoord;
out vec3 inNormal;
out vec3 inViewVector;

out gl_PerVertex
{
    vec4 gl_Position;
};

void main(void) {
    inWorldPosition = model * vec4(position, 1.0);
    gl_Position = projectionView * inWorldPosition;

    inTextureCoord = textureCoord;
    inTextureCoord.t = 1.0 - textureCoord.t;

    inNormal = mat3(model) * normal;

    inViewVector = normalize(camPosition - inWorldPosition.xyz);
}