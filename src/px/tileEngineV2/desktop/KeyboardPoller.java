package px.tileEngineV2.desktop;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**TODO Class Description and all Methods
 * <br>
 * Based on:http://gpsnippets.blogspot.com.au/2008/03/keyboard-input-polling-system-in-java.html
 * @author Michael Stopa */
public class KeyboardPoller implements KeyListener {
    
    // ++++ ++++ Constants ++++ ++++
    
    public static final int MAX_KEY_VALUE = 525;
    
    // ++++ ++++ Data ++++ ++++
    
    private static KeyboardPoller instance = null;
    
    private int[] keys = new int[MAX_KEY_VALUE];
    private String keyCache = "";
    
    private boolean[] keyStateUp = new boolean[MAX_KEY_VALUE];
    private boolean[] keyStateDown = new boolean[MAX_KEY_VALUE];
    
    private boolean keyPressed = false;
    private boolean keyReleased = false;
    
    // ++++ ++++ Initialization ++++ ++++
    
    /**Empty Constructor, doesn't actually do anything. */
    protected KeyboardPoller() {
        
    }
    
    // ++++ ++++ Game Logic ++++ ++++
    
    /**Updates the state of the KeyboardPoller.
     * <br>
     * Only resets keyStateUp because you don't want keys to be showing as up forever
     * which is what will happen if unless the array is cleared. */
    public void update() {
        //clear out key up states
        keyStateUp = new boolean[MAX_KEY_VALUE];
        keyReleased = false;
        if (keyCache.length() > 1024) {
            keyCache = "";
        }
    }
    /**Returns true if the key (0-{@link KEY_MAX_VALUE}) is being pressed. Use the 
     * keycodes from {@link KeyEvent} to check specific keys.
     * @param key KeyEvent.VK_Key code of the key being checked.
     * @return True if the specified key is currently being pressed. */
    public boolean isKeyDown(int key) {
        return keyStateDown[key];
    }
    
    /**Returns true if the key (0-{@link KEY_MAX_VALUE}) is not being pressed. Use the 
     * keycodes from {@link KeyEvent} to check specific keys.
     * @param key KeyEvent.VK_Key code of the key being checked.
     * @return True if the specified key is currently not being pressed. */
    public boolean isKeyUp(int key) {
        return keyStateUp[key];
    }
    
    /**A check to see if any key has been pressed at all in the last update cycle.
     * @return True if one or more keys have been pressed since the last call to 
     * {@link KeyboardPoller#update()}. */
    public boolean isAnyKeyDown() {
        return keyPressed;
    }
    
    /**A check to see if any key has been released at all in the last update cycle.
     * @return True if one or more keys have been released since the last call to
     * {@link KeyboardPoller#update()}. */
    public boolean isAnyKeyUp() {
        return keyReleased;
    }
    
    // ++++ ++++ Event Handling ++++ ++++

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("KeyboardPoller: A key has been pressed, code: " + e.getKeyCode());
        if (e.getKeyCode() >= 0 && e.getKeyCode() < MAX_KEY_VALUE) {
            keys[e.getKeyCode()] = (int) System.currentTimeMillis();
            keyStateDown[e.getKeyCode()] = true;
            keyStateUp[e.getKeyCode()] = false;
            keyPressed = true;
            keyReleased = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("KeyboardPoller: A key has been released code: " + e.getKeyCode());
        keys[e.getKeyCode()] = 0;
        keyStateUp[e.getKeyCode()] = true;
        keyStateDown[e.getKeyCode()] = false;
        keyPressed = false;
        keyReleased = true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        keyCache += e.getKeyChar();
    }
    
    // ++++ ++++ Accessors ++++ ++++
    
    /**Returns a reference to the current KeyboardPoller singleton instance, use this 
     * method instead of manually constructing a new instance. Automatically creates an 
     * instance of KeyboardPoller if one does not currently exist.
     * @return The currently initialized instance of KeyboardPoller. */
    public static KeyboardPoller getInstance() {
        if (instance == null) {
            instance = new KeyboardPoller();
        }
        
        return instance;
    }
}

