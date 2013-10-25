package br.unioeste.pid.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import br.unioeste.pid.imagem.ImagePanel;
import br.unioeste.pid.utils.PixelUtils;
import br.unioeste.pid.view.TelaOperacoesView;

public class TelaOperacoes extends TelaOperacoesView {

	private static final int OR = 0;
	private static final int XOR = 1;
	private static final int NOT = 2;
	private static final int AND = 3;
	private String[] nomes;
	private JTabbedPane painel;
	private PixelUtils utils = new PixelUtils();
	
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
				OR();
				break;
			case XOR:
				XOR();
				break;
			case NOT:
				NOT();
				break;
			case AND:
				AND();
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
		BufferedImage negada = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		int width = grid.getWidth();
		int height = grid.getHeight();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = grid.getRGB(i, j); // a cor inversa é dado por 255
												// menos o valor da cor
				int r = 255 - (int) ((rgb & 0x00FF0000) >>> 16);
				int g = 255 - (int) ((rgb & 0x0000FF00) >>> 8);
				int b = 255 - (int) (rgb & 0x000000FF);
				Color color = new Color(r, g, b);
				negada.setRGB(i, j, color.getRGB());
			}
		}
		imagePanel.setGrid(negada);
		imagePanel.update();

	}

	private void OR() {
		int imagem1 = cbImagem1.getSelectedIndex();
		int imagem2 = cbImagem2.getSelectedIndex();

		JScrollPane scrollPanel1 = (JScrollPane) painel.getComponentAt(imagem1);
		JScrollPane scrollPanel2 = (JScrollPane) painel.getComponentAt(imagem2);

		ImagePanel imagePanel1 = (ImagePanel) scrollPanel1.getViewport().getView();
		ImagePanel imagePanel2 = (ImagePanel) scrollPanel2.getViewport().getView();

		BufferedImage grid1 = imagePanel1.getGrid();
		BufferedImage grid2 = imagePanel2.getGrid();

		if (grid1.getHeight() == grid2.getHeight() && grid1.getWidth() == grid2.getWidth()) {
			BufferedImage retorno = new BufferedImage(grid1.getWidth(), grid1.getHeight(), grid1.getType());	
			for (int i = 0; i < grid1.getHeight(); i++) {
				for (int j = 0; j < grid1.getWidth(); j++) {
					int rgb = grid1.getRGB(j, i) | grid2.getRGB(j, i);
					retorno.setRGB(j, i, rgb);
					
				}
			}
			ImagePanel img = new ImagePanel();
			img.setGrid(retorno);
			JScrollPane scroll = new JScrollPane(img);
			utils.addClosableTab(painel, scroll, "Nova Imagem", null);
			img.update();
		}
		

	}
	
	private void XOR(){
		int imagem1 = cbImagem1.getSelectedIndex();
		int imagem2 = cbImagem2.getSelectedIndex();

		JScrollPane scrollPanel1 = (JScrollPane) painel.getComponentAt(imagem1);
		JScrollPane scrollPanel2 = (JScrollPane) painel.getComponentAt(imagem2);

		ImagePanel imagePanel1 = (ImagePanel) scrollPanel1.getViewport().getView();
		ImagePanel imagePanel2 = (ImagePanel) scrollPanel2.getViewport().getView();

		BufferedImage grid1 = imagePanel1.getGrid();
		BufferedImage grid2 = imagePanel2.getGrid();

		if (grid1.getHeight() == grid2.getHeight() && grid1.getWidth() == grid2.getWidth()) {
			BufferedImage retorno = new BufferedImage(grid1.getWidth(), grid1.getHeight(), grid1.getType());	
			for (int i = 0; i < grid1.getHeight(); i++) {
				for (int j = 0; j < grid1.getWidth(); j++) {
					int rgb = grid1.getRGB(j, i) ^ grid2.getRGB(j, i);
					retorno.setRGB(j, i, rgb);
					
				}
			}
			ImagePanel img = new ImagePanel();
			img.setGrid(retorno);
			JScrollPane scroll = new JScrollPane(img);
			utils.addClosableTab(painel, scroll, "Nova Imagem", null);
			img.update();
		}
	}
	
	private void AND(){
		int imagem1 = cbImagem1.getSelectedIndex();
		int imagem2 = cbImagem2.getSelectedIndex();

		JScrollPane scrollPanel1 = (JScrollPane) painel.getComponentAt(imagem1);
		JScrollPane scrollPanel2 = (JScrollPane) painel.getComponentAt(imagem2);

		ImagePanel imagePanel1 = (ImagePanel) scrollPanel1.getViewport().getView();
		ImagePanel imagePanel2 = (ImagePanel) scrollPanel2.getViewport().getView();

		BufferedImage grid1 = imagePanel1.getGrid();
		BufferedImage grid2 = imagePanel2.getGrid();

		if (grid1.getHeight() == grid2.getHeight() && grid1.getWidth() == grid2.getWidth()) {
			BufferedImage retorno = new BufferedImage(grid1.getWidth(), grid1.getHeight(), grid1.getType());	
			for (int i = 0; i < grid1.getHeight(); i++) {
				for (int j = 0; j < grid1.getWidth(); j++) {
					int rgb = grid1.getRGB(j, i) & grid2.getRGB(j, i);
					retorno.setRGB(j, i, rgb);
					
				}
			}
			ImagePanel img = new ImagePanel();
			img.setGrid(retorno);
			JScrollPane scroll = new JScrollPane(img);
			utils.addClosableTab(painel, scroll, "Nova Imagem", null);
			img.update();
		}
	}

}
