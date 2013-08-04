package br.unioeste.pid.controller;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import org.jfree.ui.RefineryUtilities;

import br.unioeste.pid.histograma.PainelHistograma;
import br.unioeste.pid.imagem.Image;
import br.unioeste.pid.imagem.ImagePanel;
import br.unioeste.pid.imagem.pixel.MPixel;
import br.unioeste.pid.view.TelaPrincipalView;

import com.jgoodies.looks.common.RGBGrayFilter;

public class TelaPrincipal extends TelaPrincipalView {
	private static final Icon CLOSE_TAB_ICON = new ImageIcon("libs\\closeTabButton.png", null);
	private static final long serialVersionUID = 1L;
	private File file;
	private Image Arquivo;

	public TelaPrincipal() {
		setPreferredSize(new Dimension(800, 600));
		setMinimumSize(new Dimension(800, 600));
		mntmHistograma.addActionListener(new ActionHistograma());
		mntmAbrir.addActionListener(new ActionAbrir());
		mntmSalvar.addActionListener(new ActionSalvar());
		mntmGreyscale.addActionListener(new ActionGreyScale());
		mntmPassaalta.addActionListener(new ActionPassaAlta());
		mntmOperaes.addActionListener(new ActionOperacoes());
		mntmLimiarizao.addActionListener(new ActionLimiarizacao());
	}

	public static void main(String[] args) {
		new TelaPrincipal().setVisible(true);
	}
	
	private class ActionLimiarizacao extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JScrollPane scrollPanel = (JScrollPane) tabbedPane.getSelectedComponent();
			ImagePanel imagePanel = (ImagePanel) scrollPanel.getViewport().getView();
			new TelaLimiarizacao(imagePanel).setVisible(true);;
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
				ImagePanel imagePanel = new ImagePanel();
				JScrollPane scroll = new JScrollPane();
				scroll.setViewportView(imagePanel);
				addClosableTab(tabbedPane, scroll, file.getName(), null);
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
				JOptionPane.showMessageDialog(null, "Erro Fatal: Não foi possível abrir o arquivo da imagem.\n" + ex);
			}

		}
	}

	private class ActionSalvar extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Arquivo != null) {
				String path = "C:\\Users\\J.C\\Desktop\\imgs\\";
				String name = "";
				String ext = ".bmp";
				JFileChooser j = new JFileChooser(path);
				File img = null;
				int oi = j.showSaveDialog(null);
				if (oi == JFileChooser.APPROVE_OPTION) {
					name = j.getSelectedFile().getName();

				} else {
					return;
				}
				int auxinc = 1;

				String nameAux = name;

				// cria file com nome certo
				while (true) {
					img = new File(path + nameAux + ext);
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
						System.out.println("FODEO");
						break;
					} else {
						nameAux = name + auxinc;
						auxinc++;
					}
				}

				Arquivo.Salvar(img);
			}

		}

	}

	private class ActionGreyScale extends AbstractAction {
		ImagePanel imagePanel;

		@Override
		public void actionPerformed(ActionEvent e) {
			JScrollPane scroll = (JScrollPane) tabbedPane.getSelectedComponent();
			imagePanel = (ImagePanel) scroll.getViewport().getView();
			if (imagePanel != null && Arquivo != null) {
				Arquivo.getmPixel().grayScale();
				imagePanel.reset();
				imagePanel.update();
			} else {
				System.out.println("FODEO");
			}
		}

	}

	private class ActionPassaAlta extends AbstractAction {
		ImagePanel imagePanel;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JScrollPane scroll = (JScrollPane) tabbedPane.getSelectedComponent();
			imagePanel = (ImagePanel) scroll.getViewport().getView();
			if (imagePanel != null && Arquivo != null) {
				Arquivo.getmPixel().passaAlta();
				imagePanel.reset();
				imagePanel.update();
			} else {
				System.out.println("FODEO");
			}
		}

	}

	private class ActionOperacoes extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new TelaOperacoes(tabbedPane).setVisible(true);
			
		}
		
	}

	public void addClosableTab(final JTabbedPane tabbedPane, final JComponent c, final String title, final Icon icon) {
		// Add the tab to the pane without any label
		tabbedPane.addTab(title, c);
		int pos = tabbedPane.indexOfComponent(c);

		// Create a FlowLayout that will space things 5px apart
		FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 0);

		// Make a small JPanel with the layout and make it non-opaque
		JPanel pnlTab = new JPanel(f);
		pnlTab.setOpaque(false);

		// Add a JLabel with title and the left-side tab icon
		JLabel lblTitle = new JLabel(title);
		lblTitle.setIcon(icon);

		// Create a JButton for the close tab button
		JButton btnClose = new JButton();
		btnClose.setOpaque(false);

		// Configure icon and rollover icon for button
		btnClose.setRolloverIcon(CLOSE_TAB_ICON);
		btnClose.setRolloverEnabled(true);
		btnClose.setIcon(RGBGrayFilter.getDisabledIcon(btnClose, CLOSE_TAB_ICON));

		// Set border null so the button doesn't make the tab too big
		btnClose.setBorder(null);

		// Make sure the button can't get focus, otherwise it looks funny
		btnClose.setFocusable(false);

		// Put the panel together
		pnlTab.add(lblTitle);
		pnlTab.add(btnClose);

		// Add a thin border to keep the image below the top edge of the tab
		// when the tab is selected
		pnlTab.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		// Now assign the component for the tab
		tabbedPane.setTabComponentAt(pos, pnlTab);

		// Add the listener that removes the tab
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// The component parameter must be declared "final" so that it
				// can be
				// referenced in the anonymous listener class like this.
				tabbedPane.remove(c);
			}
		};
		btnClose.addActionListener(listener);

		// Optionally bring the new tab to the front
		tabbedPane.setSelectedComponent(c);

		// -------------------------------------------------------------
		// Bonus: Adding a <Ctrl-W> keystroke binding to close the tab
		// -------------------------------------------------------------
		AbstractAction closeTabAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.remove(c);
			}
		};

		// Create a keystroke
		KeyStroke controlW = KeyStroke.getKeyStroke("control W");

		// Get the appropriate input map using the JComponent constants.
		// This one works well when the component is a container.
		InputMap inputMap = c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		// Add the key binding for the keystroke to the action name
		inputMap.put(controlW, "closeTab");

		// Now add a single binding for the action name to the anonymous action
		c.getActionMap().put("closeTab", closeTabAction);
	}

}
