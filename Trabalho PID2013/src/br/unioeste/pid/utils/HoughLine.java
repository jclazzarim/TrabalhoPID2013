package br.unioeste.pid.utils;

import java.awt.image.BufferedImage;

public class HoughLine {

	protected double theta;
	protected double r;

	public HoughLine(double theta, double r) {
		this.theta = theta;
		this.r = r;
	}

	public void desenhaLinha(BufferedImage image, int color) {

		int height = image.getHeight();
		int width = image.getWidth();

		int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

		float centerX = width / 2;
		float centerY = height / 2;

		double tsin = Math.sin(theta);
		double tcos = Math.cos(theta);

		if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75) {
			//Vertical
			for (int y = 0; y < height; y++) {
				int x = (int) ((((r - houghHeight) - ((y - centerY) * tsin)) / tcos) + centerX);
				if (x < width && x >= 0) {
					image.setRGB(x, y, color);
				}
			}
		} else {
			//Horizontal
			for (int x = 0; x < width; x++) {
				int y = (int) ((((r - houghHeight) - ((x - centerX) * tcos)) / tsin) + centerY);
				if (y < height && y >= 0) {
					image.setRGB(x, y, color);
				}
			}
		}
	}
}