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
import br.unioeste.pid.utils.PixelUtils;
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
			PixelUtils utils = new PixelUtils();
			BufferedImage src = utils.greyScale(image.getGrid());
			image.setGrid(src);
			src = utils.limiar(image.getGrid(), jsLimiar.getValue());
			image.setGrid(src);
			image.update();
			dispose();
		}

	}

	private class ActionCancelar extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}

	}

	
}
