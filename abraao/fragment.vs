#version 330

in  vec4 outTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;

void main()
{	
	vec2 top;
	
	if(outTexCoord.w == 1) {
		top = vec2((fract(outTexCoord.x + outTexCoord.z)/2), (1 - fract(outTexCoord.y))/2 );//fract transforma as coordenadas de N para 1 ou 0
	} else if(outTexCoord.w == 2) {
		top = vec2((fract(outTexCoord.x)/2),  (fract(outTexCoord.y + outTexCoord.z)/2) );//bottom top mesmo dos sides
	} else {//
	
		top = vec2((fract(outTexCoord.x)/2),  (fract(outTexCoord.y + outTexCoord.z)/2) );
	}
	
	/*
		top = vec2((fract(outTexCoord.x + outTexCoord.z)/2), (1 - fract(outTexCoord.y))/2 );//fract transforma as coordenadas de N para 1 ou 0
		explicando o codigo acima.
		O frag shader eh chamado para cada pixel na tela, ou seja, cada ponto da tela tem que ter um ponto correspondente na textura.
		com as coordenadas passadas pelo vertex shader, ficamos as aber qual o ponto no model que esta a ser redenrizado com isso podemos 
		calcular qual deveria ser a posicao na texture que deve ser desenhada.
		Como a posicao do model estava vindo invertida no y, tive que que inverter por isso a subtração, se y for 1 então é zero, ser for 0 então é 1 
	*/

//	top = vec2((fract(outTexCoord.x)/2),  (fract(outTexCoord.y + outTexCoord.z)/2) );
    fragColor = texture(texture_sampler, top);
    
}