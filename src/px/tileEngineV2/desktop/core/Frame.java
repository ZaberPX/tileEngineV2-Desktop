package px.tileEngineV2.desktop.core;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.nio.FloatBuffer;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Matrix4f;

import px.tileEngineV2.desktop.graphics.ShaderLoader;
import px.tileEngineV2.desktop.graphics.TextureLoader;

/**JFrame object the game will actually be presented in, may be deleted and replaced when
 * the options menu changes the window or resolution.
 * @author Michael Stopa */
@SuppressWarnings("serial") //never actually going to serialize this
public class Frame extends JFrame {
    
    // ++++ ++++ Data ++++ ++++
    
    /**Currently used GLCanvas */
    private GLCanvas glCanvas;
    /**DisplayMode used when rendering fullscreen. Should be null when rendering to any
     * kind of window. */
    private DisplayMode displayMode;
    
    // ++++ ++++ Initialization ++++ ++++
    
    /**Creates a window containing a splash-screen, use setDisplayType() methods to
     * initiate a proper drawing canvas. */
    public Frame() {
        super("Debuggers");
        
        GLProfile profile = GLProfile.getDefault();
        GLCapabilities capabilities = new GLCapabilities(profile);
        glCanvas = new GLCanvas(capabilities);
        //TODO setIconImage
        
        getContentPane().add(glCanvas);
        glCanvas.setPreferredSize(new Dimension(1280, 720));
    }
    
    // ++++ ++++ Inner Class ++++ ++++
    
    /**A special inner class for displaying a simple splash screen while the rest of the
     * game loads.
     * @author Michael Stopa */
    private class SplashScreen implements GLEventListener {
        
        // ++++ ++++ Data ++++ ++++
        
        //OpenGL Objects
        private int vao;
        private int vbo;
        private int tex;
        
        //Shader Program
        private int shaderProgram;
        
        //Uniforms
        private int texScaleUniform;
        private int depthUniform;
        private int modelUniform;
        private int viewUniform;
        
        private int tintUniform;
        private int texOffsetUniform;
        
        private FloatBuffer buffer;
        
        // ++++ ++++ OpenGL Event Handling ++++ ++++

        @Override
        public void init(GLAutoDrawable drawable) {
            GL4 gl = drawable.getGL().getGL4();
            int[] temp = new int[1];
            
            //Create VAO
            gl.glGenVertexArrays(1, temp, 0);
            
            //Compile Shader Program
            shaderProgram = ShaderLoader.loadShaders(drawable,
                    "res/shaders/vertex.glsl", "res/shaders/fragment.glsl");
            gl.glUseProgram(shaderProgram);
            
            texScaleUniform = gl.glGetUniformLocation(shaderProgram, "texScale");
            depthUniform = gl.glGetUniformLocation(shaderProgram, "depth");
            modelUniform = gl.glGetUniformLocation(shaderProgram, "model");
            viewUniform = gl.glGetUniformLocation(shaderProgram, "view");
            tintUniform = gl.glGetUniformLocation(shaderProgram, "tint");
            texOffsetUniform = gl.glGetUniformLocation(shaderProgram, "texOffset");
            
            float[] vertices = {
                    //Position         //Texcoord
                    -1.0f, 1.0f, 0.0f, 0.0f, //Top Left
                    -1.0f,-1.0f, 0.0f, 1.0f, //Bottom Left
                     1.0f, 1.0f, 1.0f, 0.0f, //Top Right
                     1.0f,-1.0f, 1.0f, 1.0f //Bottom Right
            };
            
            int positionAttribute = gl.glGetAttribLocation(shaderProgram, "position");
            gl.glEnableVertexAttribArray(positionAttribute);
            gl.glVertexAttribPointer(positionAttribute, 2, 
                    GL4.GL_FLOAT, false, 4 * 4, 0);

            int texcoordAttribute = gl.glGetAttribLocation(shaderProgram, "position");
            gl.glEnableVertexAttribArray(texcoordAttribute);
            gl.glVertexAttribPointer(texcoordAttribute, 2, 
                    GL4.GL_FLOAT, false, 4 * 4, 2 * 4);
            
            //Load Texture
            tex = TextureLoader.loadTexturePng(drawable, "res/textures/splash.png");
            
            //Setup Buffer loaded with identity matrix (no transforms)
            Matrix4f matrix = new Matrix4f();
            buffer = FloatBuffer.allocate(16);
            matrix.store(buffer);
            
            //Unbind all
            gl.glBindVertexArray(0);
            gl.glUseProgram(0);
            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
            gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
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

            gl.glUseProgram(shaderProgram);
            gl.glBindVertexArray(vao);
            gl.glBindTexture(GL4.GL_TEXTURE_2D, tex);
            
            gl.glUniform2f(texScaleUniform, 1f, 1f);
            gl.glUniform1f(depthUniform, 0f);
            buffer.rewind();
            gl.glUniformMatrix4fv(modelUniform, 1, false, buffer);
            buffer.rewind();
            gl.glUniformMatrix4fv(viewUniform, 1, false, buffer);
            gl.glUniform4f(tintUniform, 1f, 1f, 1f, 1f);
            gl.glUniform2f(texOffsetUniform, 0f, 0f);
            
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
}
