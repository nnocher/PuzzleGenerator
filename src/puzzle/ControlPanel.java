package puzzle;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import main.Main;

/**
 * Contains controls for puzzle
 * @author Nico Nocher
 *
 */

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public ControlPanel() {
		super();
	}
	
	/**
	 * Sets up components
	 */
	public void setupComponents() {
		this.setPreferredSize(new Dimension(400, 100));
		
		JButton zoomIn = new JButton("+");
		zoomIn.addActionListener(zoomIn_ActionListener);
		add(zoomIn, BorderLayout.WEST);
		JButton zoomOut = new JButton("-");
		zoomOut.addActionListener(zoomOut_ActionListener);
		add(zoomOut, BorderLayout.EAST);
	}
	
	//// ActionListeners ////
	
	ActionListener zoomIn_ActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Main.puzzleController.puzzlePanel.zoomIn();
		}
	};
	
	ActionListener zoomOut_ActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Main.puzzleController.puzzlePanel.zoomOut();
		}
	};
}
