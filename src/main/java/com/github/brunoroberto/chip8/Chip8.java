package com.github.brunoroberto.chip8;

import java.util.Arrays;

/**
 * <p>
 * CHIP-8 Interpreter
 * </p>
 *
 * <p>
 * CHIP-8 is an interpreted programming language, developed by Joseph
 * Weisbecker. It was initially used on the COSMAC VIP and Telmac 1800 8-bit
 * microcomputers in the mid-1970s - Wikipedia
 * </p>
 *
 * <p>
 * References:
 * </p>
 * <ul>
 * <li>https://en.wikipedia.org/wiki/CHIP-8</li>
 * <li>http://devernay.free.fr/hacks/chip8/C8TECH10.HTM - by Thomas P.
 * Greene</li>
 * <li>http://mattmik.com/files/chip8/mastering/chip8.html - By Matthew
 * Mikolay</li>
 * </ul>
 *
 * @author brunoroberto
 */
public class Chip8 {

    private static final int START_ADDRESS = 0x200; // 512
    private static final int CYCLE_PER_TICK = 10;
    private static final int V_REGISTER_SIZE = 16;
    private static final int CPU_FREQUENCY = 1000 / 65; // 60Hz

    private Memory memory;
    private Stack stack;
    private final ScreenMemory screenMemory;

    // registers
    private byte[] V;  // V0 to VF registers

    private short I;

    private byte delayTimer; // delay timer register
    private byte soundTimer; // sound timer register

    private short PC; // program counter register

    private final int fontSprites[] = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80 // F
    };

    private boolean shouldDraw = false;
    private boolean running = true;

    private final Screen screen;
    private final Keyboard keyboard;

    public Chip8(Keyboard keyboard, Screen canvas, ScreenMemory screenMemory) {
        this.keyboard = keyboard;
        this.screen = canvas;
        this.screenMemory = screenMemory;
        this.I = 0;
        this.PC = START_ADDRESS;
        this.V = new byte[V_REGISTER_SIZE];
        this.memory = new Memory();
        this.stack = new Stack();
        loadFontSprites();
    }

    private void loadFontSprites() {
        for (int i = 0; i < this.fontSprites.length; i++) {
            this.memory.writeWord(i, (byte) fontSprites[i]);
        }
    }

    private void loadRomIntoMemory(byte[] rom) {
        if (rom != null) {
            short address = this.PC;
            for (byte b : rom) {
                this.memory.writeWord(address++, b);
            }
        }
    }

    public void execute(byte[] rom) throws InterruptedException {
        loadRomIntoMemory(rom);
        while (running) {
            executeCycle();
            Thread.sleep(CPU_FREQUENCY);
        }
    }

    private void executeCycle() {
        int cycle = CYCLE_PER_TICK;
        while (cycle > 0) {
            executeOperation();
            updateScreenIfNecessary();
            decrementDelayTimer();
            handleSound();
            cycle--;
        }
    }

    private void updateScreenIfNecessary() {
        if (shouldDraw) {
            shouldDraw = false;
            this.screen.draw(this.screenMemory.getActivePixels());
        }
    }

    private void decrementDelayTimer() {
        if (this.delayTimer > 0) {
            this.delayTimer--;
        }
    }

    private void handleSound() {
        // TODO: implement sound operation
    }

    private void dump(short opCode) {
        System.out.println("========== DUMP ==========");
        System.out.println(String.format("PC: %d - 0x%04X", this.PC, this.PC));
        System.out.println(String.format("I: %d - 0x%04X", this.I, this.I));
        System.out.println(String.format("x: 0x%04X", getX(opCode)));
        System.out.println(String.format("y: 0x%04X", getY(opCode)));
        System.out.println(String.format("n: 0x%04X", getN(opCode)));
        System.out.println(String.format("kk: 0x%04X", getKK(opCode)));
        System.out.println(String.format("nnn: 0x%04X", getNNN(opCode)));
        System.out.println("V: " + Arrays.toString(this.V));
        this.stack.dump();
        this.memory.dump();
        System.out.println();
    }

    /**
     * Execute the hex operations of CHIP-8
     */
    private void executeOperation() {
        short opCode = (short) ((short) (this.memory.getWord(this.PC) << 8) | (this.memory.getWord(this.PC + 1) & 0x00FF));
        int type = (opCode & 0xF000);

        System.out.println(String.format("Instruction: 0x%04X", opCode));
        System.out.println(String.format("Sub-type: 0x%04X", type));

        dump(opCode);

        this.PC += 2;

        try {
            switch (type) {
                case 0x0000:
                    opType0x0(opCode);
                    break;
                case 0x1000:
                    opType0x1(opCode);
                    break;
                case 0x2000:
                    opType0x2(opCode);
                    break;
                case 0x3000:
                    opType0x3(opCode);
                    break;
                case 0x4000:
                    opType0x4(opCode);
                    break;
                case 0x5000:
                    opType0x5(opCode);
                    break;
                case 0x6000:
                    opType0x6(opCode);
                    break;
                case 0x7000:
                    opType0x7(opCode);
                    break;
                case 0x8000:
                    opType0x8(opCode);
                    break;
                case 0x9000:
                    opType0x9(opCode);
                    break;
                case 0xA000:
                    opType0xA(opCode);
                    break;
                case 0xB000:
                    opType0xB(opCode);
                    break;
                case 0xC000:
                    opType0xC(opCode);
                    break;
                case 0xD000:
                    opType0xD(opCode);
                    break;
                case 0xE000:
                    opType0xE(opCode);
                    break;
                case 0xF000:
                    opType0xF(opCode);
                    break;
                default:
                    throw new InvalidHexOperation(String.format("Invalid hex operation: 0x%04X", opCode));
            }

            dump(opCode);

        } catch (InvalidHexOperation e) {
            e.printStackTrace();
        }
    }

    /**
     * 0x0 operation type
     *
     * @param opCode 16bit operation code
     */
    private void opType0x0(int opCode) {
        switch (opCode) {
            // CLS - clear the display
            case 0x00E0:
                this.screenMemory.clear();
                break;
            // Return - Return from a subroutine. The interpreter sets the program counter
            // to the address at the top of the stack, then subtracts 1 from the stack
            // pointer.
            case 0x00EE:
                this.PC = this.stack.pop();
                break;
            // 0x0NNN - SYS addr
            // Jump to a machine code routine at nnn. this instruction is only used on the
            // old computers on which Chip-8 was originally implemented. It is ignored by
            // modern interpreters.
            default:
                break;
        }
    }

    /**
     * 1nnn - Jump to location nnn. The interpreter sets the program counter to nnn
     *
     * @param opCode
     */
    private void opType0x1(int opCode) {
        short nnn = getNNN(opCode);
        this.PC = nnn;
    }

    /**
     * 2nnn - Call subroutine at NNN The interpreter increments the stack pointer,
     * then puts the current PC on the top of the stack. The PC is then set to nnn.
     *
     * @param opCode
     */
    private void opType0x2(int opCode) {
        short nnn = getNNN(opCode);
        this.stack.put(this.PC);
        this.PC = nnn;
    }

    /**
     * 3xkk - SE Vx, byte - Skip next instruction if Vx = kk. The interpreter
     * compares register Vx to kk, and if they are equal, increments the program
     * counter by 2.
     *
     * @param opCode
     */
    private void opType0x3(int opCode) {
        byte x = getX(opCode);
        byte kk = getKK(opCode);
        if (this.V[x] == kk) {
            this.PC += 2;
        }
    }

    /**
     * 4xkk - SNE Vx, byte Skip next instruction if Vx != kk The interpreter
     * compares register Vx to kk, and if they are not equal, increments the program
     * counter by 2.
     *
     * @param opCode
     */
    private void opType0x4(int opCode) {
        int x = getX(opCode);
        int kk = getKK(opCode);
        if (this.V[x] != kk) {
            this.PC += 2;
        }
    }

    /**
     * 5xy0 - SE Vx, Vy Skip next instruction if Vx = Vy. The interpreter compares
     * register Vx to register Vy, and if they are equal, increments the program
     * counter by 2.
     *
     * @param opCode
     * @throws InvalidHexOperation
     */
    private void opType0x5(int opCode) throws InvalidHexOperation {
        byte x = getX(opCode);
        byte y = getY(opCode);
        if (this.V[x] == this.V[y]) {
            this.PC += 2;
        }
    }

    /**
     * 6xkk - LD Vx, byte. Set Vx = kk. The interpreter puts the value kk into
     * register Vx.
     *
     * @param opCode
     */
    private void opType0x6(int opCode) {
        byte x = getX(opCode);
        this.V[x] = getKK(opCode);
    }

    /**
     * 7xkk - ADD Vx, byte. Set Vx = Vx + kk. Adds the value kk to the value of
     * register Vx, then stores the result in Vx.
     *
     * @param opCode
     */
    private void opType0x7(int opCode) {
        byte x = getX(opCode);
        byte kk = getKK(opCode);
        this.V[x] += kk;
    }

    /**
     * 0x8 type operations
     *
     * @param opCode
     * @throws InvalidHexOperation
     */
    private void opType0x8(int opCode) throws InvalidHexOperation {
        byte x = getX(opCode);
        byte y = getY(opCode);
        byte n = getN(opCode);
        switch (n) {
            // 8xy0 - LD Vx, Vy Set Vx = Vy.
            // Stores the value of register Vy in register Vx.
            case 0x0:
                this.V[x] = this.V[y];
                break;
            // 8xy1 - OR Vx, Vy
            // Set Vx = Vx OR Vy. Performs a bitwise OR on the values of Vx and Vy, then
            // stores the result in Vx. A bitwise OR compares the corrseponding bits from
            // two values, and if either bit is 1, then the same bit in the result is also
            // 1. Otherwise, it is 0.
            case 0x1:
                this.V[x] |= this.V[y];
                break;
            // 8xy2 - AND Vx, Vy
            // Set Vx = Vx AND Vy. Performs a bitwise AND on the values of Vx and Vy, then
            // stores the result in Vx. A bitwise AND compares the corrseponding bits from
            // two values, and if both bits are 1, then the same bit in the result is also
            // 1. Otherwise, it is 0.
            case 0x2:
                this.V[x] &= this.V[y];
                break;
            // 8xy3 - XOR Vx, Vy
            // Set Vx = Vx XOR Vy. Performs a bitwise exclusive OR on the values of Vx and
            // Vy, then stores the result in Vx. An exclusive OR compares the corrseponding
            // bits from two values, and if the bits are not both the same, then the
            // corresponding bit in the result is set to 1. Otherwise, it is 0.
            case 0x3:
                this.V[x] ^= this.V[y];
                break;
            // 8xy4 - ADD Vx, Vy
            // Set Vx = Vx + Vy, set VF = carry. The values of Vx and Vy are added together.
            // If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise
            // 0. Only the lowest 8 bits of the result are kept, and stored in Vx.
            case 0x4:
                int sum = (this.V[x] + this.V[y]);
                this.V[0xF] = 0;
                if (sum > 0xFF) { // sum > 255
                    this.V[0xF] = 1;
                }
                this.V[x] = (byte) sum;
                break;
            // 8xy5 - SUB Vx, Vy
            // Set Vx = Vx - Vy, set VF = NOT borrow.
            // If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx,
            // and the results stored in Vx.
            case 0x5:
                this.V[0xF] = 0;
                if (this.V[x] > this.V[y]) {
                    this.V[0xF] = 1;
                }
                this.V[x] -= this.V[y];
                break;
            // 8xy6 - SHR Vx {, Vy}
            // Set Vx = Vx SHR 1.
            // If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0.
            // Then Vx is divided by 2.
            case 0x6:
                this.V[0xF] = (byte) (this.V[x] & 0xFE);
                this.V[x] >>= 1;
//                byte leastSignificant = (byte)(this.V[x] & (byte)0x01);
//                this.V[0xF] = leastSignificant; //Set VF to the least significant bit of Vx before the shift.
//
//                //We have to cast it to unsigned int to work properly. If we don't do it, Bitwise operation does the cast
//                //with sign, so the result is incorrect.
//                int int_vx = (this.V[x]&0xFF);
//                this.V[x] = (byte) (int_vx >>> 1); // >>> operator means right shift one bit without sign propagation.
                break;
            // 8xy7 - SUBN Vx, Vy
            // Set Vx = Vy - Vx, set VF = NOT borrow.
            // If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy,
            // and the results stored in Vx.
            case 0x7:
                this.V[0xF] = 0;
                if (this.V[y] > this.V[x]) {
                    this.V[0xF] = 1;
                }
                this.V[x] = (byte) (this.V[y] - this.V[x]);
                break;
            // 8xyE - SHL Vx {, Vy}
            // Set Vx = Vx SHL 1.
            // If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0.
            // Then Vx is multiplied by 2.
            case 0xE:
                this.V[0xF] = (byte) ((this.V[x] & 0x7F) >> 7);
                this.V[x] <<= 1;
                break;
        }
    }

    /**
     * 9xy0 - SNE Vx, Vy - Skip next instruction if Vx != Vy. The values of Vx and
     * Vy are compared, and if they are not equal, the program counter is increased
     * by 2.
     *
     * @param opCode
     * @throws InvalidHexOperation
     */
    private void opType0x9(int opCode) {
        byte x = getX(opCode);
        byte y = getY(opCode);
        if (this.V[x] != this.V[y]) {
            this.PC += 2;
        }
    }

    /**
     * Annn - LD I, addr - Set I = nnn. The value of register I is set to nnn.
     *
     * @param opCode
     */
    private void opType0xA(int opCode) {
        short nnn = getNNN(opCode);
        this.I = nnn;
    }

    /**
     * Bnnn - JP V0, addr - Jump to location nnn + V0. The program counter is set to
     * nnn plus the value of V0.
     *
     * @param opCode
     */
    private void opType0xB(int opCode) {
        short nnn = getNNN(opCode);
        this.PC = (short) (nnn + this.V[0]);
    }

    /**
     * Cxkk - RND Vx, byte Set Vx = random byte AND kk.
     * <p>
     * The interpreter generates a random number from 0 to 255, which is then ANDed
     * with the value kk. The results are stored in Vx. See instruction 8xy2 for
     * more information on AND.
     *
     * @param opCode
     */
    private void opType0xC(int opCode) {
        byte x = getX(opCode);
        byte kk = getKK(opCode);
        byte random = (byte) (Math.random() * 256);
        this.V[x] = (byte) (random & kk);
    }

    /**
     * Dxyn - DRW Vx, Vy, nibble - Display n-byte sprite starting at memory location
     * I at (Vx, Vy), set VF = collision.
     * <p>
     * The interpreter reads n bytes from memory, starting at the address stored in
     * I. These bytes are then displayed as sprites on screen at coordinates (Vx,
     * Vy). Sprites are XORed onto the existing screen. If this causes any pixels to
     * be erased, VF is set to 1, otherwise it is set to 0. If the sprite is
     * positioned so part of it is outside the coordinates of the display, it wraps
     * around to the opposite side of the screen. See instruction 8xy3 for more
     * information on XOR, and section 2.4, Display, for more information on the
     * Chip-8 screen and sprites.
     *
     * @param opCode
     */
    private void opType0xD(int opCode) {
        byte x = getX(opCode);
        byte y = getY(opCode);
        byte n = getN(opCode);

        this.V[0xF] = 0;

        for (int row = 0; row < n; row++) {
            int sprite = Byte.toUnsignedInt(this.memory.getWord(this.I + row));
//            int sprite = this.memory.getWord(this.I + row);
            for (int col = 0; col < 8; col++) {
                if ((sprite & 0x80) > 0) {
                    if (this.screenMemory.setPixel(this.V[x] + col, this.V[y] + row)) {
                        this.V[0xF] = 1;
                    }
                }
                sprite <<= 1;
            }
        }
        this.shouldDraw = true;
    }

    private void opType0xE(int opCode) {
        byte x = getX(opCode);
        byte kk = getKK(opCode);

        switch (kk) {
            case (byte) 0x009E:
                if (this.keyboard.isKeyPressed(this.V[x])) {
                    this.PC += 2;
                }
                break;
            case (byte) 0x00A1:
                if (!this.keyboard.isKeyPressed(this.V[x])) {
                    this.PC += 2;
                }
                break;
        }
    }

    private void opType0xF(int opCode) {
        byte x = getX(opCode);
        byte kk = getKK(opCode);
        switch (kk) {
            case 0x07:
                this.V[x] = this.delayTimer;
                break;
            case 0x0A:
                // keyboard
                byte keyPressed = this.keyboard.waitForKeyPressed();
                this.V[x] = keyPressed;
                break;
            case 0x15:
                this.delayTimer = this.V[x];
                break;
            case 0x18:
                this.soundTimer = this.V[x];
                break;
            case 0x1E:
                this.I += this.V[x];
                break;
            case 0x29:
                this.I = (short) (this.V[x] * 5);
                break;
            case 0x33:
                this.memory.writeWord(this.I, (byte) (this.V[x] / 100)); // LD B, Vx
                this.memory.writeWord(this.I + 1, (byte) ((this.V[x] % 100) / 10));
                this.memory.writeWord(this.I + 2, (byte) (this.V[x] % 10));
                break;
            case 0x55:
                for (int i = 0; i <= x; i++) {
                    this.memory.writeWord(this.I + i, this.V[i]);
                }
                break;
            case 0x65:
                for (int i = 0; i <= x; i++) {
                    this.V[i] = this.memory.getWord(this.I + i);
                }
                break;
            default:
                throw new InvalidHexOperation(String.format("Invalid hex operation: 0x%04X", opCode));
        }
    }

    private short getNNN(int opCode) {
        return (short) (opCode & 0x0FFF);
    }

    private byte getKK(int opCode) {
        return (byte) (opCode & 0x00FF);
    }

    private byte getN(int opCode) {
        return (byte) (opCode & 0x000F);
    }

    private byte getX(int opCode) {
        return (byte) ((opCode & 0x0F00) >>> 8);
    }

    private byte getY(int opCode) {
        return (byte) ((opCode & 0x00F0) >>> 4);
    }
}
