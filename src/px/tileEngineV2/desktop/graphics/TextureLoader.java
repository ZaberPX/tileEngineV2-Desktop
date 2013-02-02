package px.tileEngineV2.desktop.graphics;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;

import px.tileEngineV2.graphics.TextureCache;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

/**Handles all texture loading through TWL's PNGDecoder. Automatically stores all
 * retrieved textures, re-using the same references when the same texture is requested
 * multiple times so as not to have multiple instances of the same image laoded in
 * memory.
 * @author Michael Stopa */
public class TextureLoader extends TextureCache {
    
    // ++++ ++++ Data ++++ ++++
    
    private HashMap<String, Integer> textures;
    
    // ++++ ++++ Static File I/O ++++ ++++
    
    /**Creates a new OpenGL Texture object from a provided .png file.
     * @param drawable OpenGL context needed for access to Graphics Card.
     * @param filename Path and Filename of target file. NOTE: target file will be
     * treated as a PNG file regardless of extension.
     * @return Reference to newly created texture object. */
    public static int loadTexturePng(GLAutoDrawable drawable, String filename) {
        
        //Initial setup
        GL4 gl = drawable.getGL().getGL4();
        int texture;
        int[] temp = new int[1];
        ByteBuffer buf = null;
        int width = 0;
        int height = 0;
        
        //Allocate space for texture
        gl.glGenTextures(1, temp, 0);
        texture = temp[0];
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texture);
        
        //Decode texture from .png file
        try {
            InputStream in = new FileInputStream(filename);
            PNGDecoder decoder = new PNGDecoder(in);
            
            width = decoder.getWidth();
            height = decoder.getHeight();
            
            buf = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buf, width * 4, Format.RGBA);
            buf.flip();
        } catch (IOException e) {
            System.out.println("============================================\n" +
                    "COULD NOT LOAD TEXTURE: " + filename + "\n" +
                    "This file might be missing/corrupted/moved/renamed\n" +
                    "============================================\n");
            e.printStackTrace();
            System.exit(-1);
        }
        
        //Upload decoded image to Graphics Memory
        gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, width, height, 0, 
                GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, buf);
        
        //Set Texture Parameters
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, 
                GL4.GL_LINEAR);
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, 
                GL4.GL_LINEAR);
        
        //Return Reference to texture object
        return texture;
    }
    
    // ++++ ++++ Managed File I/O ++++ ++++
    
    /**As above, creates a new OpenGL Texture object from a provided .png file. In 
     * addition, this texture will automatically be stored in a list of textures this
     * object has loaded, ensuring the texture isn't duplicated, thus saving video
     * memory.
     * @param drawable OpenGL context needed for access to Graphics Card.
     * @param filename Path and Filename of target file. NOTE: target file will be
     * treated as a PNG file regardless of extension.
     * @return  Reference to newly created texture object. */
    public int loadManagedTexturePng(GLAutoDrawable drawable, String filename) {
        
        if (textures == null) {
            textures = new HashMap<String, Integer>();
        }
        //Check if this texture has already been loaded, in which case just return the
        //existing reference.
        else if (textures.containsKey(filename)) {
            return textures.get(filename);
        }
        
        int texture = loadTexturePng(drawable, filename);
        
        //Add reference to track list.
        textures.put(filename, texture);
        
        return texture;
    }
    
    // ++++ ++++ Disposal ++++ ++++
    
    /**Calls OpenGL to delete a stored texture from memory. Does nothing if there is no
     * texture with the name specified.
     * @param drawable OpenGL context needed for access to Graphics Card.
     * @param filename Filename of the texture to be deleted */
    public void disposeTexture(GLAutoDrawable drawable, String filename) {
        if (textures.containsKey(filename)) {
            textures.remove(filename);
        } //TODO add a logging notification
    }
    
    /**Calls OpenGL to delete all textures referenced by this TextureMaster object, note
     * that this object is still perfectly good to use, it will just have been wiped
     * clean of existing textures.
     * @param drawable Context used to access OpenGL commands. */
    public void flush(GLAutoDrawable drawable) {
        if (textures == null || textures.isEmpty()) {
            return;
        }
        
        GL4 gl = drawable.getGL().getGL4();
        int[] tex = new int[textures.size()];
        Integer[] old = textures.values().toArray(new Integer[tex.length]);
        for (int i = 0; i < tex.length; i++) {
            tex[i] = old[i].intValue();
        }
        
        gl.glDeleteTextures(tex.length, tex, 0);
        textures = null;
    }
}
