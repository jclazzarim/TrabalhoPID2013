package br.unioeste.pid.utils;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import br.unioeste.pid.imagem.ImagePanel;

public class TelaUtils {

	private void verificaImagem(BufferedImage imagem) {
		 
	}

	private int[] getRgb(BufferedImage imagem, int w, int h) {
		int rgb[] = new int[w * h];
		int aux = 0;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgb[aux] = imagem.getRGB(i, j);
				aux++;
			}
		}
		return rgb;
	}

	public BufferedImage destacaCell(final BufferedImage imagem) {

		int w = imagem.getWidth();
		int h = imagem.getHeight();
		int[] rgb = getRgb(imagem, w, h);
		int aux = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				imagem.setRGB(i, j, setRGB(getBlue(rgb[aux]), 0, 0));
				aux++;
			}
		}

		PixelUtils utils = new PixelUtils();

		BufferedImage grid = utils.greyScale(imagem);
		grid = otsu(grid);
		grid = utils.erosao(grid);
		grid = sobel(grid);

		BufferedImage negada = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int cor = grid.getRGB(i, j); 
				int b = 255 - (int) ((cor & 0x00FF0000) >>> 16);
				int g = 255 - (int) ((cor & 0x0000FF00) >>> 8);
				int r = 255 - (int) (cor & 0x000000FF);
				Color color = new Color(r, g, b);
				negada.setRGB(i, j, color.getRGB());
			}
		}

		return negada;
	}

	private BufferedImage otsu(BufferedImage imagem) {
		int histograma[] = histograma(imagem);
		int total = imagem.getWidth() * imagem.getHeight();

		float sum = 0;
		for (int t = 0; t < 256; t++) {
			sum += t * histograma[t];
		}

		float sumB = 0;
		int wB = 0;
		int wF = 0;

		float varMax = 0;
		int threshold = 0;

		for (int t = 0; t < 256; t++) {
			wB += histograma[t];
			if (wB == 0) {
				continue;
			}

			wF = total - wB;
			if (wF == 0) {
				break;
			}

			sumB += (float) (t * histograma[t]);

			float mB = sumB / wB;
			float mF = (sum - sumB) / wF;

			float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

			if (varBetween > varMax) {
				varMax = varBetween;
				threshold = t;
			}

		}

		PixelUtils utils = new PixelUtils();

		return utils.limiar(imagem, threshold);
	}

	private int[] histograma(BufferedImage imagem) {
		int histograma[] = new int[256];
		int w = imagem.getWidth();
		int h = imagem.getHeight();
		int rgb[] = getRgb(imagem, w, h);

		for (int i = 0; i < w * h; i++) {
			histograma[getRed(rgb[i])]++;
		}

		return histograma;
	}

	private int getBlue(int rgb) {
		return (int) (rgb & 0x000000FF);
	}

	private int getRed(int rgb) {
		return (int) ((rgb & 0x00FF0000) >>> 16);
	}

	private int setRGB(int r, int g, int b) {
		return ((255 & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
	}

	public BufferedImage sobel(BufferedImage image) {

		int mascaraHorizontal[][] = new int[3][3];
		int mascaraVertical[][] = new int[3][3];

		mascaraVertical[0][0] = 1;
		mascaraVertical[0][1] = 0;
		mascaraVertical[0][2] = -1;
		mascaraVertical[1][0] = 2;
		mascaraVertical[1][1] = 0;
		mascaraVertical[1][2] = -2;
		mascaraVertical[2][0] = 1;
		mascaraVertical[2][1] = 0;
		mascaraVertical[2][2] = -1;

		mascaraHorizontal[0][0] = -1;
		mascaraHorizontal[0][1] = -2;
		mascaraHorizontal[0][2] = -1;
		mascaraHorizontal[1][0] = 0;
		mascaraHorizontal[1][1] = 0;
		mascaraHorizontal[1][2] = 0;
		mascaraHorizontal[2][0] = 1;
		mascaraHorizontal[2][1] = 2;
		mascaraHorizontal[2][2] = 1;

		return processaPassaAlta(image, mascaraHorizontal, mascaraVertical);
	}

	public BufferedImage processaPassaAlta(BufferedImage image, int mascaraHorizontal[][], int mascaraVertical[][]) {
		int aux;
		int w = image.getWidth();
		int h = image.getHeight();
		int rgb[] = getRgb(image, w, h);
		int xy[][] = new int[w + 2][h + 2];
		int vertical, horizontal;
		int resultado;

		BufferedImage img = new BufferedImage(w, h, image.getType());

		// copiando imagem para centro matriz
		aux = 0;
		for (int j = 1; j <= w; j++) {
			for (int i = 1; i <= h; i++) {
				xy[j][i] = rgb[aux];
				aux++;
			}
		}

		// replicando nas bordas
		xy[0][0] = xy[1][1];
		xy[0][h] = xy[1][h - 1];
		xy[w][h] = xy[w - 1][h - 1];
		xy[w][0] = xy[w - 1][1];
		for (int i = 1; i <= w; i++) {
			xy[i][0] = xy[i][1];
			xy[i][h] = xy[i][h - 1];
		}

		for (int i = 1; i <= h; i++) {
			xy[0][i] = xy[1][i];
			xy[w][i] = xy[w - 1][i];
		}

		aux = 0;
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {

				vertical = getRed(xy[i - 1][j - 1]) * mascaraVertical[0][0] + getRed(xy[i - 1][j]) * mascaraVertical[0][1] + getRed(xy[i - 1][+j])
						* mascaraVertical[0][2] + getRed(xy[i][j]) * mascaraVertical[1][0] + getRed(xy[i][j - 1]) * mascaraVertical[1][1]
						+ getRed(xy[i][j + 1]) * mascaraVertical[1][2] + getRed(xy[i + 1][j - 1]) * mascaraVertical[2][0] + getRed(xy[i + 1][j])
						* mascaraVertical[2][1] + getRed(xy[i + 1][j + 1]) * mascaraVertical[2][2];

				horizontal = getRed(xy[i - 1][j - 1]) * mascaraHorizontal[0][0] + getRed(xy[i - 1][j]) * mascaraHorizontal[0][1] + getRed(xy[i - 1][+j])
						* mascaraHorizontal[0][2] + getRed(xy[i][j]) * mascaraHorizontal[1][0] + getRed(xy[i][j - 1]) * mascaraHorizontal[1][1]
						+ getRed(xy[i][j + 1]) * mascaraHorizontal[1][2] + getRed(xy[i + 1][j - 1]) * mascaraHorizontal[2][0] + getRed(xy[i + 1][j])
						* mascaraHorizontal[2][1] + getRed(xy[i + 1][j + 1]) * mascaraHorizontal[2][2];

				resultado = (int) Math.sqrt(Math.pow(vertical, 2) + Math.pow(horizontal, 2));

				if (resultado > 255) {
					resultado = 255;
				}

				if (resultado < 0) {
					resultado = 0;
				}

				rgb[aux] = setRGB(resultado, resultado, resultado);
				aux++;
			}
		}
		int count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				img.setRGB(i, j, rgb[count]);
				count++;
			}
		}
		return img;
	}

}
