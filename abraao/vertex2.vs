#version 330

layout (location =0) in vec4 position;
layout (location =1) in vec3 inColour;
layout (location =2) in vec2 texCoord;

out vec2 outTexCoord;
out vec3 exColour;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

void main()
{
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position.xyz, 1.0);
    exColour = inColour;
    
    outTexCoord = texCoord;
}