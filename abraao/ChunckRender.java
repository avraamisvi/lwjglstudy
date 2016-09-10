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
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import com.flowpowered.noise.Noise;
import com.flowpowered.noise.NoiseQuality;
import com.flowpowered.noise.model.Plane;
import com.flowpowered.noise.module.source.Perlin;

import abraao.terrains.FractalTerrain;

public class ChunckRender implements Render {
	

	private static final int MAPZ = 256;
	private static final int MAPX = 256;
	int CX = 64;
	int CY = 32;
	int CZ = 64;
	
	 List<Block> blocks = new ArrayList<>();
	 Block[][][] blocks_array = new Block[CX][CY][CZ];
	 
	float[][] map = new float[256][256];
	 
	Vector4f[] vertex = new Vector4f[(CX*CY*CZ) * 5];//vezes 5, ver metodo de gerar vertices
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
	
	public ChunckRender(ShaderProgram shaderProg) {
		
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
		
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);//defino qual o atributo do vao que recebera esse vbo
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
		
//		vboId = glGenBuffers();
//		FloatBuffer textCoordsBuffer = BufferUtils.createFloatBuffer(textCoords.length);
//		textCoordsBuffer.put(textCoords).flip();
//		glBindBuffer(GL_ARRAY_BUFFER, vboId);
//		glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
//		glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);		
		
		//liberando tudo
		glBindBuffer(GL_ARRAY_BUFFER, 0);//libero o vbo
		glBindVertexArray(0);//libero o vao
	}
	
	public void generateMap() {
		  Perlin perlin = new Perlin();//works
		
		  for(int z = 0; z < MAPZ; z++) {
			  	for(int x = 0; x < MAPX; x++) {
			    	
			  		double nx = (double)x/(double)CX- 0.5, nz = (double)z/(double)CZ- 0.5;
			    	double height = perlin.getValue(nx, nz, 0);
			    	map[x][z] = (float) Math.min((height  / 2 + 0.5) * (CY-1), CY-1);
			  	}
			  }		
	}
	
	public void definirBlocosVisiveis() {
		
		  generateMap();
		
//		  Perlin perlin = new Perlin();//works
		  
		  for(int z = 0; z < CZ; z++) {//posso usar o teste de null ao inves de ter q instanciar todos os blocos
			  	for(int x = 0; x < CX; x++) {
			  		for(int y = 0; y < CY; y++) {
						Block block = new Block();
						block.y = y;
						blocks_array[x][y][z] = block;
						blocks.add(block);				  		
				  	}	
			  	}
		  }//TODO otimizar geracao
		  
		  
		  for(int z = 0; z < CZ; z++) {
		  	for(int x = 0; x < CX; x++) {
		    	
		    	double height = map[x+100][z+30];
		    	
		    	for (int y = 0; y <= height; y++) {
		    		blocks_array[x][y][z].active = true;
	            }				
				
		  	}
		  }		  
		  
		  //DESATIVAR OS ESCONDIDOS
		  List<Block> desativados = new ArrayList<>();
		  for(int z = 0; z < CZ; z++) {
			  	for(int x = 0; x < CX; x++) {
			    	for (int y = 0; y < CY; y++) {
			    		if(isHidden(x, y, z)) {
			    			desativados.add(blocks_array[x][y][z]);
			    		}
		            }				
			  	}
		  }
		  for(Block b : desativados) {
			  b.blocked = true;
		  }		
	}
	
	boolean isblocked(int x1, int y1, int z1, int x2, int y2, int z2) {
		
		return blocks_array[x1][y1][z1].blocked || !blocks_array[x1][y1][z1].active;
		
//		List<Block> blocks, Block[][][] blocks_array
		
		// Invisible blocks are always "blocked"
//		if(!blocks_array[x1][y1][z1])
//			return true;

		// Leaves do not block any other block, including themselves
//		if(transparent[get(x2, y2, z2)] == 1)
//			return false;

		// Non-transparent blocks always block line of sight
//		if(!transparent[get(x2, y2, z2)])
//			return true;

		// Otherwise, LOS is only blocked by blocks if the same transparency type
//		return transparent[get(x2, y2, z2)] == transparent[blk[x1][y1][z1]];
	}	
	
	public void criarVertices() {
		
		 //DEFINE OS BLOCOS E O MAPA
		 definirBlocosVisiveis();
		 
		 	int i = 0;
			int merged = 0;
			boolean vis = false;;
			int max = 0;
			
			for(int x = CX - 1; x >= 0; x--) {
				for(int y = 0; y < CY; y++) {
					for(int z = 0; z < CZ; z++) {
						
						max++;
						
						// Line of sight blocked?
						if(isblocked(x, y, z, x - 1, y, z)) {
							vis = false;
							continue;
						}

						int side = blocks_array[x][y][z].front;

						// Same block as previous one? Extend it.
						if(vis && z != 0 && blocks_array[x][y][z].type == blocks_array[x][y][z - 1].type) {
							vertex[i - 5] = new Vector4f(x, y, z + 1, side);
							vertex[i - 2] = new Vector4f(x, y, z + 1, side);
							vertex[i - 1] = new Vector4f(x, y + 1, z + 1, side);
							merged++;
						// Otherwise, add a new quad.
						} else {
							vertex[i++] = new Vector4f(x, y, z, side);
							vertex[i++] = new Vector4f(x, y, z + 1, side);
							vertex[i++] = new Vector4f(x, y + 1, z, side);
							vertex[i++] = new Vector4f(x, y + 1, z, side);
							vertex[i++] = new Vector4f(x, y, z + 1, side);
							vertex[i++] = new Vector4f(x, y + 1, z + 1, side);
						}
						
						vis = true;
					}
				}
			}

			// View from positive x

			for(int x = 0; x < CX; x++) {
				for(int y = 0; y < CY; y++) {
					for(int z = 0; z < CZ; z++) {
						
						max++;
						
						if(isblocked(x, y, z, x + 1, y, z)) {
							vis = false;
							continue;
						}

						int side = blocks_array[x][y][z].back;

						if(vis && z != 0 && blocks_array[x][y][z].type == blocks_array[x][y][z - 1].type) {
							vertex[i - 4] = new Vector4f(x + 1, y, z + 1, side);
							vertex[i - 2] = new Vector4f(x + 1, y + 1, z + 1, side);
							vertex[i - 1] = new Vector4f(x + 1, y, z + 1, side);
							merged++;
						} else {
							vertex[i++] = new Vector4f(x + 1, y, z, side);
							vertex[i++] = new Vector4f(x + 1, y + 1, z, side);
							vertex[i++] = new Vector4f(x + 1, y, z + 1, side);
							vertex[i++] = new Vector4f(x + 1, y + 1, z, side);
							vertex[i++] = new Vector4f(x + 1, y + 1, z + 1, side);
							vertex[i++] = new Vector4f(x + 1, y, z + 1, side);
						}
						vis = true;
					}
				}
			}

			// View from negative y

			for(int x = 0; x < CX; x++) {
				for(int y = CY - 1; y >= 0; y--) {
					for(int z = 0; z < CZ; z++) {
						
						max++;
						
						if(isblocked(x, y, z, x, y - 1, z)) {
							vis = false;
							continue;
						}

						int bottom = blocks_array[x][y][z].bottom;


						if(vis && z != 0 && blocks_array[x][y][z].type == blocks_array[x][y][z - 1].type) {
							vertex[i - 4] = new Vector4f(x, y, z + 1, bottom);
							vertex[i - 2] = new Vector4f(x + 1, y, z + 1, bottom);
							vertex[i - 1] = new Vector4f(x, y, z + 1, bottom);
							merged++;
						} else {
							vertex[i++] = new Vector4f(x, y, z, bottom);
							vertex[i++] = new Vector4f(x + 1, y, z, bottom);
							vertex[i++] = new Vector4f(x, y, z + 1, bottom);
							vertex[i++] = new Vector4f(x + 1, y, z, bottom);
							vertex[i++] = new Vector4f(x + 1, y, z + 1, bottom);
							vertex[i++] = new Vector4f(x, y, z + 1, bottom);
						}
						vis = true;
					}
				}
			}

			// View from positive y

			for(int x = 0; x < CX; x++) {
				for(int y = 0; y < CY; y++) {
					for(int z = 0; z < CZ; z++) {
						if(isblocked(x, y, z, x, y + 1, z)) {
							vis = false;
							continue;
						}

						int top = blocks_array[x][y][z].top;


						if(vis && z != 0 && blocks_array[x][y][z].type == blocks_array[x][y][z - 1].type) {
							vertex[i - 5] = new Vector4f(x, y + 1, z + 1, top);
							vertex[i - 2] = new Vector4f(x, y + 1, z + 1, top);
							vertex[i - 1] = new Vector4f(x + 1, y + 1, z + 1, top);
							merged++;
						} else {
							vertex[i++] = new Vector4f(x, y + 1, z, top);
							vertex[i++] = new Vector4f(x, y + 1, z + 1, top);
							vertex[i++] = new Vector4f(x + 1, y + 1, z, top);
							vertex[i++] = new Vector4f(x + 1, y + 1, z, top);
							vertex[i++] = new Vector4f(x, y + 1, z + 1, top);
							vertex[i++] = new Vector4f(x + 1, y + 1, z + 1, top);
						}
						vis = true;
					}
				}
			}

			// View from negative z

			for(int x = 0; x < CX; x++) {
				for(int z = CZ - 1; z >= 0; z--) {
					for(int y = 0; y < CY; y++) {
						
						max++;
						
						if(isblocked(x, y, z, x, y, z - 1)) {
							vis = false;
							continue;
						}

						int side = blocks_array[x][y][z].right;

						if(vis && y != 0 && blocks_array[x][y][z].type == blocks_array[x][y - 1][z].type) {
							vertex[i - 5] = new Vector4f(x, y + 1, z, side);
							vertex[i - 3] = new Vector4f(x, y + 1, z, side);
							vertex[i - 2] = new Vector4f(x + 1, y + 1, z, side);
							merged++;
						} else {
							vertex[i++] = new Vector4f(x, y, z, side);
							vertex[i++] = new Vector4f(x, y + 1, z, side);
							vertex[i++] = new Vector4f(x + 1, y, z, side);
							vertex[i++] = new Vector4f(x, y + 1, z, side);
							vertex[i++] = new Vector4f(x + 1, y + 1, z, side);
							vertex[i++] = new Vector4f(x + 1, y, z, side);
						}
						vis = true;
					}
				}
			}

			// View from positive z

			for(int x = 0; x < CX; x++) {
				for(int z = 0; z < CZ; z++) {
					for(int y = 0; y < CY; y++) {
						
						max++;
						
						if(isblocked(x, y, z, x, y, z + 1)) {
							vis = false;
							continue;
						}

						int side = blocks_array[x][y][z].left;

						if(vis && y != 0 && blocks_array[x][y][z].type == blocks_array[x][y - 1][z].type) {
							vertex[i - 4] = new Vector4f(x, y + 1, z + 1, side);
							vertex[i - 3] = new Vector4f(x, y + 1, z + 1, side);
							vertex[i - 1] = new Vector4f(x + 1, y + 1, z + 1, side);
							merged++;
						} else {
							vertex[i++] = new Vector4f(x, y, z + 1, side);
							vertex[i++] = new Vector4f(x + 1, y, z + 1, side);
							vertex[i++] = new Vector4f(x, y + 1, z + 1, side);
							vertex[i++] = new Vector4f(x, y + 1, z + 1, side);
							vertex[i++] = new Vector4f(x + 1, y, z + 1, side);
							vertex[i++] = new Vector4f(x + 1, y + 1, z + 1, side);
						}
						vis = true;
					}
				}
			}		 
		 
		 System.out.println("MAX:" + max);
		
		 criarVerticesFinais(blocks);
		 System.out.println("TOTAL cubos:"    + blocks.size());
		  
	}
	
	public void criarVerticesFinais(List<Block> blocks) {
		this.vertices = new float[this.vertex.length*4];
		
		  int counter = 0;
		  
		  textCoords = new float[this.vertex.length*3];
		  textCoordsCount = 0;
		  countAddTextCoord = 0;		
		
		for(int i = 0; i < this.vertex.length; i++) {
			if(vertex[i] != null) {
			  this.vertices[counter++] = vertex[i].x;
			  this.vertices[counter++] = vertex[i].y;
			  this.vertices[counter++] = vertex[i].z;
			  this.vertices[counter++] = vertex[i].w;
			}
		  
//		  adicionarTextCoords();
		}
		
	  System.out.println("textCoordsCount:" + textCoordsCount);
	  vertexCount = this.vertices.length/3;		
	}
	
	public boolean isHidden(int x, int y, int z) {
		
		boolean negX = false;
		boolean posiX = false;
		
		boolean negY = false;
		boolean posiY = false;
		
		boolean negZ = false;
		boolean posiZ = false;		

		if(x > 0) {
			if(blocks_array[x-1][y][z].active) {
				negX = true;
			}
		}
		
		if(x < CX-1) {
			if(blocks_array[x+1][y][z].active) {
				posiX = true;
			}
		}
		
		//Y
		if(y > 0) {
			if(blocks_array[x][y-1][z].active) {
				negY = true;
			}
		}
		
		if(y < CY-1) {
			if(blocks_array[x][y+1][z].active) {
				posiY = true;
			}
		}
		
		//Z
		if(z > 0) {
			if(blocks_array[x][y][z-1].active) {
				negZ = true;
			}
		}
		
		if(z < CZ-1) {
			if(blocks_array[x][y][z+1].active) {
				posiZ = true;
			}		
		}
		
		return posiX && posiY && posiZ && negX && negY && negZ;
		
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
		 
		 public static final int T_GRASS_FRONT = 1;
		 public static final int T_GRASS_BACK = 2;
		 public static final int T_GRASS_LEFT = 3;
		 public static final int T_GRASS_RIGHT = 4;
		 public static final int T_GRASS_TOP = 5;
		 public static final int T_GRASS_BOTTOM = 6;
		 
		 static final int T_ROCK = 3;
		 static final int T_ROCK_TOP = 4;
		 static final int T_ROCK_BOTTOM = -3;
		
		 int y;
		 int front = T_GRASS_FRONT;
		 int back = T_GRASS_BACK;
		 int left = T_GRASS_LEFT;
		 int right = T_GRASS_RIGHT;
		 int top = T_GRASS_TOP;
		 int bottom = T_GRASS_BOTTOM;
		 
		 BlockTypes type = BlockTypes.T_GRASS;
		 
		 List<Vector3f> verts = new ArrayList<>();
		 boolean active = false;
		 boolean blocked = false;
	}
	
	enum BlockTypes {
		T_GRASS
	}
	
//	static class TileParser {
//		
//		public static int parseBottom(int type) {
//			int ret = 1;
//			
//			switch (type) {
//			case Block.T_GRASS:
//				ret = Block.T_GRASS_BOTTOM;
//				break;
//
//			default:
//				break;
//			}
//			
//			return ret;
//		}
//		
//		public static int parseTop(int type) {
//			int ret = 1;
//			
//			switch (type) {
//			case Block.T_GRASS:
//				ret = Block.T_GRASS_TOP;
//				break;
//
//			default:
//				break;
//			}
//			
//			return ret;
//		}		
//	}	
	
//	public void criarMontanhas(Block blocks[][][]) {
//		   
//		  int count = 0;
//		  
//		  for(int x = 0; x < CX; x++) {
//			  for(int z = 0; z < CZ; z++) {
//		    	
//		    	for(int y = 0; y < CY; y++) {
//					if(z >= count)
//						blocks[x][y][z].active = true;
//					count++;				
//				}
//		    	count = 0;
//		  	}
//		  	
//		  	
//		  }
//	}
	
}
