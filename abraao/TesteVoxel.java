package abraao;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.InputStream;
import java.util.Scanner;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

public class TesteVoxel {

	// The window handle
	private long window;
    
	int WIDTH = 1024;
	int HEIGHT = 768;
	
	public void run() throws Exception {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		try {
			init();
			loop();

			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
		} finally {
			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
	}

	private void init() throws Exception {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);


		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
		});

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(
			window,
			(vidmode.width() - WIDTH) / 2,
			(vidmode.height() - HEIGHT) / 2
		);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);
		
		// Make the window visible
		glfwShowWindow(window);
		
	}

	private void loop() throws Exception {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		initShader();
		initBlocks();		
		initInput();
		
		// Set the clear color
//		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_DEPTH_TEST);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			
			
			renderBlock();
			
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}
	
	ShaderProgram shaderProgram;
//	BlockRenderer blockRenderer;
//	TriangleRender triangleRender;
	Render render;
	ProjectionMatrix projectMatrix;//como projeto as imagens na tela, profundidade, etc, aspecto ratio
	private WorldMatrix worldMatrix;//a posicao do item na tela gera a modelViewMatrix quando multiplicado pela camera
	Camera camera;
    private final Vector3f cameraInc = new Vector3f();
	private MouseInput mouseInput;
    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;
	
	public void initInput() {
		mouseInput = new MouseInput(window);
		mouseInput.init();
	}
	
	public void initBlocks() {
//		blockRenderer = new BlockRenderer();
//		triangleRender = new TriangleRender();
//		render = new RectRender();
//		render = new BlockRender(shaderProgram);
		render = new ChunckRender(shaderProgram);
	}
	
	
	public void initShader() throws Exception {
		shaderProgram = new ShaderProgram();
		shaderProgram.createVertexShader(loadResource("/abraao/vertex.vs"));
		shaderProgram.createFragmentShader(loadResource("/abraao/fragment.vs"));
		shaderProgram.link();
		
		//apos compilar o programa posso criar os uniforms
		float aspectRatio = (float)  WIDTH / HEIGHT;
		projectMatrix = new ProjectionMatrix(shaderProgram, aspectRatio);
		worldMatrix = new WorldMatrix(shaderProgram);
		camera = new Camera();
//		camera.position(10, 20, 0);
	}
	
	
	public void renderBlock() {
		
		input();
		
		glViewport(0, 0, WIDTH, HEIGHT);//para lidar com o redimensionamento da tela
		
		shaderProgram.bind();
		
		projectMatrix.set();//configura a matriz de projeção
		
		render.update(worldMatrix, camera);//aqui faco update na view model matrix por isso passa a camera e o worldmatrix
		render.render();
		
		shaderProgram.unbind();
	}
	
    public void input() {
        cameraInc.set(0, 0, 0);
        if (isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
        
        mouseInput.input();
        
        updateCamera();
    }	
	
    public void updateCamera() {
        // Update camera position
//    	System.out.println("cameraInc.x: " + cameraInc.x);
        camera.position(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        
        // Update camera based on mouse            
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.rotate(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
        
        camera.update();
    }
    
    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }
    
    public static String loadResource(String fileName) throws Exception {
        String result = "";
        try (InputStream in = TesteVoxel.class.getClass().getResourceAsStream(fileName)) {
            result = new Scanner(in, "UTF-8").useDelimiter("\\A").next();
        }
        return result;
    }	
	
	public static void main(String[] args) throws Exception {
		new TesteVoxel().run();
	}
	

}