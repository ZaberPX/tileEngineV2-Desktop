package px.tileEngineV2.desktop.core;

import px.tileEngineV2.core.GameCore;

public class GameCore_Desktop extends GameCore {
    
    // ++++ ++++ Data ++++ ++++
    
    private Frame frame;
    
    // ++++ ++++ Accessors ++++ ++++
    
    public Frame getFrame() {
        return frame;
    }
    
    // ++++ ++++ Mutators ++++ ++++
    
    public void setFrame(Frame frame) {
        this.frame = frame;
    }
}
