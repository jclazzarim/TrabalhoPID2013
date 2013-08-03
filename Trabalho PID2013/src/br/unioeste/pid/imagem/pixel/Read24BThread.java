package br.unioeste.pid.imagem.pixel;

import java.awt.Color;

public class Read24BThread implements Runnable {

	byte rgb[];
	Color MPixel[];
   
	public Read24BThread(byte rgb[], Color MPixel[]) {
		this.rgb = rgb;
		this.MPixel = MPixel;
              
	}

	@Override
	public void run() {
		read();
	}

	private synchronized void read() {
		int k;
		for (int j = 0; j < MPixel.length; j++) {
			k = 3 * j;
			MPixel[j] = new Color(((byte) rgb[k + 2] & 0xFF), ((byte) rgb[k + 1] & 0xFF), ((byte) rgb[k] & 0xFF));

		}
	}

}
