package br.unioeste.pid.imagem.pixel;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.data.xy.IntervalXYDataset;

public class MPixel8B implements MPixel {

	byte[] cabecalho;
	Color[] paleta;
	int[] MPixel;
	int width;
	int height;

	// ArrayList<Histograma> histograma;

	public MPixel8B(int width, int height, byte[] cabeca) {
		// this.histograma = new ArrayList<>();
		MPixel = new int[(width * height)];
		paleta = new Color[256];
		this.width = width;
		this.height = height;
		cabecalho = cabeca;
	}

	@Override
	public void lerImagem(FileInputStream fis) throws IOException {
		// Histograma hist;
		// Paleta
		byte[] rgb = new byte[4];
		for (int i = 0; i < 256; i++) {
			fis.read(rgb);
			paleta[i] = new Color(((byte) rgb[2] & 0xFF), ((byte) rgb[1] & 0xFF), ((byte) rgb[0] & 0xFF));
			// hist = new Histograma(i);
			// histograma.add(hist);
		}

		int erro = width % 4;
		byte[] bErro = new byte[1];
		// Pixels
		rgb = new byte[1];
		int locuri = 0;
		for (int i = ((height * width) - 1); i >= 0; i--) {
			fis.read(rgb);
			MPixel[i] = (byte) rgb[0] & 0xFF;
			// histograma.get(paleta[MPixel[i]].getRed()).incR();
			// // System.out.println(paleta[MPixel[i]].getRed());
			// histograma.get(paleta[MPixel[i]].getGreen()).incG();
			// // System.out.println(paleta[MPixel[i]].getGreen());
			// histograma.get(paleta[MPixel[i]].getBlue()).incB();
			// // System.out.println(paleta[MPixel[i]].getBlue());
			locuri++;
			if (locuri == width) {
				for (int k = 0; k < erro; k++) {
					fis.read(bErro);
				}
				locuri = 0;
			}
		}
		// System.out.println("pixel: " + height * width);
		// int x = 0;
		// for (int i = 0; i < histograma.size(); i++) {
		// System.out.println(i + " : " + histograma.get(i).getR());
		// x += histograma.get(i).getR();
		// }
		// System.out.println("X : " + x);
	}

	@Override
	public void pintarImagem(Graphics g) {
		int c = 0;
		for (int i = 0; i < (height - 1); i++) {
			for (int j = width; j > 0; j--) {
				g.setColor(paleta[MPixel[c]]);
				g.drawLine(j, i, j, i);
				c++;
			}
		}
	}

	@Override
	public void grayScale() {
		for (int i = 0; i < paleta.length; i++) {
			paleta[i] = new Color(
					(int) ((paleta[i].getRed() * 0.299) + (paleta[i].getGreen() * 0.587) + (paleta[i].getBlue() * 0.114)),
					(int) ((paleta[i].getRed() * 0.299) + (paleta[i].getGreen() * 0.587) + (paleta[i].getBlue() * 0.114)),
					(int) ((paleta[i].getRed() * 0.299) + (paleta[i].getGreen() * 0.587) + (paleta[i].getBlue() * 0.114)));
		}
	}

	@Override
	public void passaBaixa(int value) {
		throw new UnsupportedOperationException("Not supported yet.");

	}

	@Override
	public void passaAlta() {
		grayScale();
		Color[][] MPixel = new Color[height][width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				MPixel[i][j] = paleta[this.MPixel[i+j]];
			}
		}
		
		int cor[][] = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				cor[i][j] = MPixel[i][j].getRed();
			}
		}

		double color;

		// Percorre imagem
		for (int i = 0; i < (height - 1); i++) {
			for (int j = 0; j < (width - 1); j++) {

				color = Math.sqrt(Math.pow(cor[i][j] - cor[i + 1][j + 1], 2)
						+ Math.pow(cor[i + 1][j] - cor[i][j + 1], 2));

				if (color > 255) {
					color = 255;
				}

				MPixel[i][j] = new Color((int) color, (int) color, (int) color);
			}
		}

		// ultima linha
		for (int i = 0; i < width - 1; i++) {
			color = Math.sqrt(Math.pow(cor[height - 1][i] - cor[height - 1][i + 1], 2) * 2);
			MPixel[height - 1][i] = new Color((int) color, (int) color, (int) color);

		}

		// ultima coluna
		for (int i = 0; i < height - 1; i++) {
			color = Math.sqrt(Math.pow(cor[i][width - 1] - cor[i + 1][width - 1], 2) * 2);
			MPixel[i][width - 1] = new Color((int) color, (int) color, (int) color);
		}
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				
				paleta[this.MPixel[i+j]] = MPixel[i][j];
			}
		}
	}

	@Override
	public void salvarImagem(File img) {
		try (FileOutputStream fos = new FileOutputStream(img)) {
			fos.write(cabecalho);
			for (int i = 0; i < paleta.length; i++) {

				fos.write(paleta[i].getBlue());
				fos.write(paleta[i].getGreen());
				fos.write(paleta[i].getRed());
				fos.write(0);
			}
			int erro = width % 4;
			byte[] bErro = new byte[1];
			int locuri = 0;
			for (int i = MPixel.length - 1; i >= 0; i--) {
				fos.write(MPixel[i]);
				locuri++;
				if (locuri == width) {
					for (int k = 0; k < erro; k++) {
						fos.write(bErro);
					}
					locuri = 0;
				}
			}
			fos.flush();
			fos.close();
		} catch (IOException ex) {
			Logger.getLogger(MPixel8B.class.getName()).log(Level.SEVERE, null, ex);
		}
		System.out.println("FIMSAVE");
	}

	@Override
	public IntervalXYDataset getHistograma() {
		// HistogramDataset histogramdataset = new HistogramDataset();
		// double red[] = new double[MPixel.length];
		//
		//
		// for (int i = 0; i < MPixel.length; i++) {
		// red[i] = paleta[MPixel[i]].getRed();
		// }
		//
		//
		// histogramdataset.addSeries("Vermelho", red, 256);
		// histogramdataset.addSeries("Verde", blue, 100, 0, 256);
		//
		//
		// return histogramdataset;
		return null;
	}

}
