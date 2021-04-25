package com.github.brunoroberto.chip8;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application extends JFrame {

    private static final String APPLICATION_NAME = "JCHIP-8";

    private final Screen screen;
    private final Dimension screenSize;

    private Application() {
        super(APPLICATION_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        screenSize = toolkit.getScreenSize();
        setSize(screenSize);

        screen = new Screen();
        screen.setBackground(Color.BLACK);
        this.setBackground(Color.BLACK);
        add(screen);

        setVisible(true);
    }

    public void start() {
        try {
            Path romPath = selectRomFile();
            byte[] rom = Files.readAllBytes(romPath);

            ScreenMemory screenMemory = new ScreenMemory(screenSize.getHeight());

            Keyboard keyboard = new Keyboard();
            this.addKeyListener(keyboard);

            Chip8 chip8 = new Chip8(keyboard, screen, screenMemory);
            chip8.execute(rom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Path selectRomFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.removeChoosableFileFilter(fileChooser.getChoosableFileFilters()[0]);
        fileChooser.addChoosableFileFilter(new RomFileFilter());
        int state = fileChooser.showOpenDialog(this);
        if (state != JFileChooser.APPROVE_OPTION) {
            System.out.println("No ROM file selected");
            System.exit(-1);
        }
        return fileChooser.getSelectedFile().toPath();
    }

    public static void main(String[] args) {
        new Application().start();
    }

}
