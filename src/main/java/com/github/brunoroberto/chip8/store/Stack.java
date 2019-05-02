package com.github.brunoroberto.chip8.store;

import com.github.brunoroberto.chip8.register.Register8;

/**
 * Stack representation of the CHIP-8
 * 
 * @author brunoroberto
 *
 */
public class Stack {

	private static final int STACK_SIZE = 0x10; // 16 default size

	private short[] data;
	private Register8 sp; // stack pointer register

	public Stack() {
		init(STACK_SIZE);
	}

	public Stack(int stackSize) {
		init(stackSize);
	}

	private void init(int stackSize) {
		this.data = new short[stackSize];
		this.sp = new Register8((byte) -1);
	}
}
