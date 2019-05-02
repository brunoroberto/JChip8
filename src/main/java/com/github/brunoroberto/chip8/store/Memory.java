package com.github.brunoroberto.chip8.store;

/**
 * Memory representation of the CHIP-8
 * 
 * @author brunoroberto
 *
 */
public class Memory {

	private static final int MEMORY_SIZE = 0x1000; // 4KB (4,096 bytes)

	private byte[] memory;

	public Memory() {
		init(MEMORY_SIZE);
	}

	public Memory(int size) {
		init(size);
	}

	private void init(int size) {
		this.memory = new byte[size];
	}

	public void writeWord(int addr, byte word) {
		this.memory[addr] = word;
	}

	public byte getWord(int addr) {
		return this.memory[addr];
	}
}
