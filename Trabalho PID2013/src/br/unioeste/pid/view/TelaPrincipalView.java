package br.unioeste.pid.view;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JScrollPane;

public class TelaPrincipalView extends JFrame {

	protected JMenuBar menuBar;
	protected JMenu mnArquivo;
	protected JMenuItem mntmAbrir;
	protected JMenuItem mntmSalvar;
	protected JMenu mnImagem;
	protected JMenuItem mntmGreyscale;
	protected JMenu mnExibir;
	protected JMenuItem mntmHistograma;
	protected JTabbedPane tabbedPane;
	protected JScrollPane scrollPane;
	protected JMenu mnFiltro;
	protected JMenuItem mntmPassaalta;
	protected JMenuItem mntmOperaes;
	protected JMenuItem mntmLimiarizao;
	
	public TelaPrincipalView() {
		initGUI();
	}
	
	private void initGUI() {
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnArquivo = new JMenu("Arquivo");
		menuBar.add(mnArquivo);
		
		mntmAbrir = new JMenuItem("Abrir");
		mnArquivo.add(mntmAbrir);
		
		mntmSalvar = new JMenuItem("Salvar");
		mnArquivo.add(mntmSalvar);
		
		mnImagem = new JMenu("Imagem");
		menuBar.add(mnImagem);
		
		mntmGreyscale = new JMenuItem("GreyScale");
		mnImagem.add(mntmGreyscale);
		
		this.mntmOperaes = new JMenuItem("Opera\u00E7\u00F5es");
		this.mnImagem.add(this.mntmOperaes);
		
		this.mntmLimiarizao = new JMenuItem("Limiariza\u00E7\u00E3o");
		this.mnImagem.add(this.mntmLimiarizao);
		
		this.mnFiltro = new JMenu("Filtro");
		this.mnImagem.add(this.mnFiltro);
		
		this.mntmPassaalta = new JMenuItem("Passa-Alta");
		this.mnFiltro.add(this.mntmPassaalta);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		this.scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		getContentPane().add(this.scrollPane, gbc_scrollPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.scrollPane.setViewportView(this.tabbedPane);
	}

}
