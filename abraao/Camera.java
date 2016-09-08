package abraao;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class Camera {

//	private int uniformLocation;
	private Matrix4f matrix;

    private final Vector3f position = new Vector3f();
    private final Vector3f rotation = new Vector3f();
    
	public Camera() {//ShaderProgram shaderProg
//	    uniformLocation = glGetUniformLocation(shaderProg.getProgramId(), "viewMatrix");
	    matrix = new Matrix4f();
	    matrix.identity();
	}
	
//	public int getUniformLocation() {
//		return uniformLocation;
//	}
	
//	public void set(Matrix4f value) {
//	    FloatBuffer fb = BufferUtils.createFloatBuffer(16);
//	    value.get(fb);
//	    glUniformMatrix4fv(uniformLocation, false, fb);
//	}
//	
//	public void set() {
//		this.set(matrix);
//	}
	
	public void update() {
//		matrix.identity().
//        rotateX((float)Math.toRadians(rotation.x)).
//        rotateY((float)Math.toRadians(rotation.y)).
//        rotateZ((float)Math.toRadians(rotation.z))
//        .translate(position);//FIRST ROTATION next TRANSLATE

		matrix.identity();
	    // First do the rotation so camera rotates over its position
		matrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
	        .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
	    // Then do the translation
		matrix.translate(-position.x, -position.y, -position.z);
	}
	
	public void rotate(float x, float y, float z) {
		this.rotation.x += x;
		this.rotation.y += y;
		this.rotation.z += z;
	}
	
	public void position(float x, float y, float z) {//considera a rotacao
		
		/*
		 * As you can see we first need to do the rotation and then the
		 * translation. If we do the opposite we would not be rotating along the
		 * camera position but along the coordinates origin. Please also note
		 * that in the movePosition method of the Camera class we just not
		 * simply increase the camera position by and offset. We also take into
		 * consideration the rotation along the y axis, the yaw, in order to
		 * calculate the final position. If we would just increase the camera
		 * position by the offset the camera will not move in the direction its
		 * facing.
		 */
		
        if ( z != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * z;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * z;
        }
        if ( x != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * x;
        }
        position.y += y;
	}
	
	public Matrix4f mul(Matrix4f world) {
		return new Matrix4f(matrix).mul(world);
	}
	
}
