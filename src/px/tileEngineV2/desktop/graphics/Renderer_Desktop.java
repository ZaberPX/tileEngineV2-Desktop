package px.tileEngineV2.desktop.graphics;

import java.nio.FloatBuffer;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import px.tileEngineV2.actors.Actor;
import px.tileEngineV2.core.GameCore;
import px.tileEngineV2.desktop.core.GameCore_Desktop;
import px.tileEngineV2.graphics.Renderer;
import px.tileEngineV2.graphics.Texture;
import px.tileEngineV2.graphics.TextureCache;
import px.tileEngineV2.ui.Window;
import px.tileEngineV2.ui.Window.HorzAlign;
import px.tileEngineV2.ui.Window.VertAlign;
import px.tileEngineV2.world.Tile;

import com.jogamp.opengl.util.FPSAnimator;

/**Core OpenGL Renderer implementation for Linux and Windows, all drawing done by in-game
 * actors must be done through a class like this.
 * @author Michael Stopa */
public class Renderer_Desktop extends Renderer implements GLEventListener {
    
    // ++++ ++++ Constants ++++ ++++
    
    public static final int SCREEN_MIN_SIZE = Tile.TILE_SIZE * 12;
    
    // ++++ ++++ Data ++++ ++++
    
    //Drawing Context
    private GL4 gl;
    private GLAutoDrawable autoDrawable;
    private FPSAnimator animator;
    
    //OpenGL Objects
    private int vao;
    private int vbo;
    
    //Shader Program
    private int shaderProgram;
    
    //Uniforms
    private int transUniform;
    private int depthUniform;
    private int modelUniform;
    private int viewUniform;
    private int projUniform;
    private int tintUniform;
    
    private Texture_Desktop tex;
    private TextureLoader loader;
    
    //UI Management
    private Matrix4f uiViewTransform;
    private float uiHorzOffset = 0;
    private float uiVertOffset = 0;
    
    // ++++ Transformation ++++
    
    private Matrix4f projTransform = new Matrix4f();
    
    // ++++ ++++ Initialization ++++ ++++

    @Override
    public void init(GLAutoDrawable drawable) {
        autoDrawable = drawable;
        gl = drawable.getGL().getGL4();
        
        //OpenGL Fixed Function Configuration
        
        gl.glClearColor(0.0f, 0.0f, 0.1f, 0.0f);
        
        gl.glEnable(GL4.GL_DEPTH_TEST);
        gl.glDepthFunc(GL4.GL_LEQUAL);
        
        gl.glEnable(GL4.GL_BLEND);
        gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glEnable(GL4.GL_CULL_FACE);
        gl.glCullFace(GL4.GL_BACK);
        
        //VAO VBO Shader Initialization
        
        int[] temp = new int[1];
        
        //Create VAO
        gl.glGenVertexArrays(1, temp, 0);
        vao = temp[0];
        gl.glBindVertexArray(vao);
        
        //Compile Shader Program
        shaderProgram = ShaderLoader.loadShaders(drawable,
                "res/shaders/vertex.glsl", "res/shaders/fragment.glsl");
        gl.glUseProgram(shaderProgram);
        
        transUniform = gl.glGetUniformLocation(shaderProgram, "trans");
        depthUniform = gl.glGetUniformLocation(shaderProgram, "depth");
        modelUniform = gl.glGetUniformLocation(shaderProgram, "model");
        viewUniform = gl.glGetUniformLocation(shaderProgram, "view");
        projUniform = gl.glGetUniformLocation(shaderProgram, "proj");
        tintUniform = gl.glGetUniformLocation(shaderProgram, "tint");
        
        float[] vertices = {
                //Position         //Texcoord
                -0.5f, 0.5f, 0.0f, 0.0f, //Top Left
                -0.5f,-0.5f, 0.0f, 1.0f, //Bottom Left
                 0.5f, 0.5f, 1.0f, 0.0f, //Top Right
                 0.5f,-0.5f, 1.0f, 1.0f //Bottom Right
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
        
        //Unbind all
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
        
        loader = new TextureLoader();
        tex = new Texture_Desktop(loader, "textures/splash");
        
        //View Setup
        uiViewTransform = new Matrix4f();
        uiViewTransform.m00 = 1f/100f;
        uiViewTransform.m11 = 1f/100f;
    }
    
    // ++++ ++++ Disposal ++++ ++++

    @Override
    public void dispose(GLAutoDrawable drawable) {
        autoDrawable = drawable;
        gl = drawable.getGL().getGL4();
        int[] temp = new int[1];

        temp[0] = vao;
        gl.glDeleteVertexArrays(1, temp, 0);
        temp[0] = vbo;
        gl.glDeleteBuffers(1, temp, 0);
        gl.glDeleteProgram(shaderProgram);
        
    }
    
    // ++++ ++++ Rendering ++++ ++++

    @Override
    public void drawQuadWorld(Texture texture, Matrix4f model, float depth,
            Vector4f tint) {
        gl.glBindTexture(GL4.GL_TEXTURE_2D, ((Texture_Desktop)texture).getGLTexture());
        
        Matrix3f trans = texture.getTransform();
        FloatBuffer transBuffer = FloatBuffer.allocate(9);
        trans.store(transBuffer);
        transBuffer.rewind();
        
        FloatBuffer modelBuffer = FloatBuffer.allocate(16);
        model.store(modelBuffer);
        modelBuffer.rewind();
        
        Matrix4f view = new Matrix4f();
        FloatBuffer viewBuffer = FloatBuffer.allocate(16);
        //GameCore.getInstance().getWorld().getCamera().getViewTransform()
        //        .store(viewBuffer);
        view.store(viewBuffer);
        viewBuffer.rewind();
        
        FloatBuffer projBuffer = FloatBuffer.allocate(16);
        projTransform.store(projBuffer);
        projBuffer.rewind();
        
        gl.glUniformMatrix3fv(transUniform, 1, false, transBuffer);
        gl.glUniform1f(depthUniform, depth);
        gl.glUniformMatrix4fv(modelUniform, 1, false, modelBuffer);
        gl.glUniformMatrix4fv(viewUniform, 1, false, viewBuffer);
        gl.glUniformMatrix4fv(projUniform, 1, false, projBuffer);
        gl.glUniform4f(tintUniform, tint.x, tint.y, tint.z, tint.z);
        
        gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
        
        gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
    }
    
    @Override
    public void drawQuadUi(Texture texture, Matrix4f model, float depth,
            Window.HorzAlign horzAlign, Window.VertAlign vertAlign, Vector4f tint) {
        gl.glBindTexture(GL4.GL_TEXTURE_2D, ((Texture_Desktop)texture).getGLTexture());
        
        Matrix3f trans = texture.getTransform();
        FloatBuffer transBuffer = FloatBuffer.allocate(9);
        trans.store(transBuffer);
        transBuffer.rewind();
        
        if (horzAlign == HorzAlign.LEFT) {
            Matrix4f align = Matrix4f.translate(new Vector2f(-uiHorzOffset, 0f), 
                    new Matrix4f(), null);
            model = Matrix4f.mul(align, model, null);
        } else if (horzAlign == HorzAlign.RIGHT) {
            Matrix4f align = Matrix4f.translate(new Vector2f(uiHorzOffset, 0f), 
                    new Matrix4f(), null);
            model = Matrix4f.mul(align, model, null);
        }
        if (vertAlign == VertAlign.BOTTOM) {
            Matrix4f align = Matrix4f.translate(new Vector2f(0f, -uiVertOffset), 
                    new Matrix4f(), null);
            model = Matrix4f.mul(align, model, null);
        } else if (vertAlign == VertAlign.TOP) {
            Matrix4f align = Matrix4f.translate(new Vector2f(0f, uiVertOffset), 
                    new Matrix4f(), null);
            model = Matrix4f.mul(align, model, null);
        }
        FloatBuffer modelBuffer = FloatBuffer.allocate(16);
        model.store(modelBuffer);
        modelBuffer.rewind();
        
        FloatBuffer viewBuffer = FloatBuffer.allocate(16);
        uiViewTransform.store(viewBuffer);
        viewBuffer.rewind();
        
        FloatBuffer projBuffer = FloatBuffer.allocate(16);
        projTransform.store(projBuffer);
        projBuffer.rewind();
        
        gl.glUniformMatrix3fv(transUniform, 1, false, transBuffer);
        gl.glUniform1f(depthUniform, depth);
        gl.glUniformMatrix4fv(modelUniform, 1, false, modelBuffer);
        gl.glUniformMatrix4fv(viewUniform, 1, false, viewBuffer);
        gl.glUniformMatrix4fv(projUniform, 1, false, projBuffer);
        gl.glUniform4f(tintUniform, tint.x, tint.y, tint.z, tint.z);
        
        gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
        
        gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
    }

    @Override
    public void drawText(String text, Vector3f location, Vector2f size, Vector4f color) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        autoDrawable = drawable;
        gl = drawable.getGL().getGL4();
        
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        
        gl.glUseProgram(shaderProgram);
        gl.glBindVertexArray(vao);
        
        drawQuadUi(tex, 
                Actor.modelTransform(new Vector2f(0, 0), 0f, new Vector2f(10f, 10f)), 
                0f, HorzAlign.CENTER, VertAlign.CENTER, new Vector4f(1f,1f,1f,1f));
        //TODO Draw all objects in Current World/Battle/Cutscene/Menu
        //TODO Create new view transform from World.getCamera() data.

        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        autoDrawable = drawable;
        gl = drawable.getGL().getGL4();

        float scaleX;
        float scaleY;
        if (width >= height) {
            scaleY = 1f;
            scaleX = width/height;
            uiVertOffset = 0;
            uiHorzOffset = (width-height)/2/(height/100);
        } else {
            scaleX = height/width;
            scaleY = 1f;
            uiVertOffset = (height-width)/2/(width/100);
            uiHorzOffset = 0;
        }
        
        projTransform = Matrix4f.scale(
                new Vector3f(scaleX, scaleY, 1f), new Matrix4f(), null);
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    public GL4 getContext() {
        return gl;
    }
    
    public GLAutoDrawable getAutoDrawable() {
        return autoDrawable;
    }
    
    public TextureCache getTextureCache() {
        return (TextureCache) loader;
    }

    @Override
    public void start() {
        //TODO fetch refresh rate from GameCore.
        animator = new FPSAnimator(((GameCore_Desktop)GameCore.getInstance())
                .getFrame().getCanvas(), 60);
        animator.start();
    }
    
}
