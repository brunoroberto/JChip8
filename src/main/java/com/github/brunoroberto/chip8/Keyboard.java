package com.github.brunoroberto.chip8;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class Keyboard implements KeyListener {

    private Map<Integer, Integer> mappedKeys = new HashMap<>();
    private Map<Integer, Boolean> pressedStates = new HashMap<>();

    private int countPressedKeys;
    private byte latestPressedKey;

    public Keyboard() {
        this.mappedKeys.put(KeyEvent.VK_1, 0x1);
        this.mappedKeys.put(KeyEvent.VK_NUMPAD1, 0x1);
        this.mappedKeys.put(KeyEvent.VK_2, 0x2);
        this.mappedKeys.put(KeyEvent.VK_NUMPAD2, 0x2);
        this.mappedKeys.put(KeyEvent.VK_3, 0x3);
        this.mappedKeys.put(KeyEvent.VK_NUMPAD3, 0x3);
        this.mappedKeys.put(KeyEvent.VK_C, 0xC);

        this.mappedKeys.put(KeyEvent.VK_4, 0x4);
        this.mappedKeys.put(KeyEvent.VK_NUMPAD4, 0x4);
        this.mappedKeys.put(KeyEvent.VK_5, 0x5);
        this.mappedKeys.put(KeyEvent.VK_NUMPAD5, 0x5);
        this.mappedKeys.put(KeyEvent.VK_6, 0x6);
        this.mappedKeys.put(KeyEvent.VK_NUMPAD6, 0x6);
        this.mappedKeys.put(KeyEvent.VK_D, 0xD);

        this.mappedKeys.put(KeyEvent.VK_7, 0x7);
        this.mappedKeys.put(KeyEvent.VK_NUMPAD7, 0x7);
        this.mappedKeys.put(KeyEvent.VK_8, 0x8);
        this.mappedKeys.put(KeyEvent.VK_NUMPAD8, 0x8);
        this.mappedKeys.put(KeyEvent.VK_9, 0x9);
        this.mappedKeys.put(KeyEvent.VK_NUMPAD9, 0x9);
        this.mappedKeys.put(KeyEvent.VK_E, 0xE);

        this.mappedKeys.put(KeyEvent.VK_A, 0xA);
        this.mappedKeys.put(KeyEvent.VK_0, 0x0);
        this.mappedKeys.put(KeyEvent.VK_NUMPAD0, 0x0);
        this.mappedKeys.put(KeyEvent.VK_B, 0xB);
        this.mappedKeys.put(KeyEvent.VK_F, 0xF);
    }

    private boolean isKeyboardKeyMapped(int keyboardKey) {
        return mappedKeys.containsKey(keyboardKey);
    }

    private int getChip8Key(int keyboardKey) {
        return this.mappedKeys.get(keyboardKey);
    }

    private void setPressedState(int chip8Key, boolean pressed) {
        this.pressedStates.put(chip8Key, pressed);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isKeyboardKeyMapped(e.getKeyCode())) {
            return;
        }
        int chip8Key = getChip8Key(e.getKeyCode());
        setPressedState(chip8Key, true);
        this.latestPressedKey = (byte) chip8Key;
        this.countPressedKeys++;
        System.out.println("Key pressed: " + e.getKeyCode() + " - " + getChip8Key(e.getKeyCode()));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!isKeyboardKeyMapped(e.getKeyCode())) {
            return;
        }
        this.countPressedKeys--;
        setPressedState(getChip8Key(e.getKeyCode()), false);
        System.out.println("Key released: " + e.getKeyCode() + " - " + getChip8Key(e.getKeyCode()));
    }

    public boolean isKeyPressed(int chip8Key) {
        return pressedStates.getOrDefault(chip8Key, false);
    }

    public byte waitForKeyPressed() {
        System.out.println("Waiting for key");
        while (this.countPressedKeys == 0) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return latestPressedKey;
    }

}
