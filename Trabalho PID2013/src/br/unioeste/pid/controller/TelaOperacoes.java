package br.unioeste.pid.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import br.unioeste.pid.imagem.Image;
import br.unioeste.pid.imagem.ImagePanel;
import br.unioeste.pid.imagem.pixel.MPixel;
import br.unioeste.pid.view.TelaOperacoesView;

public class TelaOperacoes extends TelaOperacoesView {

	private static final int OR = 0;
	private static final int XOR = 1;
	private static final int NOT = 2;
	private static final int AND = 3;
	private String[] nomes;
	private JTabbedPane painel;

	public TelaOperacoes(JTabbedPane painel) {
		this.painel = painel;
		setNomes();
		setMinimumSize(new Dimension(460, 130));
		btnCancelar.addActionListener(new ActionCancelar());
		btnOk.addActionListener(new ActionOk());
		cbImagem1.setModel(new DefaultComboBoxModel<>(nomes));
		cbImagem2.setModel(new DefaultComboBoxModel<>(nomes));
	}

	private void setNomes() {
		nomes = new String[painel.getTabCount()];
		for (int i = 0; i < painel.getTabCount(); i++) {
			nomes[i] = painel.getTitleAt(i);
		}
	}

	private class ActionCancelar extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}

	}

	private class ActionOk extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (cbOperacoes.getSelectedIndex()) {

			case OR:
				System.out.println("OR");
				break;
			case XOR:
				System.out.println("XOR");
				break;
			case NOT:
				NOT();
				System.out.println("NOT");
				break;
			case AND:
				System.out.println("AND");
				break;
			default:
				break;
			}
			dispose();
		}

	}

	private void NOT() {
		int imagemSelecioanda = cbImagem1.getSelectedIndex();
		JScrollPane scrollPanel = (JScrollPane) painel.getComponentAt(imagemSelecioanda);
		ImagePanel imagePanel = (ImagePanel) scrollPanel.getViewport().getView();
		
		BufferedImage grid = imagePanel.getGrid();
		int width = grid.getWidth();
		int height = grid.getHeight();
		imagePanel.reset();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = grid.getRGB(i, j); // a cor inversa é dado por 255
												// menos o valor da cor
				int r = 255 - (int) ((rgb & 0x00FF0000) >>> 16);
				int g = 255 - (int) ((rgb & 0x0000FF00) >>> 8);
				int b = 255 - (int) (rgb & 0x000000FF);
				Color color = new Color(r, g, b);
				grid.setRGB(i, j, color.getRGB());
			}
		}
		imagePanel.setGrid(grid);
		imagePanel.update();

	}

}
