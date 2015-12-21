package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileNameExtensionFilter;

import puzzle.PuzzlePiece;

/**
 * Controller for JMenuBar
 * @author Nico Nocher
 *
 */

public class MainMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;

	public MainMenuBar() {
		super();
		setupComponents();
	}
	
	private JMenuItem debugButton;
	
	/**
	 * Sets up all components of the menu
	 */
	private void setupComponents() {
		JMenu data = new JMenu(Main.stringContainer.getValue("MENUBAR_DATA"));
			JMenu data_New = new JMenu(Main.stringContainer.getValue("MENUBAR_DATA_NEW"));
				JMenuItem data_New_Browse = new JMenuItem(Main.stringContainer.getValue("MENUBAR_DATA_NEW_BROWSE"));
				data_New_Browse.addActionListener(data_New_Browse_ActionListener);
				data_New.add(data_New_Browse);
				JMenuItem data_New_ImportURL = new JMenuItem(Main.stringContainer.getValue("MENUBAR_DATA_NEW_IMPORTURL"));
				data_New_ImportURL.addActionListener(data_New_ImportURL_ActionListener);
				data_New.add(data_New_ImportURL);
			data.add(data_New);
			data.add(new JSeparator());
			JMenuItem data_Load = new JMenuItem(Main.stringContainer.getValue("MENUBAR_DATA_LOAD"));
			data_Load.addActionListener(data_Load_ActionListener);
			data.add(data_Load);
			JMenuItem data_Save = new JMenuItem(Main.stringContainer.getValue("MENUBAR_DATA_SAVE"));
			data_Save.addActionListener(data_Save_ActionListener);
			data.add(data_Save);
			data.add(new JSeparator());
			JMenuItem data_Quit = new JMenuItem(Main.stringContainer.getValue("MENUBAR_DATA_QUIT"));
			data_Quit.addActionListener(data_Quit_ActionListener);
			data.add(data_Quit);
		add(data);
		
		JMenu debug = new JMenu(Main.stringContainer.getValue("MENUBAR_DEBUG"));
			if(PuzzlePiece.debugDraw)
				debugButton = new JMenuItem(Main.stringContainer.getValue("MENUBAR_DEBUG_TOGGLE_TRUE"));
			else
				debugButton = new JMenuItem(Main.stringContainer.getValue("MENUBAR_DEBUG_TOGGLE_FALSE"));
			debugButton.addActionListener(debug_Toggle_ActionListener);
			debug.add(debugButton);
		add(debug);
	}
	
	//// Events ////
	
	// Data Menu
	// data -> new -> browse
	private ActionListener data_New_Browse_ActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Creates new puzzle, Browse for image
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "jpe", "jfif", "bmp", "png", "gif"));
			int returnVal = chooser.showOpenDialog(Main.frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				// Get number of pieces
				int numPieces = -1;
				while(numPieces <= 0) {
					try {
						numPieces = Integer.parseInt((String)JOptionPane.showInputDialog(Main.frame, "How many Pieces?", "Puzzle Setup", JOptionPane.PLAIN_MESSAGE));
					} catch(Exception ex) {
						numPieces = -1;
					}
					if(numPieces <=0)
						JOptionPane.showMessageDialog(Main.frame, "Please enter an integer above 0");
				}
				
				try {
					// Initialize animation and generatePuzzle
					Main.animation = new Animation(chooser.getSelectedFile().toURI().toURL());
					Main.puzzleController.generatePuzzle(numPieces);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	};
	// data -> load
	private ActionListener data_Load_ActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
		}
	};
	// data -> save
	private ActionListener data_Save_ActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
		}
	};
	// data -> new -> importurl
	private ActionListener data_New_ImportURL_ActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Creates new puzzle, gets image from url location
			String urlString;
			
			// URL dialog box
			urlString = JOptionPane.showInputDialog(Main.frame, "URL: ");
			if(urlString == null || urlString.equals(""))
				return;
			
			// Get number of pieces
			int numPieces = -1;
			while(numPieces <= 0) {
				try {
					numPieces = Integer.parseInt((String)JOptionPane.showInputDialog(Main.frame, "How many Pieces?", "Puzzle Setup", JOptionPane.PLAIN_MESSAGE));
				} catch(Exception ex) {
					numPieces = -1;
				}
				if(numPieces <=0)
					JOptionPane.showMessageDialog(Main.frame, "Please enter an integer above 0");
			}
			
			try {
				// Initialize animation and generatePuzzle
				Main.animation = new Animation(new URL(urlString));
				Main.puzzleController.generatePuzzle(numPieces);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	// data -> quit
	private ActionListener data_Quit_ActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Main.exit();
		}
	};
	
	// debug -> toggle
	private ActionListener debug_Toggle_ActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Toggles debug mode on/off
			if(PuzzlePiece.debugDraw) {
				PuzzlePiece.debugDraw = false;
				debugButton.setText(Main.stringContainer.getValue("MENUBAR_DEBUG_TOGGLE_FALSE"));
			} else {
				PuzzlePiece.debugDraw = true;
				debugButton.setText(Main.stringContainer.getValue("MENUBAR_DEBUG_TOGGLE_TRUE"));
			}
			
			Main.puzzleController.puzzlePanel.update();
		}
	};
}
