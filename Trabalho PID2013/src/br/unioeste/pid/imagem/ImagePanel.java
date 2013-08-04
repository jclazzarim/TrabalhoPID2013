package br.unioeste.pid.imagem;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	Image imagem;
	private BufferedImage grid;

	public ImagePanel() {
		imagem = null;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		if (getGrid() == null && imagem != null) {

			setGrid((BufferedImage) (this.createImage(imagem.getWidth(),
					imagem.getHeight())));
			imagem.getmPixel().pintarImagem(getGrid().createGraphics());

		}

		g2.drawImage(getGrid(), null, 0, 0);

		// if (imagem != null) {
		// imagem.getmPixel().paintPixel(g, imagem.getHeight(),
		// imagem.getWidth());
		// }

	}

	public void update() {
		this.paintComponent(this.getGraphics());

	}

	public Image getImagem() {
		return imagem;
	}

	public void setImagem(Image imagem) {
		this.imagem = imagem;
	}

	public void reset() {
		setGrid(null);
	}

	public BufferedImage getGrid() {
		return grid;
	}

	public void setGrid(BufferedImage grid) {
		this.grid = grid;
	}
}
