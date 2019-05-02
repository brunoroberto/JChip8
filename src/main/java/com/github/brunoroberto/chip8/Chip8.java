package com.github.brunoroberto.chip8;

import com.github.brunoroberto.chip8.register.Register16;
import com.github.brunoroberto.chip8.register.Register8;
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

	private static final short DEFAULT_PC_START = 0x200;

	private byte[] rom;

	private Memory memory;
	private Stack stack;

	// registers
	private Register8[] V; // V0 to VF registers

	private Register16 I;

	private Register8 DT; // delay timer register
	private Register8 ST; // sound timer register

	private Register16 PC; // program counter register

	public Chip8(byte[] rom, short startAddr, int memorySize, int stackSize) {
		init(rom, startAddr, memorySize, stackSize);
	}

	public Chip8(byte[] rom, short startAddr) {
		init(rom, startAddr, -1, -1);
	}

	public Chip8(byte[] rom) {
		init(rom, DEFAULT_PC_START, -1, -1);
	}

	private void init(byte[] rom, short startAddr, int memorySize, int stackSize) {
		this.rom = rom;
		this.PC.setValue(startAddr);
		this.memory = (memorySize > 0 ? new Memory(memorySize) : new Memory());
		this.stack = (stackSize > 0 ? new Stack(stackSize) : new Stack());
	}

	public void execute() {

	}

}
