package br.unioeste.pid.controller;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.jfree.ui.RefineryUtilities;

import br.unioeste.pid.histograma.PainelHistograma;
import br.unioeste.pid.imagem.Image;
import br.unioeste.pid.imagem.ImagePanel;
import br.unioeste.pid.imagem.pixel.MPixel;
import br.unioeste.pid.utils.PixelUtils;
import br.unioeste.pid.utils.TelaUtils;
import br.unioeste.pid.view.TelaPrincipalView;

public class TelaPrincipal extends TelaPrincipalView {
	private static final long serialVersionUID = 1L;
	private File file;
	private Image Arquivo;
	private PixelUtils pixelUtils = new PixelUtils();
	private TelaUtils telaUtils = new TelaUtils();

	public TelaPrincipal() {
		setPreferredSize(new Dimension(800, 600));
		setMinimumSize(new Dimension(800, 600));
		// mntmHistograma.addActionListener(new ActionHistograma());
		mntmAbrir.addActionListener(new ActionAbrir());
		mntmSalvar.addActionListener(new ActionSalvar());
		mntmGreyscale.addActionListener(new ActionGreyScale());
		mntmRoberts.addActionListener(new ActionPassaAlta());
		mntmOperaes.addActionListener(new ActionOperacoes());
		mntmLimiarizao.addActionListener(new ActionLimiarizacao());
		mntmDilatao.addActionListener(new ActionDilatacao());
		mntmEroso.addActionListener(new ActionErosao());
		mntmAbertura.addActionListener(new ActionAbertura());
		mntmFechamento.addActionListener(new ActionFechamento());
		mntmProcessamento.addActionListener(new ActionProcessamento());
		mntmMediana.addActionListener(new ActionMediana());

	}

	public static void main(String[] args) {
		new TelaPrincipal().setVisible(true);
	}

	private class ActionMediana extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int selectedIndex = tabbedPane.getSelectedIndex();
			JScrollPane scrollPanel = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
			ImagePanel imagePanel = (ImagePanel) scrollPanel.getViewport().getView();
			imagePanel.setGrid(telaUtils.mediana(imagePanel.getGrid(),3));
			imagePanel.update();
			System.out.println("Finale Mediana");
		}
		
	}
	
	private class ActionDilatacao extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int selectedIndex = tabbedPane.getSelectedIndex();
			JScrollPane scrollPanel = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
			ImagePanel imagePanel = (ImagePanel) scrollPanel.getViewport().getView();
			imagePanel.setGrid(telaUtils.dilatacaoBola(imagePanel.getGrid()));
			imagePanel.update();
			System.out.println("Finale");

		}

	}

	private class ActionAbertura extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int selectedIndex = tabbedPane.getSelectedIndex();
			JScrollPane scrollPanel = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
			ImagePanel imagePanel = (ImagePanel) scrollPanel.getViewport().getView();
			imagePanel.setGrid(pixelUtils.abertura(imagePanel.getGrid()));
			imagePanel.update();
			System.out.println("Finale");

		}

	}

	private class ActionFechamento extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int selectedIndex = tabbedPane.getSelectedIndex();
			JScrollPane scrollPanel = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
			ImagePanel imagePanel = (ImagePanel) scrollPanel.getViewport().getView();
			imagePanel.setGrid(pixelUtils.fechamento(imagePanel.getGrid()));
			imagePanel.update();
			System.out.println("Finale");

		}

	}

	private class ActionErosao extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int selectedIndex = tabbedPane.getSelectedIndex();
			JScrollPane scrollPanel = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
			ImagePanel imagePanel = (ImagePanel) scrollPanel.getViewport().getView();
			imagePanel.setGrid(pixelUtils.erosao(imagePanel.getGrid()));
			imagePanel.update();
			System.out.println("Finale");
		}

	}

	private class ActionLimiarizacao extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int selectedIndex = tabbedPane.getSelectedIndex();
			JScrollPane scrollPanel = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
			ImagePanel imagePanel = (ImagePanel) scrollPanel.getViewport().getView();
			new TelaLimiarizacao(imagePanel).setVisible(true);
		}

	}

	private class ActionHistograma extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			MPixel mPixel = Arquivo.getmPixel();
			PainelHistograma histogramdemo1 = new PainelHistograma("Histograma", mPixel.getHistograma());
			histogramdemo1.pack();
			RefineryUtilities.centerFrameOnScreen(histogramdemo1);
			histogramdemo1.setVisible(true);
		}
	}

	private class ActionAbrir extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Arquivo = new Image();
			String path = "D:\\Dropbox\\CC Unioeste\\[4] PID - Processamento de Imagem Digital\\bmps_para_teste";
			JFileChooser j = new JFileChooser(path);
			int oi = j.showOpenDialog(null);
			if (oi == JFileChooser.APPROVE_OPTION) {
				file = j.getSelectedFile();
			} else {
				return;
			}
			try {
				Arquivo.ReadFile(new FileInputStream(file));
				final ImagePanel imagePanel = new ImagePanel();
				JScrollPane scroll = new JScrollPane();
				scroll.setViewportView(imagePanel);
				pixelUtils.addClosableTab(tabbedPane, scroll, file.getName(), null);
				imagePanel.setName(file.getName());
				imagePanel.setPreferredSize(new Dimension(Arquivo.getWidth(), Arquivo.getHeight()));
				imagePanel.setSize(new Dimension(Arquivo.getWidth(), Arquivo.getHeight()));
				imagePanel.setImagem(Arquivo);
				imagePanel.reset();
				imagePanel.update();
				if (!Arquivo.isP8bits()) {
					mntmGreyscale.setEnabled(true);
				}
			} catch (FileNotFoundException ex) {
				Logger.getLogger(TelaPrincipalView.class.getName()).log(Level.SEVERE, null, ex);
				JOptionPane.showMessageDialog(null, "Erro Fatal: N�o foi poss�vel abrir o arquivo da imagem.\n" + ex);
			}

		}
	}

	private class ActionSalvar extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Arquivo != null) {
				String path = "";
				String ext = ".bmp";
				JFileChooser j = new JFileChooser();
				File img = null;
				int oi = j.showSaveDialog(null);
				if (oi == JFileChooser.APPROVE_OPTION) {
					path = j.getSelectedFile().getAbsoluteFile().toString();
					System.out.println(path);
				} else {
					return;
				}
				int auxinc = 1;

				// cria file com nome certo
				while (true) {
					img = new File(path + ext);
					if (!img.exists()) {
						try {
							img.createNewFile();
							img.setWritable(true);
							img.setReadable(true);
							img.setExecutable(true);
							break;
						} catch (IOException ex) {
							Logger.getLogger(TelaPrincipalView.class.getName()).log(Level.SEVERE, null, ex);
						}
						System.out.println("N�o Salvou");
						break;
					} else {
						path = path + auxinc;
						auxinc++;
					}
				}
				int selectedIndex = tabbedPane.getSelectedIndex();
				JScrollPane scroll = (JScrollPane) tabbedPane.getComponentAt(selectedIndex);
				ImagePanel imagePanel = (ImagePanel) scroll.getViewport().getView();
				BufferedImage grid = imagePanel.getGrid();
				try {
					ImageIO.write(grid, "bmp", img);
					System.out.println("Salvou");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}

	}

	private class ActionGreyScale extends AbstractAction {
		ImagePanel imagePanel;

		@Override
		public void actionPerformed(ActionEvent e) {
			JScrollPane scroll = (JScrollPane) tabbedPane.getSelectedComponent();
			imagePanel = (ImagePanel) scroll.getViewport().getView();
			BufferedImage src = pixelUtils.greyScale(imagePanel.getGrid());
			imagePanel.setGrid(src);
			imagePanel.update();
		}

	}

	private class ActionPassaAlta extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JScrollPane scrollPanel = (JScrollPane) tabbedPane.getSelectedComponent();
			ImagePanel imagePanel = (ImagePanel) scrollPanel.getViewport().getView();

			BufferedImage grid = imagePanel.getGrid();
			grid = pixelUtils.greyScale(grid);
			grid = telaUtils.sobel(grid);
			imagePanel.setGrid(grid);
			imagePanel.update();
		}

	}

	private class ActionOperacoes extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new TelaOperacoes(tabbedPane).setVisible(true);

		}
	}

	private class ActionProcessamento extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JScrollPane scrollPanel = (JScrollPane) tabbedPane.getSelectedComponent();
			ImagePanel imagePanel = (ImagePanel) scrollPanel.getViewport().getView();

			BufferedImage grid = imagePanel.getGrid();
			ImagePanel painel = new ImagePanel();
			painel.setGrid(telaUtils.verificaImagem(grid));
			JScrollPane scroll = new JScrollPane(painel);
			pixelUtils.addClosableTab(tabbedPane, scroll, "Mascara", null);
			painel.update();
		}

	}

}
