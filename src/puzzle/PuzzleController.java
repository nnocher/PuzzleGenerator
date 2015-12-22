package puzzle;

import generator.PuzzleGenerator;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import main.Main;
import puzzle.pieces.PuzzlePiece;

/**
 * Controls the Puzzle
 * @author Nico Nocher
 *
 */

public class PuzzleController {
	public static Dimension defaultScrollPaneSize;
	
	public PuzzleGenerator puzzleGen;
	private PuzzlePiece[][] puzzleMap;
	
	public PuzzlePanel puzzlePanel;
	public ControlPanel controlPanel;

	public int width;
	public int height;
	public int pieceWidth;
	public int pieceHeight;
	
	public PuzzleController(PuzzlePanel panel, ControlPanel controlPanel) {
		this.controlPanel = controlPanel;
		controlPanel.addComponentListener(componentListener);
		this.puzzlePanel = panel;
		puzzleGen = new PuzzleGenerator();
		
		this.puzzlePanel.addMouseListener(mouseListener);
		this.puzzlePanel.addMouseMotionListener(mouseMotionListener);
	}
	
	/**
	 * Returns if the puzzle map is populated
	 * @return
	 */
	public boolean hasPuzzle() {
		return (puzzleMap != null);
	}
	
	/**
	 * Returns the desired panel dimension
	 * @return
	 */
	public Dimension getDesiredPanelDim() {
		return new Dimension(width + (2 * pieceWidth), height + (2 * pieceHeight));
	}
	
	/**
	 * Generate a puzzle from the given image
	 * with the given number of pieces
	 * @param img
	 * @param numPieces
	 */
	public void generatePuzzle(Image img, int numPieces) {
		// Set up puzzlePanel and generator
		puzzlePanel.setZoom(1f);
		puzzleGen.setImage(img);
		
		// Generate
		puzzleGen.genPuzzle(numPieces, 1, 1);
		
		// Retrieve variables from generator
		puzzleMap = puzzleGen.getMap();
		width = puzzleGen.getWidth();
		height = puzzleGen.getHeight();
		pieceWidth = puzzleGen.getPieceWidth();
		pieceHeight = puzzleGen.getPieceHeight();
		
		// Set up panel with new puzzle
		puzzlePanel.targetDrawDim = getDesiredPanelDim();
		puzzlePanel.setPreferredSize(puzzlePanel.targetDrawDim);
		while (puzzlePanel.targetDrawDim.width < Main.puzzleScrollPane.getWidth() || puzzlePanel.targetDrawDim.height < Main.puzzleScrollPane.getHeight()) {
			if(!puzzlePanel.zoomIn())
				break;
		}
		while (puzzlePanel.targetDrawDim.width > Main.puzzleScrollPane.getWidth() || puzzlePanel.targetDrawDim.height > Main.puzzleScrollPane.getHeight()) {
			if(!puzzlePanel.zoomOut())
				break;
		}
		Main.puzzleScrollPane.setPreferredSize(defaultScrollPaneSize);
		
		// Flush generator and update panel
		puzzleGen.flushGenerator();
		puzzlePanel.update();
	}
	
	/**
	 * Generate puzzle with given amount of pieces
	 * @param numPieces
	 */
	public void generatePuzzle(int numPieces) {
		// Set up panel
		puzzlePanel.setZoom(1f);
		
		// Generate puzzle
		puzzleGen.genPuzzle(numPieces, 1, 1);
		
		// Retrieve variables from generator
		puzzleMap = puzzleGen.getMap();
		width = puzzleGen.getWidth();
		height = puzzleGen.getHeight();
		pieceWidth = puzzleGen.getPieceWidth();
		pieceHeight = puzzleGen.getPieceHeight();
		
		// Set up panel with new puzzle
		puzzlePanel.targetDrawDim = getDesiredPanelDim();
		puzzlePanel.setPreferredSize(puzzlePanel.targetDrawDim);
		while (puzzlePanel.targetDrawDim.width < Main.puzzleScrollPane.getWidth() || puzzlePanel.targetDrawDim.height < Main.puzzleScrollPane.getHeight()) {
			if(!puzzlePanel.zoomIn())
				break;
		}
		while (puzzlePanel.targetDrawDim.width > Main.puzzleScrollPane.getWidth() || puzzlePanel.targetDrawDim.height > Main.puzzleScrollPane.getHeight()) {
			if(!puzzlePanel.zoomOut())
				break;
		}
		Main.puzzleScrollPane.setPreferredSize(defaultScrollPaneSize);
		
		// Flush generator and update panel
		puzzleGen.flushGenerator();
		puzzlePanel.update();
		
		new Thread(Main.animation).start();
	}
	
	/**
	 * Paint all puzzlePieces
	 * @param g
	 */
	public void paint(Graphics g) {
		for(int y = puzzleMap[0].length - 1; y >= 0; y--) {
			for(int x = puzzleMap.length - 1; x >= 0; x--) {
				puzzleMap[x][y].paint(g);
			}
		}
	}
	
//// Panel Listener ////
	ComponentListener componentListener = new ComponentListener() {
		@Override
		public void componentHidden(ComponentEvent e) { }
		@Override
		public void componentMoved(ComponentEvent e) { }
		@Override
		public void componentResized(ComponentEvent e) {
			if(!hasPuzzle()) return;	// Do nothing if no puzzle is loaded
			
			for(PuzzlePiece[] pArray : puzzleMap)
				for(PuzzlePiece p : pArray)
					// If piece is parent or not a child
					if(p.isParent() || p.parent == null)
						// Clip into panel by setting position to itself
						p.setScaledPosition(p.getScaledPosition());
		}
		@Override
		public void componentShown(ComponentEvent e) { }
	};
	
////Mouse Controls ////
	int draggingIndex = -1;
	Point mouseOffset;
	
	MouseListener mouseListener = new MouseListener() {
		@Override
		public void mouseClicked(MouseEvent arg0) { }
		@Override
		public void mouseEntered(MouseEvent arg0) { }
		@Override
		public void mouseExited(MouseEvent arg0) { }
		@Override
		public void mousePressed(MouseEvent arg0) {
			//System.out.println("Pressed at " +  arg0.getPoint());
			if(!hasPuzzle()) return;	// Do nothing if no puzzle is loaded
			
			for(int y = 0; y < puzzleMap[0].length; y++)
				for(int x = 0; x < puzzleMap.length; x++)
					// Check if mouse clicked on piece
					if(puzzleMap[x][y].getScaledRectangle().contains(arg0.getPoint())) {
						// Drag piece
						draggingIndex = x + y * puzzleMap.length;
						Point mPos = arg0.getPoint();
						Point pPos;
						if(puzzleMap[x][y].parent == null)
							pPos = puzzleMap[x][y].getScaledPosition();
						else
							pPos = puzzleMap[x][y].parent.getScaledPosition();
						mouseOffset = new Point(mPos.x - pPos.x, mPos.y - pPos.y);
					//	System.out.println("Piece at " + x + ", " + y + " selected");
						return;
					}
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
			if(draggingIndex == -1) return;	// Do nothing if no piece is selected
			
			// Get x and y of piece being dragged
			int xIndex = draggingIndex % puzzleMap.length;
			int yIndex = draggingIndex / puzzleMap.length;
			
			// Get matching pieces
			PuzzlePiece[] pieces = new PuzzlePiece[4];
			if(xIndex > 0)
				pieces[PuzzlePiece.CONNECTOR_LEFT] = puzzleMap[xIndex - 1][yIndex];
			if(xIndex < puzzleMap.length - 1)
				pieces[PuzzlePiece.CONNECTOR_RIGHT] = puzzleMap[xIndex + 1][yIndex];
			if(yIndex > 0)
				pieces[PuzzlePiece.CONNECTOR_TOP] = puzzleMap[xIndex][yIndex - 1];
			if(yIndex < puzzleMap[0].length - 1)
				pieces[PuzzlePiece.CONNECTOR_BOTTOM] = puzzleMap[xIndex][yIndex + 1];
			
			for(int i = 0; i < 4; i++)
				// If the piece exists
				if(pieces[i] != null) {
					// Continue if the pieces is already parented together
					if(pieces[i].locked && ((pieces[i].isParent() && pieces[i] == puzzleMap[xIndex][yIndex].parent) || (puzzleMap[xIndex][yIndex].isParent() && pieces[i].parent == puzzleMap[xIndex][yIndex]) || (!pieces[i].isParent() && pieces[i].parent == puzzleMap[xIndex][yIndex].parent))) continue;
					
					// Check all 4 directoins
					for(int c = 0; c < 4; c++)
						// If connectors intersect
						if(puzzleMap[xIndex][yIndex].getScaledConnector(c).intersects(pieces[i].getScaledConnector(PuzzlePiece.getInverseConnector(i)))) {
							// Connect!
							
							// Set piece to locked position
							switch(PuzzlePiece.getInverseConnector(c)) {
							case PuzzlePiece.CONNECTOR_RIGHT:
								if(puzzleMap[xIndex][yIndex].parent == null) {
									puzzleMap[xIndex][yIndex].setScaledPosition(
											new Point(pieces[i].getScaledPosition().x + puzzleMap[xIndex][yIndex].getScaledRectangle().width,
													pieces[i].getScaledPosition().y)
									);
								} else {
									Point dPos = puzzleMap[xIndex][yIndex].getScaledPosition();
									Point parentPos = puzzleMap[xIndex][yIndex].parent.getScaledPosition();
									dPos = new Point(parentPos.x - dPos.x, parentPos.y - dPos.y);
									Point targetPoint = new Point(pieces[i].getScaledPosition().x + puzzleMap[xIndex][yIndex].getScaledRectangle().width,
											pieces[i].getScaledPosition().y);
									puzzleMap[xIndex][yIndex].parent.setScaledPosition(new Point(targetPoint.x + dPos.x, targetPoint.y + dPos.y));
								}
								break;
							case PuzzlePiece.CONNECTOR_BOTTOM:
								if(puzzleMap[xIndex][yIndex].parent == null) {
									puzzleMap[xIndex][yIndex].setScaledPosition(
											new Point(pieces[i].getScaledPosition().x,
													pieces[i].getScaledPosition().y + puzzleMap[xIndex][yIndex].getScaledRectangle().height)
									);
								} else {
									Point dPos = puzzleMap[xIndex][yIndex].getScaledPosition();
									Point parentPos = puzzleMap[xIndex][yIndex].parent.getScaledPosition();
									dPos = new Point(parentPos.x - dPos.x, parentPos.y - dPos.y);
									Point targetPoint = new Point(pieces[i].getScaledPosition().x,
											pieces[i].getScaledPosition().y + puzzleMap[xIndex][yIndex].getScaledRectangle().height);
									puzzleMap[xIndex][yIndex].parent.setScaledPosition(new Point(targetPoint.x + dPos.x, targetPoint.y + dPos.y));
								}
								break;
							case PuzzlePiece.CONNECTOR_LEFT:
								if(puzzleMap[xIndex][yIndex].parent == null) {
									puzzleMap[xIndex][yIndex].setScaledPosition(
											new Point(pieces[i].getScaledPosition().x - puzzleMap[xIndex][yIndex].getScaledRectangle().width,
													pieces[i].getScaledPosition().y)
									);
								} else {
									Point dPos = puzzleMap[xIndex][yIndex].getScaledPosition();
									Point parentPos = puzzleMap[xIndex][yIndex].parent.getScaledPosition();
									dPos = new Point(parentPos.x - dPos.x, parentPos.y - dPos.y);
									Point targetPoint = new Point(pieces[i].getScaledPosition().x - puzzleMap[xIndex][yIndex].getScaledRectangle().width,
											pieces[i].getScaledPosition().y);
									puzzleMap[xIndex][yIndex].parent.setScaledPosition(new Point(targetPoint.x + dPos.x, targetPoint.y + dPos.y));
								}
								break;
							case PuzzlePiece.CONNECTOR_TOP:
								if(puzzleMap[xIndex][yIndex].parent == null) {
									puzzleMap[xIndex][yIndex].setScaledPosition(
											new Point(pieces[i].getScaledPosition().x,
													pieces[i].getScaledPosition().y - puzzleMap[xIndex][yIndex].getScaledRectangle().height)
									);
								} else {
									Point dPos = puzzleMap[xIndex][yIndex].getScaledPosition();
									Point parentPos = puzzleMap[xIndex][yIndex].parent.getScaledPosition();
									dPos = new Point(parentPos.x - dPos.x, parentPos.y - dPos.y);
									Point targetPoint = new Point(pieces[i].getScaledPosition().x,
											pieces[i].getScaledPosition().y - puzzleMap[xIndex][yIndex].getScaledRectangle().height);
									puzzleMap[xIndex][yIndex].parent.setScaledPosition(new Point(targetPoint.x + dPos.x, targetPoint.y + dPos.y));
								}
								break;
							}
							
							// Parent pieces together
							if(!puzzleMap[xIndex][yIndex].isParent() && puzzleMap[xIndex][yIndex].parent != null)
								pieces[i].getParent().addChild(puzzleMap[xIndex][yIndex].parent);
							else
								pieces[i].getParent().addChild(puzzleMap[xIndex][yIndex]);
						//	puzzleMap[xIndex][yIndex].setParent(pieces[i].getParent());
							
							System.out.println("Connected!");
							puzzlePanel.update();
						}
				}
			
			// Release piece
			draggingIndex = -1;
			//System.out.println("Released!");
		}
	};
	
	MouseMotionListener mouseMotionListener = new MouseMotionListener() {
		@Override
		public void mouseDragged(MouseEvent arg0) {
			// If a piece is being dragged move it
			if(draggingIndex != -1) {
				Point mPos = arg0.getPoint();
				Point newPos = new Point(mPos.x - mouseOffset.x, mPos.y - mouseOffset.y);
				//System.out.println("Moving to " + newPos);
				if(puzzleMap[draggingIndex % puzzleMap.length][draggingIndex / puzzleMap.length].parent != null)
					puzzleMap[draggingIndex % puzzleMap.length][draggingIndex / puzzleMap.length].parent.setScaledPosition(newPos);
				else
					puzzleMap[draggingIndex % puzzleMap.length][draggingIndex / puzzleMap.length].setScaledPosition(newPos);
				puzzlePanel.update();
			}
		}
		@Override
		public void mouseMoved(MouseEvent arg0) { }
	};
}
