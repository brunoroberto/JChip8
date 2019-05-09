package com.github.brunoroberto.chip8.store;

/**
 * Stack representation of the CHIP-8
 * 
 * @author brunoroberto
 *
 */
public class Stack {

	private short[] data;
	private short sp; // stack pointer register

	public Stack(int stackSize) {
		this.data = new short[stackSize];
		this.sp = 0;
	}

	private boolean isFull() {
		return this.sp == this.data.length;
	}

	private boolean isEmpty() {
		return this.sp == 0;
	}

	public void put(short data) {
		if (isFull())
			throw new StackOverflowError("the stack is full");
		this.data[this.sp++] = data;
	}

	public short pop() {
		if (isEmpty())
			throw new StackOverflowError("the stack is empty");
		return this.data[this.sp--];
	}
}
