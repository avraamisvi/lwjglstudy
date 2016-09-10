package abraao;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class WorldMatrix {

	private int uniformLocation;
	private int uniformModelLocation;
	private Matrix4f matrix;
	private Matrix4f modelMatrix;

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;
    
	public WorldMatrix(ShaderProgram shaderProg) {//, float aspectRatio
	    uniformLocation = glGetUniformLocation(shaderProg.getProgramId(), "modelViewMatrix");
	    uniformModelLocation = glGetUniformLocation(shaderProg.getProgramId(), "modelMatrix");
	    matrix = new Matrix4f();
	    matrix.identity();
//	    update(aspectRatio);
	}
	
//	public int getUniformLocation() {
//		return uniformLocation;
//	}
//	
	public void set(Matrix4f value) {
	    FloatBuffer fb = BufferUtils.createFloatBuffer(16);
	    value.get(fb);
	    glUniformMatrix4fv(uniformLocation, false, fb);
	    
	    fb = BufferUtils.createFloatBuffer(16);
	    modelMatrix.get(fb);
	    glUniformMatrix4fv(uniformModelLocation, false, fb);
	}
	
	public void set() {
		this.set(matrix);
	}
	
	public void update(Vector3f offset, Vector3f rotation, float scale) {
		matrix.identity().translate(offset).
        rotateX((float)Math.toRadians(rotation.x)).
        rotateY((float)Math.toRadians(rotation.y)).
        rotateZ((float)Math.toRadians(rotation.z)).
        scale(scale);
		
		this.modelMatrix = new Matrix4f(matrix);
		
		/*
		 *  Field of View: The Field of View angle in radians. We will define a constant that holds that value
			Aspect Ratio.
			Distance to the near plane (z-near)
			Distance to the far plane (z-far).
		 */
	}
	
	public void updateCamera(Camera camera) {
		
		this.set(camera.mul(matrix));
	}
}
