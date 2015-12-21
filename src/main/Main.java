package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

import puzzle.ControlPanel;
import puzzle.PuzzleController;
import puzzle.PuzzlePanel;
import settings.SettingsContainer;
import settings.SettingsWriter;

/**
 * Main class
 * @author Nico Nocher
 *
 */

public class Main {
	
	public static final String version = "v0.5";
	
	public static JFrame frame;
	public static JMenuBar menuBar;
	public static JScrollPane puzzleScrollPane;
	public static Animation animation;
	
	public static PuzzleController puzzleController;
	
	public static SettingsContainer generalSettings;
	public static SettingsContainer stringContainer;
	
	public static void main(String[] args) {
		System.out.println("-----------------------------------------");
		String line = "|";
		String text = "Puzzle Generator " + Main.version;
		int length = (text.length() - 2) / 2;
		for(int i = 0; i < length; i++) {
			line += " ";
		}
		line += text;
		while(line.length() < 40)
			line += " ";
		System.out.println(line + "|");
		System.out.println("|           Nico Nocher, 2014           |");
		System.out.println("-----------------------------------------");
		
		new Main();
	}
	
	public Main() {
		System.out.println("Initializing...");
		
		// load all settings
		loadSettings();
		
		// Set up frame
		System.out.println("Initializing Frame...");
		frame = new JFrame(stringContainer.getValue("TITLE"));
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) { } 
			@Override
			public void windowClosed(WindowEvent arg0) { } 
			@Override
			public void windowClosing(WindowEvent arg0) {
				exit();
			} 
			@Override
			public void windowDeactivated(WindowEvent arg0) { } 
			@Override
			public void windowDeiconified(WindowEvent arg0) { } 
			@Override
			public void windowIconified(WindowEvent arg0) { } 
			@Override
			public void windowOpened(WindowEvent arg0) { }
		});
		setupComponents();
		frame.pack();
		
		System.out.println("Initialization Complete!");
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/**
	 * Loads settings and stringContainer
	 */
	private void loadSettings() {
		System.out.println("Loading Settings...");
		System.out.println("\tLoading General Settings...");
		generalSettings = new SettingsContainer("data/config.cfg");
		System.out.println("\tLoading Strings...");
		stringContainer = new SettingsContainer(generalSettings.getValue("StringContainer"));
		System.out.println("Loading Settings Complete!");
	}
	
	/**
	 * Initializes and adds components to frame
	 */
	private void setupComponents() {
		System.out.println("Initializing up Components...");
		
		// Set up JMenuBar
		menuBar = new MainMenuBar();
		frame.setJMenuBar(menuBar);
		
		// Set up main panels
		ControlPanel controlPanel = new ControlPanel();
		PuzzlePanel puzzlePanel = new PuzzlePanel();
		Main.puzzleController = new PuzzleController(puzzlePanel, controlPanel);
		
		// Places puzzlePanel in scroll pane and sets up the pane size
		puzzleScrollPane = new JScrollPane(puzzlePanel);
		int w, h;
		try {
			w = Integer.parseInt(generalSettings.getValue("PUZZLEPANE_DEFAULTSIZE_WIDTH"));
			h = Integer.parseInt(generalSettings.getValue("PUZZLEPANE_DEFAULTSIZE_HEIGHT"));
		} catch(Exception e) {
			w = 600;
			h = 400;
		}
		PuzzleController.defaultScrollPaneSize = new Dimension(w, h);
		puzzleScrollPane.setPreferredSize(PuzzleController.defaultScrollPaneSize);
		frame.add(controlPanel, BorderLayout.WEST);
		frame.add(puzzleScrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the title of the frame
	 * @return
	 */
	public static String getTitle() {
		return frame.getTitle();
	}
	
	/**
	 * Sets the title of the frame
	 * @param title
	 */
	public static void setTitle(String title) {
		frame.setTitle(title);
	}
	
	/**
	 * Exits application and saves settings
	 */
	public static void exit() {
		System.out.println("Exiting...");
		try {
			SettingsWriter.WriteSettings(generalSettings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
