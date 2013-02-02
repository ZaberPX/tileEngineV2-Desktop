package px.tileEngineV2.desktop.graphics;

import px.tileEngineV2.core.GameCore;
import px.tileEngineV2.graphics.TextureCache;

public class Texture_Desktop {
    
    // ++++ ++++ Data ++++ ++++
    
    private int glTexture;
    
    // ++++ ++++ Initialization ++++ ++++
    
    public Texture_Desktop(TextureCache cache, String filename) {
        glTexture = ((TextureLoader)cache).loadManagedTexturePng(
                ((Renderer_Desktop)GameCore.getInstance().getRenderer())
                .getAutoDrawable(), filename);
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    public int getGLTexture() {
        return glTexture;
    }
}
