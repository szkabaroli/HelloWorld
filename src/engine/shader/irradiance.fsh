#version 430

in vec3 inPosition;
out vec4 out_Color;

uniform samplerCube samplerEnv;

const float PI = 3.1415926535897932384626433832795;
const float deltaPhi = (2.0 * PI)/ 180.0;
const float deltaTheta = (0.5 * PI) / 64.0;

void main(void) {
	vec3 N = normalize(inPosition);
	vec3 up = vec3(0.0, 1.0, 0.0);
	vec3 right = normalize(cross(up, N));
	up = cross(N, right);

	const float TWO_PI = PI * 2.0;
	const float HALF_PI = PI * 0.5;

	vec3 color = vec3(0.0);
	uint sampleCount = 2000;
	for (float phi = 0.0; phi < TWO_PI; phi += deltaPhi) {
		for (float theta = 0.0; theta < HALF_PI; theta += deltaTheta) {
			vec3 tempVec = cos(phi) * right + sin(phi) * up;
			vec3 sampleVector = cos(theta) * N + sin(theta) * tempVec;
			color += texture(samplerEnv, sampleVector).rgb * cos(theta) * sin(theta);
			sampleCount++;
		}
	}
	out_Color = vec4(PI * color / float(sampleCount), 1.0);
}