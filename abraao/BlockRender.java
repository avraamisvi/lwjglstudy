package abraao;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class BlockRender implements Render {
	
	float[] vertices = new float[] {
			// V0
            -0.5f, 0.5f, 0.5f,
            // V1
            -0.5f, -0.5f, 0.5f,
            // V2
            0.5f, -0.5f, 0.5f,
            // V3
            0.5f, 0.5f, 0.5f,
            // V4
            -0.5f, 0.5f, -0.5f,
            // V5
            0.5f, 0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,
            
            // For text coords in top face
            // V8: V4 repeated
            -0.5f, 0.5f, -0.5f,
            // V9: V5 repeated
            0.5f, 0.5f, -0.5f,
            // V10: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V11: V3 repeated
            0.5f, 0.5f, 0.5f,

            // For text coords in right face
            // V12: V3 repeated
            0.5f, 0.5f, 0.5f,
            // V13: V2 repeated
            0.5f, -0.5f, 0.5f,

            // For text coords in left face
            // V14: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V15: V1 repeated
            -0.5f, -0.5f, 0.5f,

            // For text coords in bottom face
            // V16: V6 repeated
            -0.5f, -0.5f, -0.5f,
            // V17: V7 repeated
            0.5f, -0.5f, -0.5f,
            // V18: V1 repeated
            -0.5f, -0.5f, 0.5f,
            // V19: V2 repeated
            0.5f, -0.5f, 0.5f,
	};
	
	int[] indices = new int[]{//usando indices reduz o tamanho da memoria usada clockwise
        // Front face
        0, 1, 3, 3, 1, 2,
        // Top Face
        8, 10, 11, 9, 8, 11,
        // Right face
        12, 13, 7, 5, 12, 7,
        // Left face
        14, 15, 6, 4, 14, 6,
        // Bottom face
        16, 18, 19, 17, 16, 19,
        // Back face
        4, 6, 7, 5, 4, 7,
	};

	private int vaoId;

	private int vertexCount = indices.length;

	float[] colours = new float[]{
		    0.5f, 0.0f, 0.0f,
		    0.0f, 0.5f, 0.0f,
		    0.0f, 0.0f, 0.5f,
		    0.0f, 0.5f, 0.5f,
		    0.5f, 0.0f, 0.0f,
		    0.0f, 0.5f, 0.0f,
		    0.0f, 0.0f, 0.5f,
		    0.0f, 0.5f, 0.5f,
	};
	
	
	float[] textCoords = new float[]{
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,
            
            0.0f, 0.0f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            
            // For text coords in top face
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.0f, 1.0f,
            0.5f, 1.0f,

            // For text coords in right face
            0.0f, 0.0f,
            0.0f, 0.5f,

            // For text coords in left face
            0.5f, 0.0f,
            0.5f, 0.5f,

            // For text coords in bottom face
            0.5f, 0.0f,
            1.0f, 0.0f,
            0.5f, 0.5f,
            1.0f, 0.5f,
	};
	
	Texture texture;

	private ShaderProgram shaderProg;

	private int texture_sampler;
	
	public BlockRender(ShaderProgram shaderProg) {
		
		this.shaderProg = shaderProg;
		
		try {
			texture = new Texture("/abraao/grassblock.png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		texture_sampler = glGetUniformLocation(shaderProg.getProgramId(), "texture_sampler");
		
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
		
		//########## Criando os indices
		
		int idxVboId = glGenBuffers();
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices).flip();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);		
		
		
		// ########### criando cores
		
		int colourVboId = glGenBuffers();
		FloatBuffer colourBuffer = BufferUtils.createFloatBuffer(colours.length);
		colourBuffer.put(colours).flip();
		glBindBuffer(GL_ARRAY_BUFFER, colourVboId);
		glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);		
		

		// ########### VBO de texturas
		
		vboId = glGenBuffers();
		FloatBuffer textCoordsBuffer = BufferUtils.createFloatBuffer(textCoords.length);
		textCoordsBuffer.put(textCoords).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);		
		
		//liberando tudo
		glBindBuffer(GL_ARRAY_BUFFER, 0);//libero o vbo
		glBindVertexArray(0);//libero o vao
	}
	
	public void initRender() {
	    glBindVertexArray(vaoId);//vao que sera usado para desenhar
	    glEnableVertexAttribArray(0);//posicao do vbo que sera usado
	    glEnableVertexAttribArray(1);//posicao do vbo que sera usado
	    glEnableVertexAttribArray(2);//posicao do vbo que sera usado
	    
	    //TEXTURE
	    
	 // Activate first texture unit
	    glActiveTexture(GL_TEXTURE0);
	    // Bind the texture
	    glBindTexture(GL_TEXTURE_2D, texture.getId());
	}
	
	public void endRender() {
		glDisableVertexAttribArray(2);//desconecto o vbo
		glDisableVertexAttribArray(1);//desconecto o vbo
	    glDisableVertexAttribArray(0);//desconecto o vbo
	    glBindVertexArray(0);//deconecto o vao
	}
	
	public int getVaoId() {
		return vaoId;
	}

	public void render() {
		initRender();
		
		glUniform1i(texture_sampler, 0);
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		/*
		 * The parameters of that method are:
			mode: Specifies the primitives for rendering, triangles in this case. No changes here.
			count: Specifies the number of elements to be rendered.
			type: Specifies the type of value in the indices data. In this case we are using integers.
			indices: Specifies the offset to apply to the indices data to start rendering.
		 */
		
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
