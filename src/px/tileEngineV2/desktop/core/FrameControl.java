package px.tileEngineV2.desktop.core;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

public class FrameControl {
    
    public static void centerFrame(Frame frame) {
        Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getCenterPoint();
        Dimension frameSize = frame.getSize();
        
        frame.setLocation(centerPoint.x - frameSize.width / 2, 
                centerPoint.y - frameSize.height / 2);
    }
}
