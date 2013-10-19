package br.unioeste.pid.utils;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import br.unioeste.pid.imagem.ImagePanel;

public class TelaUtils {

	public void colorPick(final ImagePanel imagePanel) {
		imagePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point point = e.getPoint();
				BufferedImage grid = imagePanel.getGrid();
				int rgb = grid.getRGB(point.x, point.y);
				int r = (int) ((rgb & 0x00FF0000) >>> 16);
				int g = (int) ((rgb & 0x0000FF00) >>> 8);
				int b = (int) (rgb & 0x000000FF);
				processaCor(new Color(r, g, b));
			}
		});
	}

	private void processaCor(final Color cor) {

	}

	private int[] getRgb(BufferedImage imagem, int w, int h){
		int rgb[] = new int[w*h];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgb[i+j] = imagem.getRGB(i, j);
			}
		}
		return rgb;
	}
	
	public BufferedImage destacaCell(final BufferedImage imagem) {
		
		
		int w = imagem.getWidth();
		int h = imagem.getHeight();
		int[] rgb = getRgb(imagem, w, h);
		
//		for (int i = 0; i < w * h; i++) {
//			rgb[i] = setRGB(getRed(rgb[i]), 0, 0);
//		}

		PixelUtils utils = new PixelUtils();
		
		BufferedImage grid = utils.greyScale(imagem);
		grid = otsu(grid);
		grid = utils.erosao(grid);
		grid = utils.passaAlta(grid);
		
		BufferedImage negada = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int cor = grid.getRGB(i, j); // a cor inversa é dado por 255
												// menos o valor da cor
				int b = 255 - (int) ((cor & 0x00FF0000) >>> 16);
				int g = 255 - (int) ((cor & 0x0000FF00) >>> 8);
				int r = 255 - (int) (cor & 0x000000FF);
				Color color = new Color(r, g, b);
				negada.setRGB(i, j, color.getRGB());
			}
		}

//		BufferedImage retorno = new BufferedImage(w, h, imagem.getType());	
//		for (int i = 0; i < w; i++) {
//			for (int j = 0; j < h; j++) {
//				int rgb = grid1.getRGB(j, i) & grid2.getRGB(j, i);
//				retorno.setRGB(j, i, rgb);
//				
//			}
//		}
		
		
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
			histograma[getBlue(rgb[i])]++;
		}

		return histograma;
	}
	
	private int getBlue(int rgb){
		int r = 255 - (int) ((rgb & 0x00FF0000) >>> 16);
		int g = 255 - (int) ((rgb & 0x0000FF00) >>> 8);
		return (int) (rgb & 0x000000FF);
	}

}
