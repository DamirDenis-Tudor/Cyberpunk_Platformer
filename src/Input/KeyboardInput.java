package Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInput implements KeyListener {
    private static KeyboardInput instance;

    private final boolean[] keyCodes;
    private boolean keyW;
    private boolean keyA;
    private boolean keyS;
    private boolean keyD;

    private boolean keyE;
    private boolean space;

    private boolean previousKeyW;
    private boolean previousKeyA;
    private boolean previousKeyS;

    private boolean previousKeyD;

    private boolean previousKeyE;
    private boolean previousSpace;


    private KeyboardInput() {
        keyCodes = new boolean[256];
        keyW = false;
        keyA = false;
        keyS = false;
        keyD = false;
        keyE = false;
        space = false;
        previousSpace = false;
    }

    public static KeyboardInput getInstance() {
        if (instance == null) {
            instance = new KeyboardInput();
        }
        return instance;
    }

    public void updateInputKey() {
        previousSpace = space;
        previousKeyA = keyA;
        previousKeyW = keyW;
        previousKeyS = keyS;
        previousKeyD = keyD;
        previousKeyE = keyE;
        keyW = keyCodes[KeyEvent.VK_W];
        keyA = keyCodes[KeyEvent.VK_A];
        keyS = keyCodes[KeyEvent.VK_S];
        keyD = keyCodes[KeyEvent.VK_D];
        keyE = keyCodes[KeyEvent.VK_E];
        space = keyCodes[KeyEvent.VK_SPACE];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyCodes[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyCodes[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public boolean getKeyW() {
        return keyW;
    }

    public boolean getKeyA() {
        return keyA;
    }

    public boolean getKeyS() {
        return keyS;
    }

    public boolean getKeyD() {
        return keyD;
    }

    public boolean getKeyE() {
        return keyE;
    }


    public boolean getSpace() {
        return space;
    }

    public boolean getPreviousKeyA() {
        return previousKeyA;
    }

    public boolean getPreviousKeyD() {
        return previousKeyD;
    }

    public boolean getPreviousKeyS() {
        return previousKeyS;
    }

    public boolean getPreviousKeyW() {
        return previousKeyW;
    }

    public boolean getPreviousSpace() {
        return previousSpace;
    }
    public boolean isPreviousKeyE() {
        return previousKeyE;
    }


}
