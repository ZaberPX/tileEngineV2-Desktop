package px.tileEngineV2.desktop;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

public class FrameControl {
    
    public static void centerFrame(Frame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        
        frame.setLocation(screenSize.width / 2 - frameSize.width / 2, 
                screenSize.height / 2 - frameSize.height / 2);
    }
}
