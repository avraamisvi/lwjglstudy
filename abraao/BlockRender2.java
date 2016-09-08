package abraao;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class BlockRender2 implements Render {
	
	int CX = 32;
	int CY = 32;
	int CZ = 32;
	
	float[] vertices;
	
	int[] indices;

	private int vaoId;

	private int vertexCount;

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
	
	public BlockRender2(ShaderProgram shaderProg) {
		
		this.shaderProg = shaderProg;
		
		try {
			texture = new Texture("/abraao/grassblock.png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		texture_sampler = glGetUniformLocation(shaderProg.getProgramId(), "texture_sampler");
		
		criarVertices();
		create();
		
		System.out.println("TOTAL:" + this.vertices.length/36);
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
//		
//		int idxVboId = glGenBuffers();
//		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
//		indicesBuffer.put(indices).flip();
//		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
//		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);		
		
		
		// ########### criando cores
		
		int colourVboId = glGenBuffers();
		FloatBuffer colourBuffer = BufferUtils.createFloatBuffer(colours.length);
		colourBuffer.put(colours).flip();
		glBindBuffer(GL_ARRAY_BUFFER, colourVboId);
		glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);		
		

		// ########### VBO de texturas
		
//		textCoords = vertices;
		
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
	
	public void criarVertices() {
		
//		  byte4 vertex[CX * CY * CZ * 6 * 6];
		  List<Vector3f> verts = new ArrayList<>();
		  
		  for(int x = 0; x < CX; x++) {
		    for(int y = 0; y < CY; y++) {
		      for(int z = 0; z < CZ; z++) {

		        // View from negative x
		    	verts.add(new Vector3f(x,     y,     z));        
		        verts.add(new Vector3f(x,     y,     z + 1));        
		        verts.add(new Vector3f(x,     y + 1, z));        
		        verts.add(new Vector3f(x,     y + 1, z));        
		        verts.add(new Vector3f(x,     y,     z + 1));        
		        verts.add(new Vector3f(x,     y + 1, z + 1));        
                          
		        // View from positive x
		        verts.add(new Vector3f(x + 1, y,     z));        
		        verts.add(new Vector3f(x + 1, y + 1, z));        
		        verts.add(new Vector3f(x + 1, y,     z + 1));        
		        verts.add(new Vector3f(x + 1, y + 1, z));        
		        verts.add(new Vector3f(x + 1, y + 1, z + 1));        
		        verts.add(new Vector3f(x + 1, y    , z + 1));        

		        // Repeat for +y, -y, +z, and -z directions      
                
		        // View from negative y
		    	verts.add(new Vector3f(x,      y,  z));
		        verts.add(new Vector3f(x + 1,  y,  z));        
		        verts.add(new Vector3f(x,      y,  z + 1));        
		        verts.add(new Vector3f(x + 1,  y,  z));        
		        verts.add(new Vector3f(x + 1,  y,  z + 1));        
		        verts.add(new Vector3f(x,      y,  z + 1));  	        
		        
		          
		        // View from positive y
		        verts.add(new Vector3f(x, 	  	y + 1,   z));        
		        verts.add(new Vector3f(x, 		y + 1, 	 z + 1));        
		        verts.add(new Vector3f(x + 1, 	y + 1,   z));        
		        verts.add(new Vector3f(x + 1, 	y + 1, 	 z));        
		        verts.add(new Vector3f(x, 		y + 1, 	 z + 1));        
		        verts.add(new Vector3f(x + 1, 	y + 1, 	 z + 1));
		        
		        // View from negative z
		    	verts.add(new Vector3f(x,     	y,  z));
		        verts.add(new Vector3f(x,     	y,  z));        
		        verts.add(new Vector3f(x + 1,   y,  z));        
		        verts.add(new Vector3f(x,   	y,  z));        
		        verts.add(new Vector3f(x + 1,   y,  z));        
		        verts.add(new Vector3f(x + 1,   y, 	z));  	
		        
		        // View from positive z
		        verts.add(new Vector3f(x, 	  y,     z + 1));        
		        verts.add(new Vector3f(x + 1, y, 	 z + 1));        
		        verts.add(new Vector3f(x, 	  y + 1, z + 1));        
		        verts.add(new Vector3f(x, 	  y + 1, z + 1));        
		        verts.add(new Vector3f(x + 1, y, 	 z + 1));        
		        verts.add(new Vector3f(x + 1, y + 1, z + 1));      		        

		      }
		    }
		  }
		
		  this.vertices = new float[verts.size()*3];
		  int counter = 0;
		  
		  textCoords = new float[verts.size()*72];
		  int textCoordsCount = 0;
		  
		  for(int i = 0; i < verts.size(); i++) {
			  this.vertices[counter++] = verts.get(i).x;
			  this.vertices[counter++] = verts.get(i).y;
			  this.vertices[counter++] = verts.get(i).z;
			                                 
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.5f;			  
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.0f;		            
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.5f;            
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.5f;			            
				textCoords[textCoordsCount++] = 1.0f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 1.0f;textCoords[textCoordsCount++] =  0.0f;		            
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;            
				textCoords[textCoordsCount++] = 1.0f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  1.0f;				  
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  1.0f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  1.0f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;		  
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.5f;			  
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.0f;	
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.5f;			  
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.0f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
				textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.0f;			            

		  }
		  
//		  textCoords = new float[]{
//				  	
//				  // ------------- lado 12
//					0.0f, 0.5f,			  
//		            0.5f, 0.5f,
//		            0.0f, 0.0f,
//		            
//		            0.0f, 0.0f,
//		            0.5f, 0.5f,
//		            0.5f, 0.0f,
//		            
//		           //---------- lado 24
//		            
//		            0.5f, 0.5f,
//		            0.5f, 0.0f,		            
//		            0.0f, 0.5f,            
//
//		            0.5f, 0.0f,
//		            0.0f, 0.0f,
//		            0.0f, 0.5f,			            
//		            
//		          //---------- bottom 36     
//		            
//		            1.0f, 0.5f,
//		            1.0f, 0.0f,		            
//		            0.5f, 0.5f,            
//
//		            1.0f, 0.0f,
//		            0.5f, 0.0f,
//		            0.5f, 0.5f,
//		            
//			      //----------  topo  48
//		            
//					0.0f, 1.0f,				  
//		            0.5f, 1.0f,
//		            0.0f, 0.5f,
//		            
//		            0.0f, 0.5f,
//		            0.5f, 1.0f,
//		            0.5f, 0.5f,		  
//		            
//				  // ------------- lado 60
//					0.0f, 0.5f,			  
//		            0.5f, 0.5f,
//		            0.0f, 0.0f,
//		            
//		            0.0f, 0.0f,
//		            0.5f, 0.5f,
//		            0.5f, 0.0f,	
//		            
//		           //---------- lado 72
//		            
//					0.0f, 0.5f,			  
//		            0.5f, 0.5f,
//		            0.0f, 0.0f,
//		            
//		            0.0f, 0.0f,
//		            0.5f, 0.5f,
//		            0.5f, 0.0f,			            
//		            
//		  };
		  
		  System.out.println(textCoordsCount);
		  vertexCount = this.vertices.length/3;
		
	}
	
	public void initRender() {
	    glBindVertexArray(vaoId);//VAO que sera usado para desenhar
	    glEnableVertexAttribArray(0);//posicao do VBO que sera usado
	    glEnableVertexAttribArray(1);//posicao do vbo que sera usado
	    glEnableVertexAttribArray(2);//posicao do vbo que sera usado

	    glEnable(GL_CULL_FACE);
	    
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
	    
	    glDisable(GL_CULL_FACE);
	}
	
	public int getVaoId() {
		return vaoId;
	}

	public void render() {
		initRender();
		
		glUniform1i(texture_sampler, 0);
//		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		/*
		 * The parameters of that method are:
			mode: Specifies the primitives for rendering, triangles in this case. No changes here.
			count: Specifies the number of elements to be rendered.
			type: Specifies the type of value in the indices data. In this case we are using integers.
			indices: Specifies the offset to apply to the indices data to start rendering.
		 */
		
		glDrawArrays(GL_TRIANGLES, 0, getVertexCount());
		
		endRender();
	}

	private int getVertexCount() {
		return vertexCount;
	}

	@Override
	public void update(WorldMatrix worldMatrix, Camera camera) {
		float rot = 0f;
		Vector3f offset = new Vector3f(0,0,0);
		Vector3f rotation = new Vector3f(rot,rot,rot);
		float scale = 1;
		worldMatrix.update(offset, rotation, scale);
		worldMatrix.updateCamera(camera);
	}
	
}
