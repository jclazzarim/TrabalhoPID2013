package br.unioeste.pid.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TelaUtils {

	private int[][] reds;
	private int[][] greens;
	private int[][] blues;

	public BufferedImage verificaImagem(BufferedImage imagem, int x, int y) {
		PixelUtils utils = new PixelUtils();

		BufferedImage greyScale = utils.greyScale(imagem);
		int[] rgb = getRgb(greyScale, imagem.getWidth(), imagem.getHeight());
		int w = imagem.getWidth();
		int h = imagem.getHeight();

		int rr = 0;

		for (int i = 0; i < w * h; i++) {
			rr += getRed(rgb[i]);
		}
		rr = (int) (rr / (w * h));

		TransformadaHough hough;
		if (rr > 100) {
			
			return processaCelulaRoxa(imagem);
		} else {
			return processaCelulaColorida(imagem, x, y);
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

	private int pegaFundo(int[] r, int[] g, int[] b) {
		int soma = 0;
		int mR = 0;
		int mG = 0;
		int mB = 0;

		for (int i = 0; i < r.length; i++) {
			soma += r[i];
		}
		mR = soma;

		soma = 0;
		for (int i = 0; i < g.length; i++) {
			soma += g[i];
		}
		mG = soma;

		soma = 0;
		for (int i = 0; i < b.length; i++) {
			soma += b[i];
		}
		mB = soma;

		float mg = mG / 255;
		float mb = mB / 255;
		float mr = mR / 255;

		if (mg > mb && mg > mr) {
			return 2;
		}
		if (mb > mg && mb > mr) {
			return 3;
		}
		if (mr > mb && mr > mg) {
			return 1;
		}

		return 2;
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

	public BufferedImage processaCelulaColorida(final BufferedImage imagem, int x, int y) {

		int w = imagem.getWidth();
		int h = imagem.getHeight();
		int[] rgb = getRgb(imagem, w, h);
		int aux = 0;
		// int[] corCelula = verificaImagem(imagem);
		int[] corCelula = getColorImagem(x, y, imagem);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				imagem.setRGB(i, j, setRGB(corCelula[aux], 0, 0));
				aux++;
			}
		}

		PixelUtils utils = new PixelUtils();

		BufferedImage grid = utils.greyScale(imagem);
		grid = otsu(grid);
		grid = utils.erosao(grid);
		grid = sobel(grid);
		grid = negar(grid);
		List<Circulo> circulos = getCirculos(grid);
		for (Circulo circulo : circulos) {
			System.out.println("X: " + circulo.getX() + " Y: " + circulo.getY() + " Raio: " + circulo.getRaio());
		}
		return grid;
	}

	private List<Circulo> getCirculos(BufferedImage imagem) {
		List<Circulo> circulos = new ArrayList<>();

		for (int i = 0; i < imagem.getWidth(); i++) {
			for (int j = 0; j < imagem.getHeight(); j++) {
				Color cor = intToColor(imagem.getRGB(i, j));
				if (cor.equals(Color.BLACK)) {// compara com o fundo branco
					for (int raio = 5; raio < imagem.getWidth(); raio++) {
						if (raio > i || raio > j || (raio + i) >= imagem.getWidth() || (raio + j) >= imagem.getHeight()) {
							continue;
						}
						Color pBordaDireita = intToColor(imagem.getRGB(i + raio, j));
						Color pBordaEsquerda = intToColor(imagem.getRGB(i - raio, j));
						Color pBordaCima = intToColor(imagem.getRGB(i, j - raio));
						Color pBordaBaixo = intToColor(imagem.getRGB(i, j + raio));
						if (pBordaDireita.equals(Color.BLACK))
							if (pBordaEsquerda.equals(Color.BLACK))
								if (pBordaCima.equals(Color.BLACK))
									if (pBordaBaixo.equals(Color.BLACK)) {
										circulos.add(new Circulo(i, j, raio));
									}
					}
				}
			}
		}

		return circulos;
	}

	private Circulo detectaQuadrado(BufferedImage imagem) {
		Circulo pBE = null;
		boolean fim = false;
		for (int i = 0; i < imagem.getWidth(); i++) {
			for (int j = 0; j < imagem.getHeight(); j++) {
				Color cor = intToColor(imagem.getRGB(i, j));
				if (cor.equals(Color.BLACK)) {
					pBE = new Circulo(i, j, 0);
					fim = true;
					break;
				}
			}
			if(fim){
				break;
			}
		}
		
		Circulo pBD = null;
		for (int i = pBE.getX(); i < imagem.getWidth(); i++) {
			Color cor = intToColor(imagem.getRGB(i, pBE.getY()));
			if(cor.equals(Color.BLACK)){
				pBD = new Circulo(i, pBE.getY(), i-pBE.getX());
				break;
			}
		}

		Circulo pBC = null;
		int raio = pBD.getRaio()/2;
		for (int i = raio; i > 0; i--) {
			Color cor = intToColor(imagem.getRGB(i, pBE.getY()));
			if(cor.equals(Color.BLACK)){
				pBC = new Circulo(raio, i, 0);
				break;
			}
		}

		Circulo pBB = null;
		for (int i = raio; i < imagem.getHeight(); i++) {
			Color cor = intToColor(imagem.getRGB(i, pBE.getY()));
			if(cor.equals(Color.BLACK)){
				pBB = new Circulo(raio, i, 0);
				break;
			}
		}
		
		
		return pBE;
	}

	private Color intToColor(int rgb) {
		int b = ((rgb & 0x00FF0000) >>> 16);
		int g = ((rgb & 0x0000FF00) >>> 8);
		int r = (rgb & 0x000000FF);
		return new Color(r, g, b);
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

	private BufferedImage processaCelulaRoxa(BufferedImage imagem) {
		int[] rgb = getRgb(imagem, imagem.getWidth(), imagem.getHeight());
		int[] greens = getGreens(rgb);
		int aux = 0;
		BufferedImage grid = new BufferedImage(imagem.getWidth(), imagem.getHeight(), imagem.getType());
		for (int i = 0; i < imagem.getWidth(); i++) {
			for (int j = 0; j < imagem.getHeight(); j++) {
				grid.setRGB(i, j, setRGB(greens[aux], 0, 0));
				aux++;
			}
		}

		PixelUtils utils = new PixelUtils();
		BufferedImage greyScale = utils.greyScale(grid);
		greyScale = negar(greyScale);
		greyScale = utils.limiar(greyScale, 215);
		greyScale = utils.abertura(greyScale);
		greyScale = utils.fechamento(greyScale);
		greyScale = erosao(greyScale);
		greyScale = erosao(greyScale);
		greyScale = erosao(greyScale);
		greyScale = sobel(greyScale);
		greyScale = negar(greyScale);
		greyScale = utils.limiar(greyScale, 250);

		return greyScale;
	}

	private BufferedImage negar(BufferedImage grid) {
		BufferedImage negada = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < grid.getWidth(); i++) {
			for (int j = 0; j < grid.getHeight(); j++) {
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

}
