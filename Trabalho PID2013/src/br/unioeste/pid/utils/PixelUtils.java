package br.unioeste.pid.utils;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;

import javax.imageio.ImageTypeSpecifier;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import com.jgoodies.looks.common.RGBGrayFilter;
import com.pearsoneduc.ip.op.FFTException;
import com.pearsoneduc.ip.op.ImageFFT;

public class PixelUtils {
	private static final Icon CLOSE_TAB_ICON = new ImageIcon(
			"libs\\closeTabButton.png", null);
	private int[][] reds;
	private int[][] greens;
	private int[][] blues;

	public PixelUtils() {
	}

	public BufferedImage abertura(BufferedImage grid){
		return dilatacao(erosao(grid));
	}

	public BufferedImage fechamento(BufferedImage grid){
		return erosao(dilatacao(grid));
	}
	
	public BufferedImage passaAlta(BufferedImage src) {
		BufferedImageOp grayscaleConv = new ColorConvertOp(
				ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		src = grayscaleConv.filter(src, null);

		float f = (float) 0.5;
		ImageFFT fft = null;
		try {
			fft = new ImageFFT(src);
			fft.transform();
			fft.butterworthHighPassFilter(f);
			fft.transform();
			return fft.toImage(null);
		} catch (FFTException e) {
			e.printStackTrace();
			return null;
		}

	}

	public void addClosableTab(final JTabbedPane tabbedPane,
			final JComponent c, final String title, final Icon icon) {
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
		btnClose.setIcon(RGBGrayFilter
				.getDisabledIcon(btnClose, CLOSE_TAB_ICON));

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
		InputMap inputMap = c
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		// Add the key binding for the keystroke to the action name
		inputMap.put(controlW, "closeTab");

		// Now add a single binding for the action name to the anonymous action
		c.getActionMap().put("closeTab", closeTabAction);
	}

	private void getMatrizRGB(BufferedImage grid) {
		int width = grid.getWidth();
		int height = grid.getHeight();

		reds = new int[width][height];
		greens = new int[width][height];
		blues = new int[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = grid.getRGB(i, j);
				reds[i][j] = (int) ((rgb & 0x00FF0000) >>> 16);
				greens[i][j] = (int) ((rgb & 0x0000FF00) >>> 8);
				blues[i][j] = (int) (rgb & 0x000000FF);
			}
		}
	}

	private BufferedImage setMatrizRGB(int h, int w, BufferedImage grid) {

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				Color color = new Color(reds[i][j], greens[i][j], blues[i][j]);
				grid.setRGB(i, j, color.getRGB());
			}
		}

		return grid;
	}

	public BufferedImage dilatacao(BufferedImage grid) {
		getMatrizRGB(grid);
		int h = grid.getHeight();
		int w = grid.getWidth();
		reds = processaDilatacao(reds, w, h);
		greens = processaDilatacao(greens, w, h);
		blues = processaDilatacao(blues, w, h);

		return setMatrizRGB(h, w, grid);
	}

	private int[][] processaDilatacao(int[][] cor, int w, int h) {
		int[][] novaMatriz = new int[w][h];
		for (int i = 1; i < w - 1; i++) {
			for (int j = 1; j < h - 1; j++) {
				int max = Math.max(cor[i - 1][j - 1], cor[i - 1][j]);
				max = Math.max(max, cor[i - 1][j + 1]);
				max = Math.max(max, cor[i][j - 1]);
				max = Math.max(max, cor[i][j]);
				max = Math.max(max, cor[i][j + 1]);
				max = Math.max(max, cor[i + 1][j - 1]);
				max = Math.max(max, cor[i + 1][j]);
				max = Math.max(max, cor[i + 1][j + 1]);
				novaMatriz[i][j] = max;
			}
		}
		return novaMatriz;
	}

	public BufferedImage erosao(BufferedImage grid) {
		int h = grid.getHeight();
		int w = grid.getWidth();
		getMatrizRGB(grid);
		reds = processaErosao(reds, w, h);
		greens = processaErosao(greens, w, h);
		blues = processaErosao(blues, w, h);

		return setMatrizRGB(h, w, grid);
	}

	private int[][] processaErosao(int[][] cor, int w, int h) {
		int[][] novaMatriz = new int[w][h];
		for (int i = 1; i < w - 1; i++) {
			for (int j = 1; j < h - 1; j++) {
				int min = Math.min(cor[i - 1][j - 1], cor[i - 1][j]);
				min = Math.min(min, cor[i - 1][j + 1]);
				min = Math.min(min, cor[i][j - 1]);
				min = Math.min(min, cor[i][j]);
				min = Math.min(min, cor[i][j + 1]);
				min = Math.min(min, cor[i + 1][j - 1]);
				min = Math.min(min, cor[i + 1][j]);
				min = Math.min(min, cor[i + 1][j + 1]);
				novaMatriz[i][j] = min;
			}
		}
		return novaMatriz;
	}

}
