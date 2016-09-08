package abraao;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class TriangleRender implements Render {
	
	float[] vertices = new float[]{
			0.0f,  0.5f, 0.0f,
		    -0.5f, -0.5f, 0.0f,
		     0.5f, -0.5f, 0.0f
		  };

	private int vaoId;

	private int vertexCount = 3;
	
	
	public TriangleRender() {
		
		create();
	}
	
	public void create() {
		  
		//A VBO is just a memory buffer stored in the graphics card memory that stores vertices. 
		//A VAO is an object that contains one or more VBOs which are usually called attribute lists. 
		//Each attribute list can hold one type of data: position, colour, texture, etc. You are free to store whichever you want in each slot.
		
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
		verticesBuffer.put(vertices).flip();//crio esse buffer com os vertices para passar para o vbo
		
		vaoId = glGenVertexArrays();//crio o VAO
		glBindVertexArray(vaoId);//conecto o vao que vou modificar
		int vboId = glGenBuffers();//crio o buffer da vbo que vou configurar
		glBindBuffer(GL_ARRAY_BUFFER, vboId);//connecto o vbo que vou configurar o mesmo anterior
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);//configuro os vertices no vbo
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);//defino qual o atributo do vao que recebera esse vbo
		/*
		 	index: Specifies the location where the shader expects this data.
			size: Specifies then number of components per vertex attribute (from 1 to 4). In this case, we are passing 3D coordinates, so it should be 3.
			type: Specifies the type of each component in the array, in this case a float.
			normalized: Specifies if the values should be normalized or not.
			stride: Specifies the byte offset between consecutive generic vertex attributes. (We will explain it later).
			offset: Specifies a offset of the first component of the first component in the array in the data store of the buffer.
		 */
		
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);//libero o vbo
		glBindVertexArray(0);//libero o vao
	}
	
	public void initRender() {
	    glBindVertexArray(vaoId);//vao que sera usado para desenhar
	    glEnableVertexAttribArray(0);//posicao do vbo que sera usado
	}
	
	public void endRender() {
	    glDisableVertexAttribArray(0);//desconecto o vbo
	    glBindVertexArray(0);//deconecto o vao
	}
	
	public int getVaoId() {
		return vaoId;
	}

	public void render() {
		initRender();
		
//		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		glDrawArrays(GL_TRIANGLES, 0, 3);
		
		endRender();
	}

	private int getVertexCount() {
		return vertexCount;
	}

	@Override
	public void update(WorldMatrix wordl, Camera camera) {
		// TODO Auto-generated method stub
		
	}
}
