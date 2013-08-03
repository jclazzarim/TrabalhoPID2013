package br.unioeste.pid.view;

import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.DefaultComboBoxModel;

public class TelaOperacoesView extends JDialog {
	protected JLabel lblImagem;
	protected JLabel lblImagem_1;
	protected JComboBox cbImagem1;
	protected JComboBox cbImagem2;
	protected JLabel lblE;
	protected JPanel panel;
	protected JComboBox cbOperacoes;
	protected JLabel lblOperao;
	protected JButton btnOk;
	protected JButton btnCancelar;

	public TelaOperacoesView() {
		initGUI();
	}

	private void initGUI() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 49, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);
		{
			this.lblImagem = new JLabel("Imagem 1");
			GridBagConstraints gbc_lblImagem = new GridBagConstraints();
			gbc_lblImagem.insets = new Insets(0, 0, 5, 5);
			gbc_lblImagem.gridx = 0;
			gbc_lblImagem.gridy = 0;
			getContentPane().add(this.lblImagem, gbc_lblImagem);
		}
		{
			this.lblImagem_1 = new JLabel("Imagem 2");
			GridBagConstraints gbc_lblImagem_1 = new GridBagConstraints();
			gbc_lblImagem_1.insets = new Insets(0, 0, 5, 0);
			gbc_lblImagem_1.gridx = 2;
			gbc_lblImagem_1.gridy = 0;
			getContentPane().add(this.lblImagem_1, gbc_lblImagem_1);
		}
		{
			this.cbImagem1 = new JComboBox();
			GridBagConstraints gbc_cbImagem1 = new GridBagConstraints();
			gbc_cbImagem1.insets = new Insets(0, 0, 5, 5);
			gbc_cbImagem1.fill = GridBagConstraints.HORIZONTAL;
			gbc_cbImagem1.gridx = 0;
			gbc_cbImagem1.gridy = 1;
			getContentPane().add(this.cbImagem1, gbc_cbImagem1);
		}
		{
			this.lblE = new JLabel("e");
			GridBagConstraints gbc_lblE = new GridBagConstraints();
			gbc_lblE.insets = new Insets(0, 0, 5, 5);
			gbc_lblE.anchor = GridBagConstraints.EAST;
			gbc_lblE.gridx = 1;
			gbc_lblE.gridy = 1;
			getContentPane().add(this.lblE, gbc_lblE);
		}
		{
			this.cbImagem2 = new JComboBox();
			GridBagConstraints gbc_cbImagem2 = new GridBagConstraints();
			gbc_cbImagem2.insets = new Insets(0, 0, 5, 0);
			gbc_cbImagem2.fill = GridBagConstraints.HORIZONTAL;
			gbc_cbImagem2.gridx = 2;
			gbc_cbImagem2.gridy = 1;
			getContentPane().add(this.cbImagem2, gbc_cbImagem2);
		}
		{
			this.panel = new JPanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.gridwidth = 3;
			gbc_panel.insets = new Insets(0, 0, 0, 5);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 2;
			getContentPane().add(this.panel, gbc_panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 247, 0, 0, 0, 0, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			this.panel.setLayout(gbl_panel);
			{
				this.lblOperao = new JLabel("Opera\u00E7\u00E3o");
				GridBagConstraints gbc_lblOperao = new GridBagConstraints();
				gbc_lblOperao.insets = new Insets(0, 0, 5, 5);
				gbc_lblOperao.gridx = 0;
				gbc_lblOperao.gridy = 0;
				this.panel.add(this.lblOperao, gbc_lblOperao);
			}
			{
				this.cbOperacoes = new JComboBox();
				this.cbOperacoes.setModel(new DefaultComboBoxModel(new String[] { "OR", "XOR", "NOT", "AND" }));
				this.cbOperacoes.setSelectedIndex(0);
				GridBagConstraints gbc_cbOperacoes = new GridBagConstraints();
				gbc_cbOperacoes.insets = new Insets(0, 0, 0, 5);
				gbc_cbOperacoes.fill = GridBagConstraints.HORIZONTAL;
				gbc_cbOperacoes.gridx = 0;
				gbc_cbOperacoes.gridy = 1;
				this.panel.add(this.cbOperacoes, gbc_cbOperacoes);
			}
			{
				this.btnOk = new JButton("Ok");
				GridBagConstraints gbc_btnOk = new GridBagConstraints();
				gbc_btnOk.insets = new Insets(0, 0, 0, 5);
				gbc_btnOk.gridx = 3;
				gbc_btnOk.gridy = 1;
				this.panel.add(this.btnOk, gbc_btnOk);
			}
			{
				this.btnCancelar = new JButton("Cancelar");
				GridBagConstraints gbc_btnCancelar = new GridBagConstraints();
				gbc_btnCancelar.gridx = 4;
				gbc_btnCancelar.gridy = 1;
				this.panel.add(this.btnCancelar, gbc_btnCancelar);
			}
		}
	}

}
