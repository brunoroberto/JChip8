package com.github.brunoroberto.chip8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScreenMemory {

	private static final int COLUMNS = 64;
	private static final int ROWS = 32;

	private final int PIXEL_SIZE;

	private byte[] data;

	public ScreenMemory(double screenHeight) {
		int scale = Math.floorDiv((int) screenHeight, ROWS);
		this.PIXEL_SIZE = (int) (scale - (scale * 0.10));
		this.data = new byte[COLUMNS * ROWS];
	}

	public boolean setPixel(int x, int y) {
		int index = (x % COLUMNS) + (y % ROWS) * COLUMNS;
		this.data[index] ^= 1;
		return this.data[index] != 1;
	}

	public List<ActivePixel> getActivePixels() {
		List<ActivePixel> activePixels = new ArrayList<>();
		for (int i = 0; i < this.data.length; i++) {
			if (this.data[i] > 0) {
				int x = (i % COLUMNS) * PIXEL_SIZE;
				int y = (i / COLUMNS) * PIXEL_SIZE;
				activePixels.add(new ActivePixel(x, y));
			}
		}
		return activePixels;
	}

	public void clear() {
		Arrays.fill(data, (byte) 0);
	}

	public class ActivePixel {

		private int x;
		private int y;

		public ActivePixel(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getSize() {
			return PIXEL_SIZE;
		}
	}
}
