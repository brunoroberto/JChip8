package com.github.brunoroberto.chip8;

import com.github.brunoroberto.chip8.excep.InvalidHexOperation;
import com.github.brunoroberto.chip8.store.Memory;
import com.github.brunoroberto.chip8.store.Stack;

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

	private boolean trace = Boolean.parseBoolean(System.getProperty("trace"));

	private Memory memory;

	private Stack stack;

	private Display display;

	// registers
	private byte[] V; // V0 to VF registers

	private short I;

	private byte DT; // delay timer register
	private byte ST; // sound timer register

	private short PC; // program counter register

	private int cycle;

	private int frequency;

	private final short fontSprites[] = { 0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
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

	public Chip8(byte[] rom, Options opts) {
		init(rom, opts);
	}

	public Chip8(byte[] rom) {
		init(rom, new Options());
	}

	private void init(byte[] rom, Options opts) {
		this.PC = opts.getStartAddress();
		this.memory = new Memory(opts.getMemorySize());
		this.stack = new Stack(opts.getStackSize());
		this.display = new Display(opts.getDisplaySize());
		this.cycle = opts.getTickCycle();
		this.frequency = opts.getCpuFrequency();
		loadFontSprites();
		loadRom(rom);
	}

	private void loadFontSprites() {
		for (int i = 0; i < this.fontSprites.length; i++) {
			this.memory.writeWord(i, (byte) fontSprites[i]);
		}
	}

	private void loadRom(byte[] rom) {
		short addr = this.PC;
		for (byte b : rom) {
			this.memory.writeWord(addr++, b);
		}
	}

	public void execute() throws InterruptedException {
		while (true) {
			int cycle = this.cycle;
			do {
				executeOperation();
				cycle--;
			} while (cycle > 0);
			Thread.sleep(this.frequency);
		}
	}

	/**
	 * Execute the hex operations of CHIP-8
	 */
	private void executeOperation() {
		short opCode = (short) (this.memory.getWord(this.PC) << 8 | this.memory.getWord(this.PC + 1));
		byte type = (byte) ((opCode & 0xF000) >> 12);

		this.PC += 2;

		try {
			switch (type) {
			case 0x0:
				opType0x0(opCode);
				break;
			case 0x1:
				opType0x1(opCode);
				break;
			case 0x2:
				opType0x2(opCode);
				break;
			case 0x3:
				opType0x3(opCode);
				break;
			case 0x4:
				opType0x4(opCode);
				break;
			case 0x5:
				opType0x5(opCode);
				break;
			case 0x6:
				opType0x6(opCode);
				break;
			case 0x7:
				opType0x7(opCode);
				break;
			case 0x8:
				opType0x8(opCode);
				break;
			case 0x9:
				opType0x9(opCode);
				break;
			case 0xA:
				opType0xA(opCode);
				break;
			case 0xB:
				opType0xB(opCode);
				break;
			case 0xC:
				opType0xC(opCode);
				break;
			case 0xD:
				opType0xD(opCode);
				break;
			case 0xE:
				opType0xE(opCode);
				break;
			case 0xF:
				opType0xF(opCode);
				break;
			default:
				throw new InvalidHexOperation(String.format("Invalid hex operation: %x", opCode));
			}

		} catch (InvalidHexOperation e) {
			if (this.trace) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 0x0 operation type
	 * 
	 * @param opCode
	 */
	private void opType0x0(short opCode) {
		switch (opCode) {
		// CLS - clear the display
		case 0x00E0:
			this.display.clear();
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
	private void opType0x1(short opCode) {
		short nnn = getNNN(opCode);
		this.PC = nnn;
	}

	/**
	 * 2nnn - Call subroutine at NNN The interpreter increments the stack pointer,
	 * then puts the current PC on the top of the stack. The PC is then set to nnn.
	 * 
	 * @param opCode
	 */
	private void opType0x2(short opCode) {
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
	private void opType0x3(short opCode) {
		short x = getX(opCode);
		short nn = getNN(opCode);
		if (this.V[x] == nn)
			this.PC += 2;
	}

	/**
	 * 4xkk - SNE Vx, byte Skip next instruction if Vx != kk The interpreter
	 * compares register Vx to kk, and if they are not equal, increments the program
	 * counter by 2.
	 * 
	 * @param opCode
	 */
	private void opType0x4(short opCode) {
		short x = getX(opCode);
		short nn = getNN(opCode);
		if (this.V[x] != nn)
			this.PC += 2;
	}

	/**
	 * 5xy0 - SE Vx, Vy Skip next instruction if Vx = Vy. The interpreter compares
	 * register Vx to register Vy, and if they are equal, increments the program
	 * counter by 2.
	 * 
	 * @param opCode
	 * @throws InvalidHexOperation
	 */
	private void opType0x5(short opCode) throws InvalidHexOperation {
		short n = getN(opCode);
		if (n != 0)
			throw new InvalidHexOperation(String.format("Invalid Operation: %x", opCode));
		short x = getX(opCode);
		short y = getY(opCode);
		if (this.V[x] == this.V[y])
			this.PC += 2;
	}

	/**
	 * 6xkk - LD Vx, byte. Set Vx = kk. The interpreter puts the value kk into
	 * register Vx.
	 * 
	 * @param opCode
	 */
	private void opType0x6(short opCode) {
		short x = getX(opCode);
		this.V[x] = (byte) getNN(opCode);
	}

	/**
	 * 7xkk - ADD Vx, byte. Set Vx = Vx + kk. Adds the value kk to the value of
	 * register Vx, then stores the result in Vx.
	 * 
	 * @param opCode
	 */
	private void opType0x7(short opCode) {
		short x = getX(opCode);
		this.V[x] += (byte) getNN(opCode);
	}

	/**
	 * 0x8 type operations
	 * 
	 * @param opCode
	 * @throws InvalidHexOperation
	 */
	private void opType0x8(short opCode) throws InvalidHexOperation {
		short x = getX(opCode);
		short y = getY(opCode);
		short n = getN(opCode);
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
			int sum = this.V[x] + this.V[y];
			if (sum > 0xFF)
				this.V[0xF] = 1;
			else
				this.V[0xF] = 0;
			this.V[x] = (byte) (sum & 0xFF);
			break;
		// 8xy5 - SUB Vx, Vy
		// Set Vx = Vx - Vy, set VF = NOT borrow.
		// If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx,
		// and the results stored in Vx.
		case 0x5:
			if (this.V[x] > this.V[y])
				this.V[0xF] = 1;
			else
				this.V[0xF] = 0;
			this.V[x] -= this.V[y];
			break;
		// 8xy6 - SHR Vx {, Vy}
		// Set Vx = Vx SHR 1.
		// If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0.
		// Then Vx is divided by 2.
		case 0x6:
			this.V[0xF] = (byte) (this.V[x] & 0x1);
			this.V[x] /= 2;
			break;
		// 8xy7 - SUBN Vx, Vy
		// Set Vx = Vy - Vx, set VF = NOT borrow.
		// If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy,
		// and the results stored in Vx.
		case 0x7:
			if (this.V[y] > this.V[x])
				this.V[0xF] = 1;
			else
				this.V[0xF] = 0;
			this.V[x] = (byte) (this.V[y] - this.V[x]);
			break;
		// 8xyE - SHL Vx {, Vy}
		// Set Vx = Vx SHL 1.
		// If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0.
		// Then Vx is multiplied by 2.
		case 0xE:
			if (((this.V[x] & 0x80) >> 7) == 1)
				this.V[0xF] = 1;
			else
				this.V[0xF] = 0;
			this.V[x] *= 2;
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
	private void opType0x9(short opCode) throws InvalidHexOperation {
		short n = getN(opCode);
		if (n != 0)
			throw new InvalidHexOperation(String.format("Invalid Operation: %x", opCode));
		short x = getX(opCode);
		short y = getY(opCode);
		if (this.V[x] != this.V[y])
			this.PC += 2;
	}

	/**
	 * Annn - LD I, addr - Set I = nnn. The value of register I is set to nnn.
	 * 
	 * @param opCode
	 */
	private void opType0xA(short opCode) {
		short nnn = getNNN(opCode);
		this.I = nnn;
	}

	/**
	 * Bnnn - JP V0, addr - Jump to location nnn + V0. The program counter is set to
	 * nnn plus the value of V0.
	 * 
	 * @param opCode
	 */
	private void opType0xB(short opCode) {
		short nnn = getNNN(opCode);
		this.PC = (short) (nnn + this.V[0]);
	}

	/**
	 * Cxkk - RND Vx, byte Set Vx = random byte AND kk.
	 * 
	 * The interpreter generates a random number from 0 to 255, which is then ANDed
	 * with the value kk. The results are stored in Vx. See instruction 8xy2 for
	 * more information on AND.
	 * 
	 * @param opCode
	 */
	private void opType0xC(short opCode) {
		short x = getX(opCode);
		short kk = getNN(opCode);
		int random = ((int) Math.random() * 256);
		this.V[x] = (byte) (random & kk);
	}

	/**
	 * Dxyn - DRW Vx, Vy, nibble - Display n-byte sprite starting at memory location
	 * I at (Vx, Vy), set VF = collision.
	 * 
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
	private void opType0xD(short opCode) {
	}

	private void opType0xE(short opCode) {
	}

	private void opType0xF(short opCode) {
	}

	private short getNNN(short opCode) {
		return (short) (opCode & 0x0FFF);
	}

	private short getNN(short opCode) {
		return (short) (opCode & 0x00FF);
	}

	private short getN(short opCode) {
		return (short) (opCode & 0x000F);
	}

	private short getX(short opCode) {
		return (short) ((opCode & 0x0F00) >> 8);
	}

	private short getY(short opCode) {
		return (short) ((opCode & 0x00F0) >> 4);
	}
}
