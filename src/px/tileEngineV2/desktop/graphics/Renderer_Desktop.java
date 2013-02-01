package px.tileEngineV2.desktop.graphics;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import px.tileEngineV2.graphics.Renderer;

public class Renderer_Desktop extends Renderer implements GLEventListener {
    
    private GL4 context;

    @Override
    public void drawQuad(int texture, Vector3f location, Vector2f size,
            Vector4f tint) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void drawText(String text, Vector3f location, Vector2f size,
            Vector4f color) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        context = drawable.getGL().getGL4();
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        context = drawable.getGL().getGL4();
        // TODO Auto-generated method stub
        
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        context = drawable.getGL().getGL4();
        // TODO Auto-generated method stub
        
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        context = drawable.getGL().getGL4();
        // TODO Auto-generated method stub
        
    }
    
    public GL4 getContext() {
        return context;
    }
    
}
