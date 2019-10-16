#version 430

in vec4 inWorldPosition;
in vec2 inTextureCoord;
in vec3 inNormal;
in vec3 inViewVector;

uniform mat4 projection, model, view;
uniform vec3 camPosition;

uniform samplerCube prefilteredMap;
uniform samplerCube irradianceMap;
uniform sampler2D brdfSample;

uniform sampler2D albedoSample;
uniform sampler2D metalnessSample;
uniform sampler2D roughnessSample;
uniform sampler2D normalSample;

vec3 albedo = texture(albedoSample, inTextureCoord).rgb;
float metalness = texture(metalnessSample, inTextureCoord).r;
float roughness = 0.6f;
vec3 normal = texture(normalSample, inTextureCoord).rgb;

const vec3 lightDirection = vec3(40.2, 40.0, 4.0);
#define PI 3.1415926535897932384626433832795

out vec4 out_Color;

float D_GGX(float dotNH, float roughness)
{
	float alpha = roughness * roughness;
	float alpha2 = alpha * alpha;
	float denom = dotNH * dotNH * (alpha2 - 1.0) + 1.0;
	return (alpha2)/(PI * denom*denom);
}

float G_SchlicksmithGGX(float dotNL, float dotNV, float roughness)
{
	float r = (roughness + 1.0);
	float k = (r*r) / 8.0;
	float GL = dotNL / (dotNL * (1.0 - k) + k);
	float GV = dotNV / (dotNV * (1.0 - k) + k);
	return GL * GV;
}

vec3 F_Schlick(float cosTheta, vec3 F0)
{
	return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);

}

vec3 F_SchlickR(float cosTheta, vec3 F0, float roughness)
{
	return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);

}

vec4 prefilteredReflection(vec3 R, float roughness)
{
	const float MAX_REFLECTION_LOD = 5.0;
	float lod = roughness * MAX_REFLECTION_LOD;
	float lodf = floor(lod);
	float lodc = ceil(lod);
	vec4 a = textureLod(prefilteredMap, R, lodf);
	vec4 b = textureLod(prefilteredMap, R, lodc);
	return mix(a, b, lod - lodf);
}

vec3 specularContribution(vec3 L, vec3 V, vec3 N, vec3 F0, float metalness, float roughness)
{
	vec3 H = normalize (V + L);
	float dotNH = clamp(dot(N, H), 0.0, 1.0);
	float dotNV = clamp(dot(N, V), 0.0, 1.0);
	float dotNL = clamp(dot(N, L), 0.0, 1.0);

	vec3 lightColor = vec3(1.0);

	vec3 color = vec3(0.0);

	if (dotNL > 0.0) {
		float D = D_GGX(dotNH, roughness);
		float G = G_SchlicksmithGGX(dotNL, dotNV, roughness);
		vec3 F = F_Schlick(dotNV, F0);
		vec3 spec = D * F * G / (4.0 * dotNL * dotNV + 0.001);
		vec3 kD = (vec3(1.0) - F) * (1.0 - metalness);
		color += (kD * albedo / PI + spec) * dotNL;
	}

	return color;
}

vec3 perturbNormal()
{
	vec3 tangentNormal = normal * 2.0 - 1.0;

	vec3 q1 = dFdx(inWorldPosition.xyz);
	vec3 q2 = dFdy(inWorldPosition.xyz);
	vec2 st1 = dFdx(inTextureCoord);
	vec2 st2 = dFdy(inTextureCoord);

	vec3 N = normalize(inNormal);
	vec3 T = normalize(q1 * st2.t - q2 * st1.t);
	vec3 B = -normalize(cross(N, T));
	mat3 TBN = mat3(T, B, N);

	return normalize(TBN * tangentNormal);
}

void main(void) {

    roughness = roughness - 0.2;

    vec3 N = perturbNormal();
    vec3 V = inViewVector;
    vec3 R = reflect(-V, N);

    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, metalness);

    vec3 Lo = vec3(0.0);
    vec3 L = normalize(lightDirection.xyz - inWorldPosition.xyz);
    //Lo += specularContribution(L, V, N, F0, metalness, roughness);

    vec2 brdf = texture(brdfSample, vec2(max(dot(N, V), 0.0), roughness)).rg;
    vec3 reflection = prefilteredReflection(R, roughness).rgb;
    vec3 irradiance = texture(irradianceMap, N).rgb;

    vec3 diffuse = albedo * irradiance;

    vec3 F = F_SchlickR(max(dot(N, V), 0.0), F0, roughness);

    vec3 spec = reflection * (F * brdf.r + brdf.g);

    vec3 kD = 1.0 - F;
    kD *= 1.0 - metalness;
    vec3 ambient = (kD * diffuse + spec);

    vec3 color = ambient + Lo;

    out_Color =  vec4(color,1.0);

}
