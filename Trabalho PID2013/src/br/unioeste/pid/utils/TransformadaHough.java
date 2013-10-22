package br.unioeste.pid.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

public class TransformadaHough extends Thread {

	final int tamanhoVizinhanca = 4;
	final int maxTheta = 180;
	final double passoTheta = Math.PI / maxTheta;
	protected int w, h;
	protected int[][] houghArray; // Matriz de hough
	protected float centerX, centerY; // Centro da imagem
	protected int houghHeight; // Altura de hough
	protected int doubleHeight;
	protected int count;// quantidade de pontos adicionados
	private double[] sinCache;// Chache maroto do Seno
	private double[] cosCache;// Cache maroto do coseno
	private BufferedImage imagem;

	public TransformadaHough(BufferedImage imagem) {
		this.imagem = imagem;
		this.w = imagem.getWidth();
		this.h = imagem.getHeight();
		inicializar();
		addPoints(imagem);
	}

	private void inicializar() {

		houghHeight = (int) (Math.sqrt(2) * Math.max(h, w)) / 2;

		doubleHeight = 2 * houghHeight;

		houghArray = new int[maxTheta][doubleHeight];

		centerX = w / 2;
		centerY = h / 2;

		count = 0;

		// Cria uma cache (grana) pra dexa mais rapido o processamento
		sinCache = new double[maxTheta];
		cosCache = sinCache.clone();
		for (int t = 0; t < maxTheta; t++) {
			double realTheta = t * passoTheta;
			sinCache[t] = Math.sin(realTheta);
			cosCache[t] = Math.cos(realTheta);
		}
	}

	/**
	 * Adiciona os pontos de uma imagem; assume que todo ponto nao preto é uma
	 * borda Imagem monocromatica
	 */
	private void addPoints(BufferedImage image) {

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				// pega pontos brancos (nao pretos)
				if ((image.getRGB(x, y) & 0x000000ff) != 0) {
					addPoint(x, y);
				}
			}
		}
	}

	/**
	 * Adiciona um ponto na matriz de hough
	 */
	private void addPoint(int x, int y) {

		for (int t = 0; t < maxTheta; t++) {

			int r = (int) (((x - centerX) * cosCache[t]) + ((y - centerY) * sinCache[t]));
			r += houghHeight;

			if (r < 0 || r >= doubleHeight)
				continue;
			houghArray[t][r]++;

		}

		count++;
	}

	/**
	 * Metodo pra extrair as linhas dos pontos desenhados na imagem
	 * @param threshold
	 * @return
	 */
	private Vector<HoughLine> getLines(int threshold) {

		Vector<HoughLine> lines = new Vector<HoughLine>(20);

		if (count == 0)
			return lines;

		for (int t = 0; t < maxTheta; t++) {
			loop: for (int r = tamanhoVizinhanca; r < doubleHeight
					- tamanhoVizinhanca; r++) {

				if (houghArray[t][r] > threshold) {

					int peak = houghArray[t][r];

					for (int dx = -tamanhoVizinhanca; dx <= tamanhoVizinhanca; dx++) {
						for (int dy = -tamanhoVizinhanca; dy <= tamanhoVizinhanca; dy++) {
							int dt = t + dx;
							int dr = r + dy;
							if (dt < 0)
								dt = dt + maxTheta;
							else if (dt >= maxTheta)
								dt = dt - maxTheta;
							if (houghArray[dt][dr] > peak) {
								continue loop;
							}
						}
					}

					double theta = t * passoTheta;

					lines.add(new HoughLine(theta, r));

				}
			}
		}

		return lines;
	}

	/**
	 * Maior valor do array
	 */
	private int getMaxArrayValue() {
		int max = 0;
		for (int t = 0; t < maxTheta; t++) {
			for (int r = 0; r < doubleHeight; r++) {
				if (houghArray[t][r] > max) {
					max = houghArray[t][r];
				}
			}
		}
		return max;
	}

	/**
	 * transforma o array em imagem(degubzz Marotis)
	 */
	private BufferedImage getHoughArrayImage() {
		int max = getMaxArrayValue();
		BufferedImage image = new BufferedImage(maxTheta, doubleHeight,
				BufferedImage.TYPE_INT_ARGB);
		for (int t = 0; t < maxTheta; t++) {
			for (int r = 0; r < doubleHeight; r++) {
				double value = 255 * ((double) houghArray[t][r]) / max;
				int v = 255 - (int) value;
				int c = new Color(v, v, v).getRGB();
				image.setRGB(t, r, c);
			}
		}
		return image;
	}

	public BufferedImage getImagem(){
		Vector<HoughLine> lines = getLines(30);

		// desenha as linhas na imagem
		for (int j = 0; j < lines.size(); j++) {
			HoughLine line = lines.elementAt(j);
			line.desenhaLinha(imagem, Color.RED.getRGB());
		}
		return imagem;
	}
	
}
