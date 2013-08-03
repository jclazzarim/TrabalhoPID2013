package br.unioeste.pid.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;

import br.unioeste.pid.histograma.PainelHistograma;
import br.unioeste.pid.imagem.Image;
import br.unioeste.pid.imagem.pixel.MPixel;
import br.unioeste.pid.view.TelaPrincipalView;

public class TelaPrincipal extends TelaPrincipalView {
	private static final long serialVersionUID = 1L;
	private File file;
	private Image Arquivo;

	public TelaPrincipal() {
		mntmHistograma.addActionListener(new ActionHistograma());

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new TelaPrincipalView().setVisible(true);
			}
		});
	}

	public static void main(String[] args) {
		new TelaPrincipal().setVisible(true);
	}

	private class ActionHistograma extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			MPixel mPixel = Arquivo.getmPixel();
			PainelHistograma histogramdemo1 = new PainelHistograma(
					"Histograma", mPixel.getHistograma());
			histogramdemo1.pack();
			RefineryUtilities.centerFrameOnScreen(histogramdemo1);
			histogramdemo1.setVisible(true);
		}
	}

	private class ActionSalvar extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			Arquivo = new Image();
			String path = "C:\\Users\\J.C\\Desktop\\imgs";
			JFileChooser j = new JFileChooser(path);
			int oi = j.showOpenDialog(null);
			if (oi == JFileChooser.APPROVE_OPTION) {
				file = j.getSelectedFile();
			} else {
				return;
			}
			try {
				Arquivo.ReadFile(new FileInputStream(file));
			} catch (FileNotFoundException ex) {
				Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE,
						null, ex);
				JOptionPane.showMessageDialog(null,
						"Erro Fatal: Não foi possível abrir o arquivo da imagem.\n"
								+ ex);
			}
			 imagePanel = new JPanel();
			tabbedPane.add(imagePanel);
			imagePanel.setPreferredSize(new Dimension(Arquivo.getWidth(),
					Arquivo.getHeight()));
			imagePanel.setSize(new Dimension(Arquivo.getWidth(), Arquivo
					.getHeight()));
			imagePanel.setImagem(Arquivo);
			imagePanel.reset();
			imagePanel.update();
			// this.setSize(new Dimension((Arquivo.getWidth() + 22),
			// (Arquivo.getHeight() + 67)));
			if (!Arquivo.isP8bits()) {
				jmiGrayScale.setEnabled(true);
				// jmFiltro.setEnabled(true);
			}

		}
	}
}
