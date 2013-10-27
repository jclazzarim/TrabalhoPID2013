package br.unioeste.pid.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class TelaUtils {

	private int[][] reds;
	private int[][] greens;
	private int[][] blues;

	public BufferedImage verificaImagem(BufferedImage imagem) {
		PixelUtils utils = new PixelUtils();

		BufferedImage greyScale = utils.greyScale(imagem);
		int[] rgb = getRgb(greyScale, imagem.getWidth(), imagem.getHeight());
		int w = imagem.getWidth();
		int h = imagem.getHeight();

		int rr = 0;

		for (int i = 0; i < w * h; i++) {
			rr += getGreen(rgb[i]);
		}
		rr = (int) (rr / (w * h));

		if (rr > 110) {
			return processaCelulaRoxa(imagem);
		} else {

			return processaCelulaColorida(getCelulaImagem(imagem));
		}

	}

	public int[] getColorImagem(int x, int y, BufferedImage imagem) {
		PixelUtils utils = new PixelUtils();
		imagem = utils.erosao(imagem);
		imagem = utils.erosao(imagem);
		imagem = utils.erosao(imagem);

		int rgb = imagem.getRGB(x, y);

		int blue = getBlue(rgb);
		int green = getGreen(rgb);
		int red = getRed(rgb);

		if (blue > green && blue > red) {
			return getBlues(getRgb(imagem, imagem.getWidth(), imagem.getHeight()));
		}

		if (red > green && blue < red) {
			return getReds(getRgb(imagem, imagem.getWidth(), imagem.getHeight()));
		}

		if (blue < green && green > red) {
			return getGreens(getRgb(imagem, imagem.getWidth(), imagem.getHeight()));
		}
		return null;

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

	private BufferedImage getCelulaImagem(BufferedImage imagem) {
		int[] rgb = getRgb(imagem, imagem.getWidth(), imagem.getHeight());

		int w = imagem.getWidth();
		int h = imagem.getHeight();
		int[] red = getReds(rgb);
		int[] green = getGreens(rgb);
		int[] blue = getBlues(rgb);

		int mr = 0;
		int mg = 0;
		int mb = 0;

		for (int i = 0; i < w * h; i++) {
			mr += red[i];
			mg += green[i];
			mb += blue[i];
		}

		mr = (int) (mr / (w * h));
		mg = (int) (mg / (w * h));
		mb = (int) (mb / (w * h));

		if (mr > mg) {
			if (mr > mb) {
				return getCelula(imagem, green, blue);
			} else {
				return getCelula(imagem, red, green);
			}
		} else {
			if (mg > mb) {
				return getCelula(imagem, red, blue);
			} else {
				return getCelula(imagem, red, green);
			}
		}

	}

	private BufferedImage getCelula(BufferedImage imagem, int[] cor1, int[] cor2) {
		int aux = 0;
		int w = imagem.getWidth();
		int h = imagem.getHeight();
		BufferedImage imagemCor1 = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage imagemCor2 = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				imagemCor1.setRGB(i, j, setRGB(cor1[aux], cor1[aux], cor1[aux]));
				imagemCor2.setRGB(i, j, setRGB(cor2[aux], cor2[aux], cor2[aux]));
				aux++;
			}
		}

		for (int i = 0; i < 2; i++) {
			imagemCor1 = erosaoBola(imagemCor1);
			imagemCor2 = erosaoBola(imagemCor2);
		}
		for (int i = 0; i < 2; i++) {
			imagemCor1 = dilatacaoBola(imagemCor1);
			imagemCor2 = dilatacaoBola(imagemCor2);
		}

		PixelUtils utils = new PixelUtils();
		imagemCor1 = utils.limiar(imagemCor1, 127);
		imagemCor2 = utils.limiar(imagemCor2, 127);

		int img1 = 0;
		int img2 = 0;
		int[] rgb1 = getRgb(imagemCor1, imagemCor1.getWidth(), imagemCor1.getHeight());
		int[] rgb2 = getRgb(imagemCor1, imagemCor1.getWidth(), imagemCor1.getHeight());
		for (int i = 0; i < w * h; i++) {
			img1 += getGreen(rgb1[i]);
			img2 += getGreen(rgb2[i]);
		}

		img1 = (int) (img1 / (w * h));
		img2 = (int) (img2 / (w * h));

		if (img1 > img2) {
			return imagemCor1;
		} else {
			return imagemCor2;
		}
	}

	private BufferedImage processaCelulaColorida(final BufferedImage imagem) {

		PixelUtils utils = new PixelUtils();

		BufferedImage grid = imagem;
		grid = mediana(grid, 19);
		grid = utils.dilatacao(grid);
		grid = negar(grid);
		ArrayList<Pixel> borda = getBorda(grid);
		grid = roberts(grid);
		grid = negar(grid);

		grid = processaDiametro(grid, borda);
		return grid;
	}

	private BufferedImage processaCelulaRoxa(BufferedImage imagem) {
		int[] rgb = getRgb(imagem, imagem.getWidth(), imagem.getHeight());
		int[] greens = getGreens(rgb);
		int aux = 0;
		BufferedImage grid = new BufferedImage(imagem.getWidth(), imagem.getHeight(), imagem.getType());
		for (int i = 0; i < imagem.getWidth(); i++) {
			for (int j = 0; j < imagem.getHeight(); j++) {
				grid.setRGB(i, j, setRGB(0, greens[aux], 0));
				aux++;
			}
		}

		PixelUtils utils = new PixelUtils();
		BufferedImage greyScale = utils.greyScale(grid);
		greyScale = negar(greyScale);
		greyScale = utils.limiar(greyScale, 191);
		greyScale = negar(greyScale);
		greyScale = dilatacaoBola(greyScale);
		greyScale = dilatacaoBola(greyScale);
		greyScale = erosaoBola(greyScale);
		greyScale = erosaoBola(greyScale);
		greyScale = erosaoBola(greyScale);
		greyScale = mediana(greyScale, 17);
		greyScale = dilatacaoBola(greyScale);

		ArrayList<Pixel> borda = getBorda(greyScale);
		greyScale = roberts(greyScale);
		greyScale = negar(greyScale);

		return processaDiametro(greyScale, borda);
	}

	private Object[] getDiametro(List<Entry<Pixel, Integer>> lista) {
		Object[] resultados = new Object[4];
		resultados[0] = 0;
		int max = 0;
		int perimetro = 0;

		for (int i = 0; i < lista.size(); i++) {
			Pixel pixel = lista.get(i).getKey();
			for (int j = i; j < lista.size(); j++) {
				Pixel pixel2 = lista.get(j).getKey();
				int distancia = (int) Math.sqrt(Math.pow(pixel2.getX() - pixel.getX(), 2) + Math.pow(pixel2.getY() - pixel.getY(), 2));
				if (distancia > max) {
					max = distancia;
					resultados[0] = distancia;
					resultados[1] = pixel;
					resultados[2] = pixel2;
				}
			}
			perimetro++;
		}
		resultados[3] = perimetro;
		return resultados;
	}

	private BufferedImage processaDiametro(BufferedImage grid, ArrayList<Pixel> borda) {
		PixelUtils utils = new PixelUtils();

		grid = setBorda(grid, borda);
		utils.getConectividades(grid);

		List<List<Entry<Pixel, Integer>>> subImagens = getSubImagens(utils.getCelulas().entrySet());
		int max = 0;

		Graphics graphics = grid.getGraphics();
		graphics.setColor(Color.RED);
		Pixel pixel1;
		Pixel pixel2;
		int mediaP = 0;
		int mediaD = 0;
		for (List<Entry<Pixel, Integer>> entry : subImagens) {
			if (entry.get(0).getValue() != 0) {
				if (entry.get(0).getValue() > max) {
					max = entry.get(0).getValue();
				}
				Object[] diametro = getDiametro(entry);
				System.out.println("Perimetro da celula " + entry.get(0).getValue() + ": " + (int) diametro[3]);
				System.out.println("Diametro " + entry.get(0).getValue() + ": " + (int) diametro[0]);
				mediaD += (int) diametro[0];
				mediaP += (int) diametro[3];
				pixel1 = (Pixel) diametro[1];
				pixel2 = (Pixel) diametro[2];
				if (pixel1 != null && pixel2 != null)
					graphics.drawString("D: " + (int) diametro[0] + " P: " + (int) diametro[3], pixel1.getX(), pixel1.getY());
				graphics.drawLine(pixel1.getX(), pixel1.getY(), pixel2.getX(), pixel2.getY());
			}
		}
		graphics.drawString("Celulas: " + max, 0, 10);
		graphics.drawString(" Media Diametro: " + mediaD / max, 0, 20);
		graphics.drawString("Media Perimetro: " + mediaP / max, 0, 30);
		System.out.println("Quantidad de celulas: " + max);
		return grid;
	}

	private List<List<Entry<Pixel, Integer>>> getSubImagens(Set<Entry<Pixel, Integer>> entrys) {
		List<Entry<Pixel, Integer>> listaPixels = new ArrayList<>(entrys);
		Comparator<Entry<Pixel, Integer>> compare = new Comparator<Entry<Pixel, Integer>>() {
			@Override
			public int compare(Entry<Pixel, Integer> o1, Entry<Pixel, Integer> o2) {
				return o1.getValue() - o2.getValue();
			}
		};
		Collections.sort(listaPixels, compare);

		int max = -1;
		List<Entry<Pixel, Integer>> lista = new ArrayList<Entry<Pixel, Integer>>();
		List<List<Entry<Pixel, Integer>>> retorno = new ArrayList<List<Entry<Pixel, Integer>>>();
		for (Entry<Pixel, Integer> entry : listaPixels) {
			if (max == -1) {
				max = entry.getValue();
			}

			if (entry.getValue() == max) {
				lista.add(entry);
			} else {
				retorno.add(lista);
				lista = new ArrayList<Entry<Pixel, Integer>>();
				lista.add(entry);
				max = entry.getValue();
			}
		}
		retorno.add(lista);

		return retorno;
	}

	private ArrayList<Pixel> getBorda(BufferedImage imagem) {
		ArrayList<Pixel> pixels = new ArrayList<>();

		for (int j = 0; j < imagem.getHeight(); j++) {
			int rgb = imagem.getRGB(0, j);
			if (rgb == Color.BLACK.getRGB()) {
				Pixel pixel = new Pixel(0, j);
				pixels.add(pixel);
			}
		}

		for (int j = 0; j < imagem.getWidth(); j++) {
			int rgb = imagem.getRGB(j, 0);
			if (rgb == Color.BLACK.getRGB()) {
				Pixel pixel = new Pixel(j, 0);
				pixels.add(pixel);
			}
		}

		for (int j = 0; j < imagem.getHeight(); j++) {
			int rgb = imagem.getRGB(imagem.getWidth() - 2, j);
			if (rgb == Color.BLACK.getRGB()) {
				Pixel pixel = new Pixel(imagem.getWidth() - 1, j);
				pixels.add(pixel);
			}
		}

		for (int j = 0; j < imagem.getWidth(); j++) {
			int rgb = imagem.getRGB(j, imagem.getHeight() - 2);
			if (rgb == Color.BLACK.getRGB()) {
				Pixel pixel = new Pixel(j, imagem.getHeight() - 1);
				pixels.add(pixel);
			}
		}
		return pixels;
	}

	private BufferedImage setBorda(BufferedImage imagem, ArrayList<Pixel> pixels) {
		for (Pixel pixel : pixels) {
			imagem.setRGB(pixel.getX(), pixel.getY(), Color.BLACK.getRGB());
		}
		return imagem;
	}

	private BufferedImage erosaoBola(BufferedImage imagem) {
		imagem = negar(imagem);
		imagem = dilatacaoBola(imagem);
		return negar(imagem);
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
			histograma[getGreen(rgb[i])]++;
		}

		return histograma;
	}

	private int getBlue(int rgb) {
		return (int) (rgb & 0x000000FF);
	}

	private int[] getBlues(int[] rgb) {
		int[] blues = new int[rgb.length];
		for (int i = 0; i < rgb.length; i++) {
			blues[i] = getBlue(rgb[i]);
		}
		return blues;
	}

	private int[] getGreens(int[] rgb) {
		int[] greens = new int[rgb.length];
		for (int i = 0; i < rgb.length; i++) {
			greens[i] = getGreen(rgb[i]);
		}
		return greens;
	}

	private int[] getReds(int[] rgb) {
		int[] reds = new int[rgb.length];
		for (int i = 0; i < rgb.length; i++) {
			reds[i] = getRed(rgb[i]);
		}
		return reds;
	}

	private int getRed(int rgb) {
		return (int) ((rgb & 0x00FF0000) >>> 16);
	}

	private int getGreen(int rgb) {
		return (int) ((rgb & 0x0000FF00) >>> 8);
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

	public BufferedImage roberts(BufferedImage image) {

		int mascaraHorizontal[][] = new int[3][3];
		int mascaraVertical[][] = new int[3][3];

		mascaraVertical[0][0] = 0;
		mascaraVertical[0][1] = 0;
		mascaraVertical[0][2] = -1;
		mascaraVertical[1][0] = 0;
		mascaraVertical[1][1] = 1;
		mascaraVertical[1][2] = 0;
		mascaraVertical[2][0] = 0;
		mascaraVertical[2][1] = 0;
		mascaraVertical[2][2] = 0;

		mascaraHorizontal[0][0] = -1;
		mascaraHorizontal[0][1] = 0;
		mascaraHorizontal[0][2] = 0;
		mascaraHorizontal[1][0] = 0;
		mascaraHorizontal[1][1] = 1;
		mascaraHorizontal[1][2] = 0;
		mascaraHorizontal[2][0] = 0;
		mascaraHorizontal[2][1] = 0;
		mascaraHorizontal[2][2] = 0;

		return processaPassaAlta(image, mascaraHorizontal, mascaraVertical);
	}

	private BufferedImage processaPassaAlta(BufferedImage image, int mascaraHorizontal[][], int mascaraVertical[][]) {
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

	private BufferedImage negar(BufferedImage grid) {
		BufferedImage negada = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < grid.getWidth(); i++) {
			for (int j = 0; j < grid.getHeight(); j++) {
				int cor = grid.getRGB(i, j);
				int r = 255 - (int) ((cor & 0x00FF0000) >>> 16);
				int g = 255 - (int) ((cor & 0x0000FF00) >>> 8);
				int b = 255 - (int) (cor & 0x000000FF);
				Color color = new Color(r, g, b);
				negada.setRGB(i, j, color.getRGB());
			}
		}
		return negada;
	}

	public BufferedImage dilatacaoBola(BufferedImage imagem) {
		int aux;
		int w = imagem.getWidth();
		int h = imagem.getHeight();
		int rgb[] = getRgb(imagem, w, h);
		int xy[][] = new int[w][h];
		int xy2[][] = new int[w][h];

		aux = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				xy[i][j] = rgb[aux];
				xy2[i][j] = rgb[aux];
				aux++;
			}
		}

		aux = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {

				if (getGreen(xy[i][j]) == 0) {
					xy2[i][j] = setRGB(0, 0, 0);

					if (i - 1 > 0) {
						xy2[i - 1][j] = setRGB(0, 0, 0);
					}
					if (i - 2 > 0) {
						xy2[i - 2][j] = setRGB(0, 0, 0);
					}
					if (i - 3 > 0) {
						xy2[i - 3][j] = setRGB(0, 0, 0);
					}
					if (i - 4 > 0) {
						xy2[i - 4][j] = setRGB(0, 0, 0);
					}

					if (i + 1 < w) {
						xy2[i + 1][j] = setRGB(0, 0, 0);
					}
					if (i + 2 < w) {
						xy2[i + 2][j] = setRGB(0, 0, 0);
					}
					if (i + 3 < w) {
						xy2[i + 3][j] = setRGB(0, 0, 0);
					}
					if (i + 4 < w) {
						xy2[i + 4][j] = setRGB(0, 0, 0);
					}
					if (i + 5 < w) {
						xy2[i + 5][j] = setRGB(0, 0, 0);
					}

					if (j + 1 < h) {
						xy2[i][j + 1] = setRGB(0, 0, 0);
					}
					if (j + 2 < h) {
						xy2[i][j + 2] = setRGB(0, 0, 0);
					}
					if (j + 3 < h) {
						xy2[i][j + 3] = setRGB(0, 0, 0);
					}
					if (j + 4 < h) {
						xy2[i][j + 4] = setRGB(0, 0, 0);
					}

					if (j - 1 > 0) {
						xy2[i][j - 1] = setRGB(0, 0, 0);
					}
					if (j - 2 > 0) {
						xy2[i][j - 2] = setRGB(0, 0, 0);
					}
					if (j - 3 > 0) {
						xy2[i][j - 3] = setRGB(0, 0, 0);
					}
					if (j - 4 > 0) {
						xy2[i][j - 4] = setRGB(0, 0, 0);
					}
					if (j - 5 > 0) {
						xy2[i][j - 5] = setRGB(0, 0, 0);
					}

					if (j - 1 > 0 && i + 1 < w) {
						xy2[i + 1][j - 1] = setRGB(0, 0, 0);
					}
					if (j - 2 > 0 && i + 1 < w) {
						xy2[i + 1][j - 2] = setRGB(0, 0, 0);
					}
					if (j - 3 > 0 && i + 1 < w) {
						xy2[i + 1][j - 3] = setRGB(0, 0, 0);
					}
					if (j - 4 > 0 && i + 1 < w) {
						xy2[i + 1][j - 4] = setRGB(0, 0, 0);
					}
					if (j - 5 > 0 && i + 1 < w) {
						xy2[i + 1][j - 5] = setRGB(0, 0, 0);
					}

					if (j + 1 < h && i + 1 < w) {
						xy2[i + 1][j + 1] = setRGB(0, 0, 0);
					}
					if (j + 2 < h && i + 1 < w) {
						xy2[i + 1][j + 2] = setRGB(0, 0, 0);
					}
					if (j + 3 < h && i + 1 < w) {
						xy2[i + 1][j + 3] = setRGB(0, 0, 0);
					}
					if (j + 4 < h && i + 1 < w) {
						xy2[i + 1][j + 4] = setRGB(0, 0, 0);
					}

					if (i - 1 > 0 && j - 1 > 0) {
						xy2[i - 1][j - 1] = setRGB(0, 0, 0);
					}
					if (i - 2 > 0 && j - 1 > 0) {
						xy2[i - 2][j - 1] = setRGB(0, 0, 0);
					}
					if (i - 3 > 0 && j - 1 > 0) {
						xy2[i - 3][j - 1] = setRGB(0, 0, 0);
					}
					if (i - 4 > 0 && j - 1 > 0) {
						xy2[i - 4][j - 1] = setRGB(0, 0, 0);
					}

					if (i + 2 < w && j - 1 > 0) {
						xy2[i + 2][j - 1] = setRGB(0, 0, 0);
					}
					if (i + 3 < w && j - 1 > 0) {
						xy2[i + 3][j - 1] = setRGB(0, 0, 0);
					}
					if (i + 4 < w && j - 1 > 0) {
						xy2[i + 4][j - 1] = setRGB(0, 0, 0);
					}
					if (i + 5 < w && j - 1 > 0) {
						xy2[i + 5][j - 1] = setRGB(0, 0, 0);
					}

					if (i - 1 > 0 && j + 1 < h) {
						xy2[i - 1][j + 1] = setRGB(0, 0, 0);
					}
					if (i - 1 > 0 && j + 2 < h) {
						xy2[i - 1][j + 2] = setRGB(0, 0, 0);
					}
					if (i - 1 > 0 && j + 3 < h) {
						xy2[i - 1][j + 3] = setRGB(0, 0, 0);
					}

					if (i - 2 > 0 && j + 1 < h) {
						xy2[i - 2][j + 1] = setRGB(0, 0, 0);
					}
					if (i - 2 > 0 && j + 2 < h) {
						xy2[i - 2][j + 2] = setRGB(0, 0, 0);
					}
					if (i - 2 > 0 && j + 3 < h) {
						xy2[i - 2][j + 3] = setRGB(0, 0, 0);
					}

					if (i - 3 > 0 && j + 1 < h) {
						xy2[i - 3][j + 1] = setRGB(0, 0, 0);
					}
					if (i - 3 > 0 && j + 2 < h) {
						xy2[i - 3][j + 2] = setRGB(0, 0, 0);
					}

					if (i + 2 < w && j + 1 < h) {
						xy2[i + 2][j + 1] = setRGB(0, 0, 0);
					}
					if (i + 2 < w && j + 2 < h) {
						xy2[i + 2][j + 2] = setRGB(0, 0, 0);
					}
					if (i + 2 < w && j + 3 < h) {
						xy2[i + 2][j + 3] = setRGB(0, 0, 0);
					}

					if (i + 3 < w && j + 1 < h) {
						xy2[i + 3][j + 1] = setRGB(0, 0, 0);
					}
					if (i + 3 < w && j + 2 < h) {
						xy2[i + 3][j + 2] = setRGB(0, 0, 0);
					}
					if (i + 3 < w && j + 3 < h) {
						xy2[i + 3][j + 3] = setRGB(0, 0, 0);
					}

					if (i + 4 < w && j + 1 < h) {
						xy2[i + 4][j + 1] = setRGB(0, 0, 0);
					}
					if (i + 4 < w && j + 2 < h) {
						xy2[i + 4][j + 2] = setRGB(0, 0, 0);
					}

					if (i - 1 > 0 && j - 2 > 0) {
						xy2[i - 1][j - 2] = setRGB(0, 0, 0);
					}
					if (i - 1 > 0 && j - 3 > 0) {
						xy2[i - 1][j - 3] = setRGB(0, 0, 0);
					}
					if (i - 1 > 0 && j - 4 > 0) {
						xy2[i - 1][j - 4] = setRGB(0, 0, 0);
					}

					if (i - 2 > 0 && j - 2 > 0) {
						xy2[i - 2][j - 2] = setRGB(0, 0, 0);
					}
					if (i - 2 > 0 && j - 3 > 0) {
						xy2[i - 2][j - 3] = setRGB(0, 0, 0);
					}
					if (i - 2 > 0 && j - 4 > 0) {
						xy2[i - 2][j - 4] = setRGB(0, 0, 0);
					}

					if (i - 3 > 0 && j - 2 > 0) {
						xy2[i - 3][j - 2] = setRGB(0, 0, 0);
					}
					if (i - 3 > 0 && j - 3 > 0) {
						xy2[i - 3][j - 3] = setRGB(0, 0, 0);
					}

					if (i + 2 < w && j - 2 > 0) {
						xy2[i + 2][j - 2] = setRGB(0, 0, 0);
					}
					if (i + 2 < w && j - 3 > 0) {
						xy2[i + 2][j - 3] = setRGB(0, 0, 0);
					}
					if (i + 2 < w && j - 4 > 0) {
						xy2[i + 2][j - 4] = setRGB(0, 0, 0);
					}

					if (i + 3 < w && j - 2 > 0) {
						xy2[i + 3][j - 2] = setRGB(0, 0, 0);
					}
					if (i + 3 < w && j - 3 > 0) {
						xy2[i + 3][j - 3] = setRGB(0, 0, 0);
					}
					if (i + 3 < w && j - 4 > 0) {
						xy2[i + 3][j - 4] = setRGB(0, 0, 0);
					}

					if (i + 4 < w && j - 2 > 0) {
						xy2[i + 4][j - 2] = setRGB(0, 0, 0);
					}
					if (i + 4 < w && j - 3 > 0) {
						xy2[i + 4][j - 3] = setRGB(0, 0, 0);
					}

				}
			}
		}

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgb[aux] = xy2[i][j];

				imagem.setRGB(i, j, rgb[aux]);
				aux++;
			}
		}

		return imagem;
	}

	private static int[][] processaErosao(int[][] matriz, int w, int h) {
		int[][] novaMatriz = new int[w][h];
		for (int i = 1; i < w - 1; i++) {
			for (int j = 1; j < h - 1; j++) {
				int min = Math.min(matriz[i - 1][j], matriz[i][j - 1]);
				min = Math.min(min, matriz[i][j]);
				min = Math.min(min, matriz[i][j + 1]);
				min = Math.min(min, matriz[i + 1][j]);
				novaMatriz[i][j] = min;
			}
		}
		return novaMatriz;
	}

	public BufferedImage erosao(BufferedImage grid) {
		int h = grid.getHeight();
		int w = grid.getWidth();
		getMatrizRGB(grid);
		reds = processaErosao(reds, w, h);
		greens = processaErosao(greens, w, h);
		blues = processaErosao(blues, w, h);

		return setMatrizRGB(h, w, grid);
	}

	private void getMatrizRGB(BufferedImage grid) {
		int width = grid.getWidth();
		int height = grid.getHeight();

		reds = new int[width][height];
		greens = new int[width][height];
		blues = new int[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = grid.getRGB(i, j);
				reds[i][j] = (int) ((rgb & 0x00FF0000) >>> 16);
				greens[i][j] = (int) ((rgb & 0x0000FF00) >>> 8);
				blues[i][j] = (int) (rgb & 0x000000FF);
			}
		}
	}

	private BufferedImage setMatrizRGB(int h, int w, BufferedImage grid) {

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				Color color = new Color(reds[i][j], greens[i][j], blues[i][j]);
				grid.setRGB(i, j, color.getRGB());
			}
		}

		return grid;
	}

	// public BufferedImage mediana(BufferedImage imagemOriginal, int
	// vizinhanca) {
	// BufferedImage imagem = imagemOriginal;
	// Integer h = imagem.getHeight();
	// Integer w = imagem.getWidth();
	// int[][][] matrizImagem = new int[w][h][3];
	//
	// for (int i = 0; i < w; i++) {
	// for (int j = 0; j < h; j++) {
	// int rgb = imagem.getRGB(i, j);
	// matrizImagem[i][j][0] = (rgb >> 16) & 255;
	// matrizImagem[i][j][1] = (rgb >> 8) & 255;
	// matrizImagem[i][j][2] = (rgb) & 255;
	// }
	// }
	//
	// imagem = medianaVizinhanca(vizinhanca, imagemOriginal, matrizImagem);
	//
	// return imagem;
	// }
	//
	// public static BufferedImage medianaVizinhanca(int vizinhanca,
	// BufferedImage imagemOriginal, int[][][] matrizImagem) {
	// Integer h = imagemOriginal.getHeight();
	// Integer w = imagemOriginal.getWidth();
	// int[][][] matrizNovaImagem = new int[w][h][3];
	// int[] somaR = new int[h * w];
	// int[] somaG = new int[h * w];
	// int[] somaB = new int[h * w];
	//
	// int contador = 0;
	//
	// if (vizinhanca % 2 != 0) {
	// vizinhanca--;
	// }
	//
	// int pivo = (h * w) / 2;
	// int limiteLateral = vizinhanca / 2;
	// int limSuplinha, limInflinha, limSupColuna, limInfColuna;
	//
	// for (int i = 0; i < w; i++) {
	// for (int j = 0; j < h; j++) {
	// limInflinha = i - limiteLateral;
	// limSuplinha = i + limiteLateral;
	// limInfColuna = j - limiteLateral;
	// limSupColuna = j + limiteLateral;
	//
	// for (; limInflinha <= limSuplinha; limInflinha++, limInfColuna = j
	// - limiteLateral) {
	// for (; limInfColuna <= limSupColuna; limInfColuna++) {
	// if ((limInflinha >= 0) && (limInfColuna >= 0)
	// && (limInflinha < i) && (limInfColuna < j)) {
	// somaR[contador] = matrizImagem[limInflinha][limInfColuna][0];
	// somaG[contador] = matrizImagem[limInflinha][limInfColuna][1];
	// somaB[contador] = matrizImagem[limInflinha][limInfColuna][2];
	// contador++;
	// }
	//
	// }
	// }
	//
	// Arrays.sort(somaR);
	// Arrays.sort(somaG);
	// Arrays.sort(somaB);
	//
	// int medianaR = (somaR[pivo] + somaR[pivo - 1]) / 2;
	// int medianaG = (somaG[pivo] + somaG[pivo - 1]) / 2;
	// int medianaB = (somaB[pivo] + somaB[pivo - 1]) / 2;
	//
	// matrizNovaImagem[i][j][0] = medianaR;
	// matrizNovaImagem[i][j][1] = medianaG;
	// matrizNovaImagem[i][j][2] = medianaB;
	//
	// contador = 0;
	// }
	// }
	//
	// BufferedImage novaImagem = new BufferedImage(w, h,
	// BufferedImage.TYPE_3BYTE_BGR);
	// for (int i = 0; i < w; i++) {
	// for (int j = 0; j < h; j++) {
	// int r = ((matrizNovaImagem[i][j][0])) & 255;
	// int g = ((matrizNovaImagem[i][j][1])) & 255;
	// int b = (matrizNovaImagem[i][j][2]) & 255;
	// int rgb = (r << 16) | (g << 8) | (b);
	// novaImagem.setRGB(i, j, rgb);
	// }
	// }
	//
	// return novaImagem;
	// }

	public BufferedImage mediana(BufferedImage imagem, int masc) {
		int aux;
		int w = imagem.getWidth();
		int h = imagem.getHeight();
		int xy[][] = new int[w][h];
		int xy2[][] = new int[w][h];
		int rgb[] = getRgb(imagem, w, h);
		ArrayList<Integer> pixel = new ArrayList<>();

		int tamanhomediana = masc;
		aux = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				xy[i][j] = (rgb[aux]);
				aux++;
			}
		}

		aux = 0;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {

				int round = Math.abs(tamanhomediana / 2);

				for (int k = i - round; k < i + round; k++) {
					for (int l = j - round; l < j + round; l++) {
						if (k > -1 && k < w && l > -1 && l < h) {
							pixel.add(xy[k][l]);

						}
					}
				}
				Collections.sort(pixel);
				xy2[i][j] = pixel.get(Math.round(pixel.size() / 2));
				// pixel = new ArrayList<>();
				pixel.removeAll(pixel);
			}
		}

		aux = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgb[aux] = xy2[i][j];
				aux++;
			}
		}

		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
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
