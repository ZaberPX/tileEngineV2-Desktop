package px.tileEngineV2.desktop.core;

import java.nio.FloatBuffer;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

import px.tileEngineV2.desktop.graphics.ShaderLoader;
import px.tileEngineV2.desktop.graphics.TextureLoader;

import com.jogamp.opengl.util.FPSAnimator;

/**A self-contained class for displaying a temporary splash screen while the larger
 * game loads.
 * @author Michael Stopa */
class SplashScreen implements GLEventListener {
    
    // ++++ ++++ Data ++++ ++++
    
    public FPSAnimator fpsAnimator;
    
    //OpenGL Objects
    private int vao;
    private int vbo;
    private int tex;
    
    //Shader Program
    private int shaderProgram;
    
    //Uniforms
    private int textureTransformUniform;
    private int depthUniform;
    private int modelUniform;
    private int viewUniform;
    
    private int tintUniform;
    
    private FloatBuffer vertBuffer;
    private FloatBuffer texBuffer;
    
    // ++++ ++++ OpenGL Event Handling ++++ ++++

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        
        // ++++ OpenGL Configuration ++++
        
        gl.glClearColor(0.0f, 0.0f, 0.1f, 0.0f);
        
        gl.glEnable(GL4.GL_DEPTH_TEST);
        gl.glDepthFunc(GL4.GL_LEQUAL);
        
        gl.glEnable(GL4.GL_BLEND);
        gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glEnable(GL4.GL_CULL_FACE);
        gl.glCullFace(GL4.GL_BACK);
        
        // ++++ VAO/VBO/Shader Initialization ++++
        
        int[] temp = new int[1];
        
        //Create VAO
        gl.glGenVertexArrays(1, temp, 0);
        vao = temp[0];
        gl.glBindVertexArray(vao);
        
        //Compile Shader Program
        shaderProgram = ShaderLoader.loadShaders(drawable,
                "res/shaders/vertex.glsl", "res/shaders/fragment.glsl");
        gl.glUseProgram(shaderProgram);
        
        textureTransformUniform = 
                gl.glGetUniformLocation(shaderProgram, "textureTransform");
        depthUniform = gl.glGetUniformLocation(shaderProgram, "depth");
        modelUniform = gl.glGetUniformLocation(shaderProgram, "model");
        viewUniform = gl.glGetUniformLocation(shaderProgram, "view");
        tintUniform = gl.glGetUniformLocation(shaderProgram, "tint");
        
        float[] vertices = {
                //Position         //Texcoord
                -1.0f, 1.0f, 0.0f, 0.0f, //Top Left
                -1.0f,-1.0f, 0.0f, 1.0f, //Bottom Left
                 1.0f, 1.0f, 1.0f, 0.0f, //Top Right
                 1.0f,-1.0f, 1.0f, 1.0f //Bottom Right
        };
        
        //Store verts in a Vertex Buffer Object
        gl.glGenBuffers(1, temp, 0);
        vbo = temp[0];
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertices.length * 4,
                FloatBuffer.wrap(vertices), GL4.GL_STATIC_DRAW);
        
        int positionAttribute = gl.glGetAttribLocation(shaderProgram, "position");
        gl.glEnableVertexAttribArray(positionAttribute);
        gl.glVertexAttribPointer(positionAttribute, 2, GL4.GL_FLOAT, false, 4 * 4, 0);

        int texcoordAttribute = gl.glGetAttribLocation(shaderProgram, "texcoord");
        gl.glEnableVertexAttribArray(texcoordAttribute);
        gl.glVertexAttribPointer(texcoordAttribute, 2, GL4.GL_FLOAT, false, 4 * 4, 2 * 4);
        
        //Load Texture
        tex = TextureLoader.loadTexturePng(drawable, "res/textures/splash.png");
        
        //Setup Buffer loaded with identity matrix (no transforms)
        Matrix4f matrix = new Matrix4f();
        vertBuffer = FloatBuffer.allocate(16);
        matrix.store(vertBuffer);
        
        Matrix3f texMatrix = new Matrix3f();
        texBuffer = FloatBuffer.allocate(9);
        texMatrix.store(texBuffer);
        
        //Unbind all
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        if (fpsAnimator != null) fpsAnimator.stop();
        
        GL4 gl = drawable.getGL().getGL4();
        int[] temp = new int[1];

        temp[0] = vao;
        gl.glDeleteVertexArrays(1, temp, 0);
        temp[0] = vbo;
        gl.glDeleteBuffers(1, temp, 0);
        temp[0] = tex;
        gl.glDeleteTextures(1, temp, 0);
        gl.glDeleteProgram(shaderProgram);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(shaderProgram);
        gl.glBindVertexArray(vao);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, tex);
        
        texBuffer.rewind();
        gl.glUniformMatrix3fv(textureTransformUniform, 1, false, texBuffer);
        gl.glUniform1f(depthUniform, 0f);
        vertBuffer.rewind();
        gl.glUniformMatrix4fv(modelUniform, 1, false, vertBuffer);
        vertBuffer.rewind();
        gl.glUniformMatrix4fv(viewUniform, 1, false, vertBuffer);
        gl.glUniform4f(tintUniform, 1f, 1f, 1f, 1f);
        
        gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
        
        gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // SplashScreen is non-resizable, so it doesn't get reshaped.
    }
}
