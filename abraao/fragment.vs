#version 330

in  vec4 outTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform mat4 modelViewMatrix;
uniform mat4 modelMatrix;

const vec4 fogcolor = vec4(0.6, 0.8, 1.0, 1.0);
const float fogdensity = .00013;

int T_GRASS_FRONT  = 1;
int T_GRASS_BACK   = 2;
int T_GRASS_LEFT   = 3;
int T_GRASS_RIGHT  = 4;
int T_GRASS_TOP    = 5;
int T_GRASS_BOTTOM = 6;

vec3 light_position = vec3(0,0,0);
vec3 light_intensities = vec3(1,1,1);

vec2 getTextCoord(vec4 outTexCoord) {
	
	vec2 ret;
	
	if(outTexCoord.w == T_GRASS_FRONT || outTexCoord.w == T_GRASS_LEFT || outTexCoord.w == T_GRASS_RIGHT || outTexCoord.w == T_GRASS_BACK || outTexCoord.w == T_GRASS_FRONT) {
		
		ret = vec2((fract(outTexCoord.x + outTexCoord.z)/2), (1 - fract(outTexCoord.y))/2 );//fract transforma as coordenadas de N para 1 ou 0
	
	} else if(outTexCoord.w == T_GRASS_TOP) {
		ret = vec2((fract(outTexCoord.x)/2),  (fract(outTexCoord.y + outTexCoord.z) + 1)/2 );//bottom top mesmo dos sides
	
	} else if(outTexCoord.w == T_GRASS_BOTTOM) {
	
		ret = vec2( (fract(outTexCoord.x) + 1) / 2,  (fract(outTexCoord.y + outTexCoord.z)/2) );
	}
	
	return ret;

}

vec3 vn1 = vec3(0.000000, -1.000000, 0.000000);//bottom
vec3 vn2 = vec3(0.000000, 1.000000, 0.000000);//top

vec3 vn3 = vec3(1.000000, 0.000000, 0.000000);//right
vec3 vn5 = vec3(-1.000000, 0.000000, 0.000000);//left

vec3 vn4 = vec3(0.000000, 0.000000, 1.000000);//front
vec3 vn6 = vec3(0.000000, 0.000000, -1.000000);//back

vec3 getSideNormal() {

	if(outTexCoord.w == T_GRASS_FRONT) {
		return vn4;
	} else if(outTexCoord.w == T_GRASS_LEFT) {
		return vn5;
	} else if(outTexCoord.w == T_GRASS_RIGHT) {
		return vn3;
	} else if(outTexCoord.w == T_GRASS_BACK) {
		return vn6;		
	} else if(outTexCoord.w == T_GRASS_TOP) {
		return vn2;
	} else {//if(outTexCoord.w == T_GRASS_BOTTOM)
		return vn1;
	}	
}


vec4 calculateLight(vec4 color, vec2 fragTexCoord) {

	vec3 normal = normalize(transpose(inverse(mat3(modelMatrix))) * getSideNormal());
	vec3 surfacePos = vec3(modelMatrix * vec4(outTexCoord.xyz, 1));
	vec4 surfaceColor = texture(texture_sampler, fragTexCoord);
	vec3 surfaceToLight = normalize(light_position - surfacePos);

	float diffuseCoefficient = max(0.0, dot(normal, surfaceToLight));
	vec3 diffuse = diffuseCoefficient * surfaceColor.rgb * light_intensities;
	
	return vec4(diffuse, surfaceColor.a);
}

void main()
{	
	vec4 color;
	
	float z = gl_FragCoord.z / gl_FragCoord.w;
	float fog = clamp(exp(-fogdensity * z * z), 0.2, 1);
	
	vec2 textCoord = getTextCoord(outTexCoord);
    //color = texture(texture_sampler, textCoord);
    
    color = calculateLight(color, textCoord);
    
    fragColor = mix(fogcolor, color, fog);
}


/*
	top = vec2((fract(outTexCoord.x + outTexCoord.z)/2), (1 - fract(outTexCoord.y))/2 );//fract transforma as coordenadas de N para 1 ou 0
	explicando o codigo acima.
	O frag shader eh chamado para cada pixel na tela, ou seja, cada ponto da tela tem que ter um ponto correspondente na textura.
	com as coordenadas passadas pelo vertex shader, ficamos as aber qual o ponto no model que esta a ser redenrizado com isso podemos 
	calcular qual deveria ser a posicao na texture que deve ser desenhada.
	Como a posicao do model estava vindo invertida no y, tive que que inverter por isso a subtração, se y for 1 então é zero, ser for 0 então é 1 
*/


/**
vec4 finalColor;
	
	//calculate normal in world coordinates
	mat3 normalMatrix = transpose(inverse(mat3(modelMatrix)));
	vec3 normal = normalize(normalMatrix * getSideNormal());	
	
	//calculate the location of this fragment (pixel) in world coordinates
    vec3 fragPosition = vec3(modelViewMatrix * vec4(outTexCoord.xyz, 1));
	
    //calculate the vector from this pixels surface to the light source
    vec3 surfaceToLight = light_position - fragPosition;
    
    //calculate the cosine of the angle of incidence
    float brightness = dot(normal, surfaceToLight) / (length(surfaceToLight) * length(normal));
    brightness = clamp(brightness, 0, 1);
    
    
	//calculate final color of the pixel, based on:
    // 1. The angle of incidence: brightness
    // 2. The color/intensities of the light: light.intensities
    // 3. The texture and texture coord: texture(tex, fragTexCoord)
    vec4 surfaceColor = texture(texture_sampler, fragTexCoord);
    finalColor = vec4(brightness * light_intensities * surfaceColor.rgb, surfaceColor.a);    
    
	return finalColor;
*/