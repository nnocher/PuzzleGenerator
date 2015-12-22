package generator;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import main.Main;
import main.MathFunctions;
import puzzle.pieces.PuzzlePiece;

/**
 * Generates Puzzle
 * @author Nico Nocher
 *
 */

public class PuzzleGenerator {

	public BufferedImage img;
	
	private PuzzlePiece[][] puzzleMap;
	
	private int pieceWidth;
	private int pieceHeight;
	
	public PuzzleGenerator() { }
	public PuzzleGenerator(BufferedImage img) {
		setImage(img);
	}
	public PuzzleGenerator(Image img) {
		setImage(img);
	}
	
	/**
	 * Set the Image using BufferedImage
	 * @param img
	 */
	public void setImage(BufferedImage img) {
		this.img = img;
	}
	/**
	 * Set the Image using Image
	 * @param img
	 */
	public void setImage(Image img) {
		//this.img = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		//Graphics2D g = this.img.createGraphics();
		//g.drawImage(img, 0, 0, null);
		//g.dispose();
		
		this.img = (BufferedImage) img;
	}
	
	/**
	 * Returns a clone generated map
	 * @return
	 */
	public PuzzlePiece[][] getMap() {
		return puzzleMap.clone();
	}
	
	/**
	 * Returns the image width
	 * @return
	 */
	public int getWidth() {
		//return img.getWidth();
		return Main.animation.width;
	}
	
	/**
	 * Returns the image height
	 * @return
	 */
	public int getHeight() {
		//return img.getHeight();
		return Main.animation.height;
	}
	
	/**
	 * Returns the width of one puzzle piece
	 * @return
	 */
	public int getPieceWidth() {
		return pieceWidth;
	}
	
	/**
	 * Returns the height of one puzzle piece
	 * @return
	 */
	public int getPieceHeight() {
		return pieceHeight;
	}
	
	/**
	 * Generates a puzzle with no piece offset
	 * @param numberOfPieces
	 * @return
	 */
	public boolean genPuzzle(int numberOfPieces) {
		return genPuzzle(numberOfPieces, 0, 0);
	}
	/**
	 * Generates a puzzle and places the pieces with the given offset
	 * @param numberOfPieces
	 * @param pieceOffsetX
	 * @param pieceOffsetY
	 * @return
	 */
	public boolean genPuzzle(int numberOfPieces, int pieceOffsetX, int pieceOffsetY) {
		// Determine the number of pieces
		
		// cannot be a prime number
		if(MathFunctions.isPrime(numberOfPieces)) numberOfPieces--;
		if(numberOfPieces == 0) return false;
		
	//	// Find rounded number closest to square root and set the number of pieces on y to that
		int nopY = (int) (Math.sqrt(numberOfPieces) + 0.5);
		int nopX = numberOfPieces / nopY; // Determine number of pieces on x
		
		// Determine piece size by dividing the image size by the number of pieces
		pieceWidth = Main.animation.width / nopX; //img.getWidth() / nopX;
		pieceHeight = Main.animation.height / nopY; //img.getHeight() / nopY;
		
		// Initialize map
		puzzleMap = new PuzzlePiece[nopX][nopY];
		Random r = new Random();
		
		// Populate the map with pieces
		for(int y = 0; y < nopY; y++) {
			for(int x = 0; x < nopX; x++) {
				Point p = new Point(x * pieceWidth, y * pieceHeight);
				// Create puzzle piece and place it at a randomized position
			//	puzzleMap[x][y] = new PuzzlePiece(img.getSubimage(p.x, p.y, pieceWidth, pieceHeight), new Rectangle(r.nextInt(img.getWidth() + pieceOffsetX * pieceWidth), r.nextInt(img.getHeight() + pieceOffsetY * pieceHeight), pieceWidth, pieceHeight));
				puzzleMap[x][y] = new PuzzlePiece(new Rectangle(p.x, p.y, pieceWidth, pieceHeight), new Rectangle(r.nextInt(Main.animation.width + pieceOffsetX * pieceWidth), r.nextInt(Main.animation.height + pieceOffsetY * pieceHeight), pieceWidth, pieceHeight));
			}
		}
		
		System.out.println("Generated " + nopX + " by " + nopY + " PieceSize: " + pieceWidth + "," + pieceHeight);
		
		return true;
	}
	
	/**
	 * Releases all variables held by the generator
	 */
	public void flushGenerator() {
		if(img != null)
			img.flush();
		img = null;
		puzzleMap = null;
		pieceWidth = 0;
		pieceHeight = 0;
	}
	
	/**
	 * Used to draw the loaded image.
	 * Doesn't draw anything after flushGenerator is called,
	 * unless another image is loaded again
	 * @param g
	 */
	public void paintImage(Graphics2D g) {
		if(img == null) return;
		g.drawImage(img, 0, 0, null);
	}
}
