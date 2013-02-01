package px.tileEngineV2.desktop.core;

import java.awt.Dimension;
import java.awt.DisplayMode;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

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
    private SplashScreen splashScreen;
    
    // ++++ ++++ Initialization ++++ ++++
    
    /**Creates a window containing a splash-screen, use setDisplayType() methods to
     * initiate a proper drawing canvas. */
    public Frame() {
        super("Debuggers");
        
        GLProfile profile = GLProfile.getDefault();
        GLCapabilities capabilities = new GLCapabilities(profile);
        glCanvas = new GLCanvas(capabilities);
        glCanvas.requestFocusInWindow();
        
        splashScreen = new SplashScreen();
        splashScreen.fpsAnimator = new FPSAnimator(glCanvas, 60);
        glCanvas.addGLEventListener(splashScreen);
        splashScreen.fpsAnimator.start();
        
        getContentPane().add(glCanvas);
        glCanvas.setPreferredSize(new Dimension(1280, 720));
        
        //Frame setup
        pack();
        setResizable(false);
        removeNotify();
        setUndecorated(true);
        addNotify();
        
        //TODO setIconImage
        FrameControl.centerFrame(this);
        setVisible(true);
    }
}
