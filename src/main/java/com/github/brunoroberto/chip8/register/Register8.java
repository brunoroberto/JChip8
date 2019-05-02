package com.github.brunoroberto.chip8.register;

public class Register8 {

	private byte value;

	public Register8() {
	}

	public Register8(byte value) {
		setValue(value);
	}

	public void setValue(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return this.value;
	}

}
