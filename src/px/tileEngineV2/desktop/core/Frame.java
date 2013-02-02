package px.tileEngineV2.desktop.core;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import px.tileEngineV2.core.GameCore;
import px.tileEngineV2.desktop.graphics.Renderer_Desktop;

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
        
        getContentPane().add(glCanvas);
        glCanvas.setPreferredSize(new Dimension(1280, 720));
        
        //Frame setup
        pack();
        setResizable(false);
        removeNotify();
        setUndecorated(true);
        addNotify();
        
        splashScreen = new SplashScreen();
        splashScreen.fpsAnimator = new FPSAnimator(glCanvas, 60);
        glCanvas.addGLEventListener(splashScreen);
        splashScreen.fpsAnimator.start();
        
        //TODO setIconImage
        FrameControl.centerFrame(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    public void setupBorderless() {
        setVisible(false);
        cleanScreen();
        
        DisplayMode currentMode = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDisplayMode();
        Dimension size = new Dimension(currentMode.getWidth(), currentMode.getHeight());
        glCanvas.setPreferredSize(size);
        glCanvas.requestFocusInWindow();
        
        pack();
        setResizable(true);
        removeNotify();
        setUndecorated(true);
        addNotify();
        
        FrameControl.centerFrame(this);
        setVisible(true);
    }
    
    public void setupFullscreen(DisplayMode displayMode) {
        setVisible(false);
        cleanScreen();
        
        //TODO IMPORTANT: Check if the displayMode can actually be displayed by the
        //current graphics device.
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        glCanvas.setPreferredSize(new Dimension(
                displayMode.getWidth(), displayMode.getHeight()));
        //TODO check if fullscreen supported
        device.setFullScreenWindow(this);
        //TODO check if displayMode changing supported
        //device.setDisplayMode(displayMode);
        this.displayMode = displayMode;
        
        pack();
        setResizable(true);
        removeNotify();
        setUndecorated(true);
        addNotify();
        
        setVisible(true);
    }
    
    public void setupWindowed(Dimension windowSize) {
        setVisible(false);
        cleanScreen();
        
        glCanvas.setPreferredSize(windowSize);
        glCanvas.requestFocusInWindow();
        
        pack();
        setResizable(true);
        removeNotify();
        setUndecorated(false);
        addNotify();
        
        FrameControl.centerFrame(this);
        setVisible(true);
    }
    
    /**Removes SplashScreen and remove fullscreen displaymode if any are in use. */
    private void cleanScreen() {
        //TODO Consider instead of default screen device, use preset one in config.
        if (displayMode != null) {
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .setFullScreenWindow(null);
        }
        if (splashScreen != null) {
            getContentPane().remove(glCanvas);
            glCanvas.destroy();
            splashScreen = null;
            
            GLProfile profile = GLProfile.getDefault();
            GLCapabilities capabilities = new GLCapabilities(profile);
            glCanvas = new GLCanvas(capabilities);
            glCanvas.requestFocusInWindow();
            getContentPane().add(glCanvas);
            glCanvas.addGLEventListener(
                    (Renderer_Desktop)GameCore.getInstance().getRenderer());
        }
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    public GLCanvas getCanvas() {
        return glCanvas;
    }
}
