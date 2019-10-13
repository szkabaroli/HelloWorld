#version 430

in vec3 position;
out vec3 textureCoords;

uniform mat4 projectionView;

void main(void){
	gl_Position = projectionView * vec4(position, 1.0);
	textureCoords = position;
}