package com.github.brunoroberto.chip8;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class Screen extends JPanel {

    private List<ScreenMemory.ActivePixel> activePixels = new ArrayList<>();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.clearRect(0, 0, this.getWidth(), this.getHeight());

        // draw the pixels
        graphics2D.setColor(Color.WHITE);
        this.activePixels.forEach(activePixel -> {
            graphics2D.fillRect(activePixel.getX(), activePixel.getY(), activePixel.getSize(), activePixel.getSize());
        });
    }

    public void draw(List<ScreenMemory.ActivePixel> activePixels) {
        this.activePixels = activePixels;
        repaint();
    }
}
