package br.unioeste.pid.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;

import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.unioeste.pid.imagem.ImagePanel;
import br.unioeste.pid.view.TelaLimiarizacaoView;

public class TelaLimiarizacao extends TelaLimiarizacaoView {

	public TelaLimiarizacao(ImagePanel imagePanel) {
		setTitle("Limiarização");
		setMinimumSize(new Dimension(330, 135));
		btnOk.addActionListener(new ActionOk(imagePanel));
		btnCancelar.addActionListener(new ActionCancelar());
		jsLimiar.addChangeListener(limiarListener());
	}

	private ChangeListener limiarListener(){
		return new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				jtfValor.setText(jsLimiar.getValue()+"");
			}
		};
	}
	
	private class ActionOk extends AbstractAction {
		ImagePanel image;

		public ActionOk(ImagePanel image) {
			this.image = image;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BufferedImageOp grayscaleConv = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
			BufferedImage src = grayscaleConv.filter(image.getGrid(), null);
			image.setGrid(src);
			limiar(image, jsLimiar.getValue());
			dispose();
		}

	}

	private class ActionCancelar extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}

	}

	public void limiar(ImagePanel imagePanel, int limiar) {
		BufferedImage image = imagePanel.getGrid();
		BufferedImage imageLimiar = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		int width = image.getWidth();
		int height = image.getHeight();
		imagePanel.reset();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = image.getRGB(i, j);
				int r = (int) ((rgb & 0x00FF0000) >>> 16);
				int g = (int) ((rgb & 0x0000FF00) >>> 8);
				int b = (int) (rgb & 0x000000FF);
				int media = (r + g + b) / 3;
				Color white = new Color(255, 255, 255);
				Color black = new Color(0, 0, 0);
				if (media < limiar)
					imageLimiar.setRGB(i, j, black.getRGB());
				else
					imageLimiar.setRGB(i, j, white.getRGB());
			}
		}
		imagePanel.setGrid(imageLimiar);
		imagePanel.update();
	}
}
