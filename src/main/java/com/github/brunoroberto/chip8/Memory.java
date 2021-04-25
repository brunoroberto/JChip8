package com.github.brunoroberto.chip8;

import java.util.Arrays;

/**
 * Memory representation of the CHIP-8
 * 
 * @author brunoroberto
 *
 */
public class Memory {

	private static final short SIZE = 4096; // 4KB

	private byte[] memory;

	public Memory() {
		this.memory = new byte[SIZE];
	}

	public void writeWord(int address, byte word) {
		this.memory[address] = word;
	}

	public byte getWord(int address) {
		try {
			return this.memory[address];
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void dump() {
		System.out.println("Memory: " + Arrays.toString(memory));
	}
}
