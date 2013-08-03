package br.unioeste.pid.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import br.unioeste.pid.view.TelaOperacoesView;

public class TelaOperacoes extends TelaOperacoesView {

	private static final int OR = 0;
	private static final int XOR = 1;
	private static final int NOT = 2;
	private static final int AND = 3;

	public TelaOperacoes() {

		setMinimumSize(new Dimension(460, 130));
		btnCancelar.addActionListener(new ActionCancelar());
		btnOk.addActionListener(new ActionOk());
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
}
