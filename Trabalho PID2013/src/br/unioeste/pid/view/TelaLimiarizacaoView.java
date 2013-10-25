package br.unioeste.pid.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class TelaLimiarizacaoView extends JDialog {
	protected JLabel lblAr;
	protected JSlider jsLimiar;
	protected JPanel panel;
	protected JButton btnOk;
	protected JButton btnCancelar;
	protected JTextField jtfValor;
	
	public TelaLimiarizacaoView() {
		initGUI();
	}
	private void initGUI() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		{
			this.lblAr = new JLabel("Limiar");
			GridBagConstraints gbc_lblAr = new GridBagConstraints();
			gbc_lblAr.insets = new Insets(0, 0, 5, 0);
			gbc_lblAr.gridx = 0;
			gbc_lblAr.gridy = 0;
			getContentPane().add(this.lblAr, gbc_lblAr);
		}
		{
			this.jsLimiar = new JSlider();
			this.jsLimiar.setPaintLabels(true);
			this.jsLimiar.setMajorTickSpacing(50);
			this.jsLimiar.setMinorTickSpacing(5);
			this.jsLimiar.setMaximum(255);
			this.jsLimiar.setValue(127);
			GridBagConstraints gbc_jsLimiar = new GridBagConstraints();
			gbc_jsLimiar.insets = new Insets(0, 0, 5, 0);
			gbc_jsLimiar.gridx = 0;
			gbc_jsLimiar.gridy = 1;
			getContentPane().add(this.jsLimiar, gbc_jsLimiar);
		}
		{
			this.panel = new JPanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 2;
			getContentPane().add(this.panel, gbc_panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{0, 0, 89, 0};
			gbl_panel.rowHeights = new int[]{0, 0};
			gbl_panel.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			this.panel.setLayout(gbl_panel);
			{
				this.btnOk = new JButton("Ok");
				GridBagConstraints gbc_btnOk = new GridBagConstraints();
				gbc_btnOk.insets = new Insets(0, 0, 0, 5);
				gbc_btnOk.gridx = 0;
				gbc_btnOk.gridy = 0;
				this.panel.add(this.btnOk, gbc_btnOk);
			}
			{
				this.jtfValor = new JTextField();
				this.jtfValor.setEnabled(false);
				GridBagConstraints gbc_jtfValor = new GridBagConstraints();
				gbc_jtfValor.insets = new Insets(0, 0, 0, 5);
				gbc_jtfValor.fill = GridBagConstraints.HORIZONTAL;
				gbc_jtfValor.gridx = 1;
				gbc_jtfValor.gridy = 0;
				this.panel.add(this.jtfValor, gbc_jtfValor);
				this.jtfValor.setColumns(10);
			}
			{
				this.btnCancelar = new JButton("Cancelar");
				GridBagConstraints gbc_btnCancelar = new GridBagConstraints();
				gbc_btnCancelar.gridx = 2;
				gbc_btnCancelar.gridy = 0;
				this.panel.add(this.btnCancelar, gbc_btnCancelar);
			}
		}
	}

}
