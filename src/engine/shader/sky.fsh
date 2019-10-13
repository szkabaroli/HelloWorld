#version 430

in vec3 textureCoords;
out vec4 out_Color;

uniform samplerCube samplerEnv;

void main(void){

	vec3 color = texture(samplerEnv, textureCoords).rgb;

    out_Color = vec4(color, 1.0);
}