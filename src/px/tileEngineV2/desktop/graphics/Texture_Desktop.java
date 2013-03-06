package px.tileEngineV2.desktop.graphics;

import px.tileEngineV2.core.GameCore;
import px.tileEngineV2.graphics.Texture;
import px.tileEngineV2.graphics.TextureCache;

public class Texture_Desktop extends Texture {
    
    // ++++ ++++ Data ++++ ++++
    
    private int glTexture;
    
    // ++++ ++++ Initialization ++++ ++++
    
    public Texture_Desktop(TextureCache cache, String filename) {
        super(cache, filename);
        if (cache != null) {
            glTexture = ((TextureLoader)cache).loadManagedTexturePng(
                    ((Renderer_Desktop)GameCore.getInstance().getRenderer())
                    .getAutoDrawable(), filename);
        } else {
            glTexture = TextureLoader.loadTexturePng(
                    ((Renderer_Desktop)GameCore.getInstance().getRenderer())
                    .getAutoDrawable(), filename);
        }
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    public int getGLTexture() {
        return glTexture;
    }
}
