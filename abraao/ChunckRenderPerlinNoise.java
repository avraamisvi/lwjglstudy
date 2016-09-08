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
import java.util.Random;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import com.flowpowered.noise.model.Plane;
import com.flowpowered.noise.module.source.Perlin;

import abraao.terrains.FractalTerrain;

public class ChunckRenderPerlinNoise implements Render {
	
	int CX = 72;
	int CY = 72;
	int CZ = 72;
	
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
	
	public ChunckRenderPerlinNoise(ShaderProgram shaderProg) {
		
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
		
		System.out.println("TOTAL posicoes:" + this.vertices.length);
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
		
		  OpenSimplexNoise openNoise = new OpenSimplexNoise(231321);
		  SimplexNoise noise = new SimplexNoise();
		
//		  byte4 vertex[CX * CY * CZ * 6 * 6];
//		  List<Vector3f> verts = new ArrayList<>();
		  		
//		 double[][] noiseMap = new double[72][72];
		 
		    SimplexNoise simplexNoise=new SimplexNoise();
		
		    double xStart=0;
		    double XEnd=CX;
		    double yStart=0;
		    double yEnd=CZ;
		
		    int xResolution=CX;
		    int yResolution=CZ;
		
		    double[][] result=new double[xResolution][yResolution];
		
//		    for(int i=0;i<xResolution;i++){
//		        for(int j=0;j<yResolution;j++){
//		            int x=(int)(xStart+i*((XEnd-xStart)/xResolution));
//		            int y=(int)(yStart+j*((yEnd-yStart)/yResolution));
//		            //result[i][j]=0.5*(1+simplexNoise.noise(x,y));
//		            result[i][j] = Noise.gradientCoherentNoise3D(x, arg1, arg2, 100, arg4);
//		        }
//		    }
		 
		 List<Block> blocks = new ArrayList<>();
		 Block[][][] blocks_array = new Block[CX][CY][CZ];
		 
		  for(int x = 0; x < CX; x++) {
		    for(int y = 0; y < CY; y++) {
		      for(int z = 0; z < CZ; z++) {
		    	
				
				Block block = new Block();
				block.y = y;
				
				blocks_array[x][y][z] = block;
				blocks.add(block);
				
		        // View from negative x
		    	block.verts.add(new Vector3f(x,     y,     z));        
		        block.verts.add(new Vector3f(x,     y,     z + 1));        
		        block.verts.add(new Vector3f(x,     y + 1, z));        
		        block.verts.add(new Vector3f(x,     y + 1, z));        
		        block.verts.add(new Vector3f(x,     y,     z + 1));        
		        block.verts.add(new Vector3f(x,     y + 1, z + 1));        
                          
		        // View from positive x
		        block.verts.add(new Vector3f(x + 1, y,     z));        
		        block.verts.add(new Vector3f(x + 1, y + 1, z));        
		        block.verts.add(new Vector3f(x + 1, y,     z + 1));        
		        block.verts.add(new Vector3f(x + 1, y + 1, z));        
		        block.verts.add(new Vector3f(x + 1, y + 1, z + 1));        
		        block.verts.add(new Vector3f(x + 1, y    , z + 1));        

		        // Repeat for +y, -y, +z, and -z directions      
                
		        // View from negative y
		    	block.verts.add(new Vector3f(x,      y,  z));
		        block.verts.add(new Vector3f(x + 1,  y,  z));        
		        block.verts.add(new Vector3f(x,      y,  z + 1));        
		        block.verts.add(new Vector3f(x + 1,  y,  z));        
		        block.verts.add(new Vector3f(x + 1,  y,  z + 1));        
		        block.verts.add(new Vector3f(x,      y,  z + 1));  	        
		        
		          
		        // View from positive y
		        block.verts.add(new Vector3f(x, 	  	y + 1,   z));        
		        block.verts.add(new Vector3f(x, 		y + 1, 	 z + 1));        
		        block.verts.add(new Vector3f(x + 1, 	y + 1,   z));        
		        block.verts.add(new Vector3f(x + 1, 	y + 1, 	 z));        
		        block.verts.add(new Vector3f(x, 		y + 1, 	 z + 1));        
		        block.verts.add(new Vector3f(x + 1, 	y + 1, 	 z + 1));
		        
		        // View from negative z
		    	block.verts.add(new Vector3f(x,     	y, 	    z));
		        block.verts.add(new Vector3f(x,     	y + 1,  z));        
		        block.verts.add(new Vector3f(x + 1,   y,  	z));        
		        block.verts.add(new Vector3f(x,   	y + 1,  z));        
		        block.verts.add(new Vector3f(x + 1,   y + 1,  z));        
		        block.verts.add(new Vector3f(x + 1,   y, 		z));  	
		        
		        // View from positive z
		        block.verts.add(new Vector3f(x, 	  y,     z + 1));        
		        block.verts.add(new Vector3f(x + 1, y, 	 z + 1));        
		        block.verts.add(new Vector3f(x, 	  y + 1, z + 1));        
		        block.verts.add(new Vector3f(x, 	  y + 1, z + 1));        
		        block.verts.add(new Vector3f(x + 1, y, 	 z + 1));        
		        block.verts.add(new Vector3f(x + 1, y + 1, z + 1));      		        

		      }
		    }
		  }
		
		  System.out.println("TOTAL cubos:"    + blocks.size());
		  
		  //remover cubos
//		  Random rand = new Random();
		  
		  Perlin perlin = new Perlin();//works
		  
		  for(int z = 0; z < CZ; z++) {
		  	for(int x = 0; x < CX; x++) {
		    	
		  		double nx = (double)x/(double)CX- 0.5, nz = (double)z/(double)CZ- 0.5;
		    	double height = perlin.getValue(nx, nz, 0);
	            
		    	height = Math.min((height  / 2 + 0.5) * 71, 71);
		    	
		    	for (int y = 0; y <= height; y++) {
		    		blocks_array[x][y][z].active = true;
	            }				
				
		  	}
		  }
		  
		  //USANDO MID
//		  MidpointDisplacement mid = new MidpointDisplacement();
//		  mid.n = 7;
//		  int[][] midMap = mid.getMap();
//		  int maxh = 0;
//		  for(int x = 0; x < CX; x++) {
//		    for(int z = 0; z < CZ; z++) {
//		    	
//		    	int height = midMap[x][z];
//		    	
//		    	if(maxh < height)
//		    		maxh = height;
//		    	
//	            for (int y = 0; y <= height; y++) {
//	            	blocks_array[x][y][z].active = true;
//	            }				
//				
//		  	}
//		  }
////			
//		  System.out.println(maxh);
//		  criarMontanhas(blocks_array);
		  
//			int lod = 9;//level of detail
//			double roughness = .5;
			
//			FractalTerrain frac = new FractalTerrain(lod , roughness);
//			  int maxh = 0;
//			  for(int x = 0; x < CX; x++) {
//			    for(int z = 0; z < CZ; z++) {
//			    	
//			    	int height = 0;
//			    	
//			    	try {
//				    	height = (int) (frac.getAltitude(x, z)*71);
//				    	
//			    	} catch(Exception e) {
//			    		System.out.println("error");
//			    	}
//				    	
//				    	if(maxh < height)
//				    		maxh = height;
//				    	
//			            for (int y = 0; y <= height; y++) {
//			            	blocks_array[x][y][z].active = true;
//			            }
//				
//		  	}
//		  }
//		  	  double roughness = .4;
//			  double exaggeration = .7;
//			  int lod = 5;
//			  int steps = 71;//1 << lod;
//			  
//			  FractalTerrain terrain = new FractalTerrain(lod , roughness);
//			  for (int i = 0; i <= steps; ++ i) {
//			    for (int j = 0; j <= steps; ++ j) {
//			      double x = 1.0 * i / steps, z = 1.0 * j / steps;
//			      double altitude = terrain.getAltitude (x, z) * 71;
////			      map[i][j] = new Triple (x, altitude * exaggeration, z);       
////			      colors[i][j] = terrain.getColor (x, z);
//					if (maxh < altitude )
//						maxh = (int) altitude;
//	
//					for (int y = 0; y <= altitude ; y++) {
//						blocks_array[i][y][j].active = true;
//					}
//			    }
//			  }			  
//			  
//			  
//			  System.out.println(maxh);
			  
			//fim modelando terrain
		  
		  this.vertices = new float[blocks.size()*36*3];
		  int counter = 0;
		  
		  textCoords = new float[blocks.size()*72];
		  textCoordsCount = 0;
		  countAddTextCoord = 0;
		  
		  for(Block b : blocks) {
		  
		  	if(!b.active)
		  		continue;
		  		
			for(int i = 0; i < b.verts.size(); i++) {
			  this.vertices[counter++] = b.verts.get(i).x;
			  this.vertices[counter++] = b.verts.get(i).y;
			  this.vertices[counter++] = b.verts.get(i).z;
			  
			  adicionarTextCoords();
		  }
		  }
		  
		  System.out.println("textCoordsCount:" + textCoordsCount);
		  vertexCount = this.vertices.length/3;
		
	}
	
	int textCoordsCount = 0;
	int countAddTextCoord = 0;
	public void adicionarTextCoords() {
		  countAddTextCoord++;
		  if(countAddTextCoord == 36) {
				 
			countAddTextCoord = 0;
			  
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
			
			textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
			textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.0f;		            
			textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.5f;            
			textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.0f;
			textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.0f;
			textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.5f;		
			
			textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.5f;			  
			textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
			textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.0f;
			textCoords[textCoordsCount++] = 0.0f;textCoords[textCoordsCount++] =  0.0f;
			textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.5f;
			textCoords[textCoordsCount++] = 0.5f;textCoords[textCoordsCount++] =  0.0f;			            
		  }		
	}
	
	public void initRender() {
	    glBindVertexArray(vaoId);//VAO que sera usado para desenhar
	    glEnableVertexAttribArray(0);//posicao do VBO que sera usado
	    glEnableVertexAttribArray(1);//posicao do vbo que sera usado
	    glEnableVertexAttribArray(2);//posicao do vbo que sera usado

	    glEnable(GL_CULL_FACE);
	    
	    //TEXTURE
	 // Activate first texture unit
	    glActiveTexture(GL_TEXTURE0); //aqui especifico o lugar na memoria onde a textura sera guardada, no caso unidade 0
	    // Bind the texture
	    glBindTexture(GL_TEXTURE_2D, texture.getId());//aqui guardo a textura na memoria
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
		
		glUniform1i(texture_sampler, 0);//informo q a textura deve ser buscada na unidade 0
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
	
	class Block {
		 int y;
		 List<Vector3f> verts = new ArrayList<>();
		 boolean active = false;
	}
	
	public void criarMontanhas(Block blocks[][][]) {
		   
		  int count = 0;
		  
		  for(int x = 0; x < CX; x++) {
			  for(int z = 0; z < CZ; z++) {
		    	
		    	for(int y = 0; y < CY; y++) {
					if(z >= count)
						blocks[x][y][z].active = true;
					count++;				
				}
		    	count = 0;
		  	}
		  	
		  	
		  }
	}
}
