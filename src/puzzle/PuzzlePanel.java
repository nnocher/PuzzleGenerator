package puzzle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import main.Main;

/**
 * Drawing surface for puzzle
 * @author Nico Nocher
 *
 */

public class PuzzlePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final float minZoom = 0.01f;
	private final float maxZoom = 10f;
	private final float zoomFactor = 1.2f;
	
	public float zoom = 1.0f;
	public Dimension targetDrawDim;
	
	public Color bgColor = Color.black;
	
	public PuzzlePanel() {
		super();
	}
	
	/**
	 * Returns current zoom
	 * @return
	 */
	public float getZoom() {
		return zoom;
	}
	
	/**
	 * Sets zoom to given zoom
	 * @param zoom
	 */
	public void setZoom(float zoom) {
		this.zoom = zoom;
		if(zoom < minZoom)
			zoom = minZoom;
		if(zoom > maxZoom)
			zoom = maxZoom;
		
		// Resize panel
		Dimension newSize = Main.puzzleController.getDesiredPanelDim();
		newSize = new Dimension((int) (newSize.width * zoom + 0.5f), (int) (newSize.height * zoom + 0.5f));
		targetDrawDim = newSize;
		setPreferredSize(targetDrawDim);
	}
	
	/**
	 * Zoom in
	 * @return
	 */
	public boolean zoomIn() {
		if(zoom * zoomFactor < maxZoom) {
			setZoom(zoom * zoomFactor);	// Set new zoom
			System.out.println("Zoom: " + zoom);
			update();
		} else
			return false;
		return true;
	}
	
	/**
	 * Zoom out
	 * @return
	 */
	public boolean zoomOut() {
		if(zoom / zoomFactor > minZoom) {
			setZoom(zoom / zoomFactor);	// Set new zoom
			System.out.println("Zoom: " + zoom);
			update();
		} else
			return false;
		return true;
	}
	
	/**
	 * Repaint panel
	 */
	public void update() {
		repaint();
	}
	
	/**
	 * Draw panel
	 */
	public void paint(Graphics g) {
		// Clears panel
		g.setColor(bgColor);
		g.fillRect(0, 0, super.getWidth(), super.getHeight());
		
		if(Main.puzzleController.hasPuzzle())
			Main.puzzleController.paint(g);
	}
}
