package com.github.brunoroberto.chip8.store;

/**
 * Memory representation of the CHIP-8
 * 
 * @author brunoroberto
 *
 */
public class Memory {

	private byte[] memory;

	public Memory(int size) {
		this.memory = new byte[size];
	}

	public void writeWord(int addr, byte word) {
		this.memory[addr] = word;
	}

	public byte getWord(int addr) {
		return this.memory[addr];
	}
}
