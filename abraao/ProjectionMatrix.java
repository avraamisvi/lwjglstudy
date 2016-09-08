package abraao;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class ProjectionMatrix {

	private int uniformLocation;
	private Matrix4f projectionMatrix;

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;
    
	public ProjectionMatrix(ShaderProgram shaderProg, float aspectRatio) {
	    uniformLocation = glGetUniformLocation(shaderProg.getProgramId(), "projectionMatrix");
	    projectionMatrix  = new Matrix4f();
	    
	    update(aspectRatio);
	}
	
	public int getUniformLocation() {
		return uniformLocation;
	}
	
	public void set(Matrix4f value) {
	    FloatBuffer fb = BufferUtils.createFloatBuffer(16);
	    value.get(fb);
	    glUniformMatrix4fv(uniformLocation, false, fb);
	}
	
	public void set() {
		this.set(projectionMatrix);
	}
	
	public void update(float aspectRatio) {
		projectionMatrix.identity();
		projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
		/*
		 *  Field of View: The Field of View angle in radians. We will define a constant that holds that value
			Aspect Ratio.
			Distance to the near plane (z-near)
			Distance to the far plane (z-far).
		 */
	}
}
