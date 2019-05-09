package com.github.brunoroberto.chip8;

import java.util.Arrays;

public class Display {

	private byte[] data;

	public Display(int displaySize) {
		this.data = new byte[displaySize];
	}

	public void clear() {
		Arrays.fill(data, (byte) 0);
	}
}
