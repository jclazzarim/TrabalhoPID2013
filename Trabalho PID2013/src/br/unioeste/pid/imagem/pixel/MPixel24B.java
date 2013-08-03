package br.unioeste.pid.imagem.pixel;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.data.xy.IntervalXYDataset;

public class MPixel24B implements MPixel {

	Color MPixel[][];
	int width, height;
	boolean grayScale;
	byte[] cabecalho;

	public MPixel24B(int height, int width, byte[] cabeca) {
		this.width = width;
		this.height = height;
		MPixel = new Color[height][width];
		grayScale = false;
		cabecalho = cabeca;
	}

	@Override
	public void lerImagem(FileInputStream fis) throws IOException {
		int erro = (width) % 4;
		byte[] bErro = new byte[1];
		byte[] rgb;
		ExecutorService pool = Executors.newFixedThreadPool(200);
		for (int i = (height - 1); i >= 0; i--) {
			rgb = new byte[width * 3];
			fis.read(rgb);
			pool.execute(new Read24BThread(rgb, MPixel[i]));
			for (int k = 0; k < erro; k++) {
				fis.read(bErro);
			}
		}
		pool.shutdown();
	}

	@Override
	public void pintarImagem(Graphics g) {
		for (int i = 0; i < (height); i++) {
			for (int j = 0; j < (width); j++) {
				g.setColor(MPixel[i][j]);
				g.drawLine(j, i, j, i);
			}
		}
	}

	@Override
	public void grayScale() {
		for (int i = 0; i < (height); i++) {
			for (int j = 0; j < (width); j++) {
				MPixel[i][j] = new Color(
						(int) ((MPixel[i][j].getRed() * 0.299)
								+ (MPixel[i][j].getGreen() * 0.587) + (MPixel[i][j]
								.getBlue() * 0.114)),
						(int) ((MPixel[i][j].getRed() * 0.299)
								+ (MPixel[i][j].getGreen() * 0.587) + (MPixel[i][j]
								.getBlue() * 0.114)),
						(int) ((MPixel[i][j].getRed() * 0.299)
								+ (MPixel[i][j].getGreen() * 0.587) + (MPixel[i][j]
								.getBlue() * 0.114)));
			}
		}
		grayScale = true;
	}

	@Override
	public void passaBaixa(int value) {
		if (!grayScale) {
			grayScale();
		}

		int cor[][] = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				cor[i][j] = MPixel[i][j].getRed();
			}
		}

		double factor = 1 / Math.pow(value, 2);
		// double factor = 0.5;

		double mask[][] = new double[value][value];

		int walk = (int) value / 2;
		int walkX, walkY;
		double color;
		int fator = 0;
		// Percorre imagem
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				color = 0;
				fator = 0;
				// percorre máscara
				for (int k = 0; k < value; k++) {
					for (int l = 0; l < value; l++) {
						walkX = i - (k - walk);
						walkY = j - (l - walk);
						if (walkX < 0 || walkX >= height || walkY < 0
								|| walkY >= width) {
							continue;
						}
						color += (cor[walkX][walkY]);
						fator++;
					}
				}
				color = color / fator;
				MPixel[i][j] = new Color((int) color, (int) color, (int) color);
			}
		}

	}

	@Override
	public void passaAlta() {
		if (!grayScale) {
			grayScale();
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
			color = Math.sqrt(Math.pow(cor[height - 1][i]
					- cor[height - 1][i + 1], 2) * 2);
			MPixel[height - 1][i] = new Color((int) color, (int) color,
					(int) color);

		}

		// ultima coluna
		for (int i = 0; i < height - 1; i++) {
			color = Math.sqrt(Math.pow(cor[i][width - 1]
					- cor[i + 1][width - 1], 2) * 2);
			MPixel[i][width - 1] = new Color((int) color, (int) color,
					(int) color);
		}
	}

	@Override
	public void salvarImagem(File img) {
		try (FileOutputStream fos = new FileOutputStream(img)) {
            fos.write(cabecalho);
            int erro = (width) % 4;
            byte[] bErro = new byte[1];

            for (int i = height - 1; i >= 0; i--) {
                for (int j = 0; j < width; j++) {
                    fos.write(MPixel[i][j].getBlue());
                    fos.write(MPixel[i][j].getGreen());
                    fos.write(MPixel[i][j].getRed());

                }
                for (int k = 0; k < erro; k++) {
                    fos.write(bErro);
                }
            }
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(MPixel8B.class.getName()).log(Level.SEVERE, null, ex);
        }

	}


	@Override
	public IntervalXYDataset getHistograma() {
//		 HistogramDataset histogramdataset = new HistogramDataset();
//	        double red[] = new double[256];
//	        double blue[] = new double[256];
//	        double green[] = new double[256];
//
//	        for (int i = 0; i < histograma.size(); i++) {
//	            red[i] = histograma.get(i).getR();
//	            green[i] = histograma.get(i).getG();
//	            blue[i] = histograma.get(i).getB();
//	        }
//
//	        histogramdataset.addSeries("Vermelho", red, 100, 0, 256);
//	        histogramdataset.addSeries("Azul", green, 100, 0, 256);
//	        histogramdataset.addSeries("Verde", blue, 100, 0, 256);
//	        
//
//	        return histogramdataset;
		return null;
	}

}
