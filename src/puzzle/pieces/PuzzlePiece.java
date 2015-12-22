package puzzle.pieces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Main;

/**
 * One PuzzlePiece
 * @author Nico Nocher
 *
 */

public class PuzzlePiece {
	public static final byte CONNECTOR_RIGHT = 0;
	public static final byte CONNECTOR_BOTTOM = 1;
	public static final byte CONNECTOR_LEFT = 2;
	public static final byte CONNECTOR_TOP = 3;
	
	public static boolean debugDraw = false;
	public static byte connectorWidth = 10;
	
	Image texture;
	
	PieceOverlayInfo overlayInfo;
	
	Rectangle rect;
	
	public boolean locked = false;
	private ArrayList<PuzzlePiece> pieceLock;
	public PuzzlePiece parent;
	
	/**
	 * Set piece with set texture and given rectangle
	 * @deprecated
	 * @param texture
	 * @param rect
	 */
	public PuzzlePiece(Image texture, Rectangle rect) {
		this.texture = texture;
		this.rect = rect;
	}
	
	private Rectangle subimage;
	/**
	 * Set piece with given subimage rectangle and given rectangle
	 * @param subimage
	 * @param rect
	 */
	public PuzzlePiece(Rectangle subimage, Rectangle rect) {
		this.subimage = subimage;
		this.rect = rect;
	}
	
	/*
	public void applyOverlay() {
		BufferedImage img = new BufferedImage(texture.getWidth(null), texture.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = img.createGraphics();
		
		BufferedImage overlay = (BufferedImage) overlayTexture;
		
		// draw piece
		g2D.drawImage(texture, 0, 0, null);
		
		for(int y = 0; y < overlayTexture.getHeight(null); y++) {
			for(int x = 0; x < overlayTexture.getWidth(null); x++) {
				if(new Color(overlay.getRGB(x, y)).getRed() == 255) {
					System.out.println("Found red at " + x + "," + y);
				}
			}
		}
	}*/
	
	/**
	 * Returns if the piece is a parent
	 * @return
	 */
	public boolean isParent() {
		return (locked && (parent == null || parent == this));
	}
	
	/**
	 * Returns if the piece has children
	 * @return
	 */
	public boolean hasChildren() {
		return (pieceLock != null && pieceLock.size() > 0);
	}
	
	/**
	 * Returns the rectangle for the piece
	 * @return
	 */
	public Rectangle getRectangle() {
		return rect;
	}
	
	/**
	 * Returns scaled instance of the rectangle
	 * @return
	 */
	public Rectangle getScaledRectangle() {
		float zoom = Main.puzzleController.puzzlePanel.zoom;
		return new Rectangle(
				(int) (rect.x * zoom + 0.5f), (int) (rect.y * zoom + 0.5f),
				(int) (rect.width * zoom + 0.5f), (int) (rect.height * zoom + 0.5f)
			);
	}
	
	/**
	 * Returns position
	 * @return
	 */
	public Point getPosition() {
		return new Point(rect.x, rect.y);
	}
	
	/**
	 * Returns scaled position
	 * @return
	 */
	public Point getScaledPosition() {
		Rectangle r = getScaledRectangle();
		return new Point(r.x, r.y);
	}
	
	/**
	 * Returns parent rectangle
	 * @return
	 */
	public Rectangle getParentRectangle() {
		// If its not a parent and doesn't have parent, return own rectangle
		if(!isParent() && parent == null) return getRectangle();
		// If is not parent return parent rectangle
		if(!isParent()) return parent.getParentRectangle();
		
		// Get min and max values for rectangle
		int minX = rect.x;
		int maxX = rect.x + rect.width;
		int minY = rect.y;
		int maxY = rect.y + rect.height;
		
		// Check children for lower mins and higher maxs
		for(PuzzlePiece p : pieceLock) {
			Rectangle r = p.getRectangle();
			if(minX > r.x) minX = r.x;
			if(minY > r.y) minY = r.y;
			if(maxX < r.x + r.width) maxX = r.x + r.width;
			if(maxY < r.y + r.height) maxY = r.y + r.height; 
		}
		
		// Return min/max rectangle
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
	
	/**
	 * Returns scaled instance of the parent rectangle
	 * @return
	 */
	public Rectangle getScaledParentRectangle() {
		// If its not a parent and doesn't have parent, return own scaled rectangle
		if(!isParent() && parent == null) return getScaledRectangle();
		// If is not parent return parent rectangle
		if(!isParent()) return parent.getScaledParentRectangle();

		// Get scaled rectangle
		Rectangle rect = getScaledRectangle();
		
		// Get min and max values for scaled rectangle
		int minX = rect.x;
		int maxX = rect.x + rect.width;
		int minY = rect.y;
		int maxY = rect.y + rect.height;
		
		// Check children for lower mins and higher maxs
		for(PuzzlePiece p : pieceLock) {
			Rectangle r = p.getScaledRectangle();
			if(minX > r.x) minX = r.x;
			if(minY > r.y) minY = r.y;
			if(maxX < r.x + r.width) maxX = r.x + r.width;
			if(maxY < r.y + r.height) maxY = r.y + r.height; 
		}
		
		// Returns scaled min/max rectangle
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	//	float zoom = Main.puzzleController.puzzlePanel.zoom;
	//	Rectangle parentCollision = getParentRectangle();
	//	return new Rectangle((int) (parentCollision.x * zoom + 0.5f), (int) (parentCollision.y * zoom + 0.5f),
	//			(int) (parentCollision.width * zoom + 0.5f), (int) (parentCollision.height * zoom + 0.5f));
	}
	
	/**
	 * Returns parent of piece, returns itself if it has no parent
	 * @return
	 */
	public PuzzlePiece getParent() {
		locked = true;
		if(parent == null)
			return this;
		return parent;
	}
	
	/**
	 * Returns connector at given direction
	 * @param type
	 * @return
	 */
	public Rectangle getConnector(int type) {
		switch(type) {
		case CONNECTOR_LEFT:
			return new Rectangle(
					rect.x - (int) (connectorWidth / 2),
					(rect.y + (rect.height / 2) - (connectorWidth / 2)),
					connectorWidth,
					connectorWidth
			);
		case CONNECTOR_RIGHT:
			return new Rectangle(
					(rect.x + rect.width) - (int) (connectorWidth / 2),
					(rect.y + (rect.height / 2) - (connectorWidth / 2)),
					connectorWidth,
					connectorWidth
			);
		case CONNECTOR_TOP:
			return new Rectangle(
					(rect.x + (rect.width) / 2) - (int) (connectorWidth / 2),
					rect.y - (connectorWidth / 2),
					connectorWidth,
					connectorWidth
			);
		case CONNECTOR_BOTTOM:
			return new Rectangle(
					(rect.x + (rect.width) / 2) - (int) (connectorWidth / 2),
					(rect.y + rect.height) - (int) (connectorWidth / 2),
					connectorWidth,
					connectorWidth
			);
		}
		return null;
	}
	
	/**
	 * Returns a scaled connector
	 * @param type
	 * @return
	 */
	public Rectangle getScaledConnector(int type) {
		Rectangle rect = getScaledRectangle();
		int scaledConnector = (int) (connectorWidth * Main.puzzleController.puzzlePanel.zoom + 0.5f);
		switch(type) {
		case CONNECTOR_LEFT:
			return new Rectangle(
					rect.x - (int) (scaledConnector / 2),
					(rect.y + (rect.height / 2) - (scaledConnector / 2)),
					scaledConnector,
					scaledConnector
			);
		case CONNECTOR_RIGHT:
			return new Rectangle(
					(rect.x + rect.width) - (int) (scaledConnector / 2),
					(rect.y + (rect.height / 2) - (scaledConnector / 2)),
					scaledConnector,
					scaledConnector
			);
		case CONNECTOR_TOP:
			return new Rectangle(
					(rect.x + (rect.width) / 2) - (int) (scaledConnector / 2),
					rect.y - (scaledConnector / 2),
					scaledConnector,
					scaledConnector
			);
		case CONNECTOR_BOTTOM:
			return new Rectangle(
					(rect.x + (rect.width / 2)) - (int) (scaledConnector / 2),
					(rect.y + rect.height) - (int) (scaledConnector / 2),
					scaledConnector,
					scaledConnector
			);
		}
		return null;
	}
	
	/**
	 * Sets the position to the given point
	 * Moves parent with all children if exists
	 * @param p
	 */
	public void setPosition(Point p) {
		// Check if piece is locked
		if(locked) {
			if(parent == null) {
				//If parent move all children
				p = clipPointToPanel(p);
				
				for(PuzzlePiece piece : pieceLock) {
					Point dPos = getPosition();
					dPos = new Point(dPos.x - p.x, dPos.y - p.y);
					Point cPos = piece.getPosition();
					piece.setPositionOverride(new Point(cPos.x - dPos.x, cPos.y - dPos.y));
				}
			} else {
				// No clipping needed, parent handles clipping
				// Has parent, so set parent position
				Point pPos = parent.getPosition();
				Point dPos = new Point(p.x - rect.x, p.y - rect.y);
				parent.setPosition(new Point(pPos.x + dPos.x, pPos.y + dPos.y));
			}
		} else {
			// If not do simple movement
			// Clip to panel
			p = clipPointToPanel(p);
			
			rect.x = p.x;
			rect.y = p.y;
		}
	}
	
	/**
	 * Directly sets the position
	 * @param p
	 */
	public void setPositionOverride(Point p) {
		rect.x = p.x;
		rect.y = p.y;
	}
	
	/**
	 * Sets the scaled position of the piece
	 * @param p
	 */
	public void setScaledPosition(Point p) {
		// Check if piece is locked
		if(locked) {
			if(parent == null || parent == this) {
				//If parent move all children
				p = clipScaledPointToPanel(p);
				
				for(PuzzlePiece piece : pieceLock) {
					Point dPos = getScaledPosition();
					dPos = new Point(dPos.x - p.x, dPos.y - p.y);
					Point cPos = piece.getScaledPosition();
					piece.setScaledPositionOverride(new Point(cPos.x - dPos.x, cPos.y - dPos.y));
				}
				
				float zoom = Main.puzzleController.puzzlePanel.zoom;
				rect.x = (int) (p.x / zoom + 0.5f);
				rect.y = (int) (p.y / zoom + 0.5f);
			} else {
				// No clipping needed, parent handles clipping
				// Has parent, so set parent position
				
				Point pPos = parent.getPosition();
				Point dPos = new Point(p.x - rect.x, p.y - rect.y);
				parent.setScaledPosition(new Point(pPos.x + dPos.x, pPos.y + dPos.y));
			}
		} else {
			// If not do simple movement
			// Clip to panel
			p = clipScaledPointToPanel(p);
			
			float zoom = Main.puzzleController.puzzlePanel.zoom;
			rect.x = (int) (p.x / zoom + 0.5f);
			rect.y = (int) (p.y / zoom + 0.5f);
		}
	}
	
	/**
	 * Directly sets the scaled position
	 * @param p
	 */
	public void setScaledPositionOverride(Point p) {
		float zoom = Main.puzzleController.puzzlePanel.zoom;
		rect.x = (int) (p.x / zoom + 0.5f);
		rect.y = (int) (p.y / zoom + 0.5f);
	}
	
	/**
	 * Sets parent
	 * @param p
	 */
	public void setParent(PuzzlePiece p) {
		parent = p;
		locked = (p != null);
	}
	
	/**
	 * Adds given piece as child
	 * @param p
	 */
	public void addChild(PuzzlePiece p) {
		if(p == null) return;
		if(pieceLock == null)
			pieceLock = new ArrayList<PuzzlePiece>();
		p.setParent(this);
		pieceLock.add(p);
		
		if(p.hasChildren()) {
			ArrayList<PuzzlePiece> list = p.getChildren();
			if(list != null) {
				for(PuzzlePiece piece : list) {
					piece.setParent(this);
					pieceLock.add(piece);
				}
			}
		}
		locked = true;
	}
	
	/**
	 * Returns list of all children
	 * @return
	 */
	private ArrayList<PuzzlePiece> getChildren() {
		ArrayList<PuzzlePiece> newList = new ArrayList<PuzzlePiece>();
		for(PuzzlePiece p : pieceLock)
			newList.add(p);
		pieceLock.clear();
		pieceLock = null;
		return newList;
	}
	
	/**
	 * Clips the object into the panel at the given point
	 * and returns the clipped position
	 * @param p
	 * @return
	 */
	private Point clipPointToPanel(Point p) {
		// Get parent rectangle
		Rectangle parentRect = getParentRectangle();
		
		// Determine offset from parent
		Point dP = new Point(getPosition().x - parentRect.x, getPosition().y - parentRect.y);
		
		// Get screen rectangle
		Dimension dim = Main.puzzleController.puzzlePanel.getSize();
		Rectangle screenRect = new Rectangle(0, 0, dim.width, dim.height);
		
		// Calculate new rectangle at new position
		Rectangle newRect = new Rectangle(dP.x + p.x, dP.y + p.y, parentRect.width, parentRect.height);
		
		// Clip newRect into screenRect
		if(!screenRect.contains(newRect)) {
			if(newRect.x < screenRect.x) newRect.x = screenRect.x;
			if(newRect.y < screenRect.y) newRect.y = screenRect.y;
			if(newRect.x + newRect.width > screenRect.x + screenRect.width) newRect.x = screenRect.x + screenRect.width - newRect.width;
			if(newRect.y + newRect.height > screenRect.y + screenRect.height) newRect.y = screenRect.y + screenRect.height - newRect.height;
			
			// Return newRect
			return new Point(newRect.x + dP.x, newRect.y + dP.y);
		}
		
		// newRect is on screen, return p
		return p;
	}
	
	/**
	 * Clips the scaled object into the panel at the given point
	 * and returns the clipped position
	 * @param p
	 * @return
	 */
	private Point clipScaledPointToPanel(Point p) {
		// Get Scaled parent rectangle
		Rectangle parentRect = getScaledParentRectangle();
		
		// Get scaled offset from parent
		// 		deltaP = pos - parentRect.pos
		Point dP = new Point(getScaledPosition().x - parentRect.x, getScaledPosition().y - parentRect.y);
		
		// Get screen rectangle
		Dimension dim = Main.puzzleController.puzzlePanel.getSize();
		Rectangle screenRect = new Rectangle(0, 0, dim.width, dim.height);
		
		// Calculate new rectangle at new position
		// 		deltaP = newP - newParentPos
		// 		deltaP + newParentPos = newP
		// 		newParentPos = newP - deltaP
		Rectangle newRect = new Rectangle(p.x - dP.x, p.y - dP.y, parentRect.width, parentRect.height);
		
		// Clip newRect into screenRect
		if(!screenRect.contains(newRect)) {
			if(newRect.x < screenRect.x) newRect.x = screenRect.x;
			if(newRect.y < screenRect.y) newRect.y = screenRect.y;
			if(newRect.x + newRect.width > screenRect.x + screenRect.width) newRect.x = screenRect.x + screenRect.width - newRect.width;
			if(newRect.y + newRect.height > screenRect.y + screenRect.height) newRect.y = screenRect.y + screenRect.height - newRect.height;
			
			// Return newRect
			// 		newParentPos = newP - deltaP
			// 		newParentPos + deltaP = newP
			return new Point(newRect.x + dP.x, newRect.y + dP.y);
		}
		
		// newRect is on screen, return p
		return p;
	}
	
	/**
	 * Draw PuzzlePiece
	 * @param g
	 */
	public void paint(Graphics g) {
		// Get scaled rectangle to draw image at
		Rectangle r = getScaledRectangle();
		
		// Get Image from animator
	//	g.drawImage(texture, r.x, r.y, r.width, r.height, null);
		BufferedImage img = Main.animation.getCurrentFrame();
		if(img == null) {
			// No image is set, return
			System.out.println("No Image");
			return;
		}
		
		// Draw subimage at given r
		g.drawImage(img.getSubimage(subimage.x, subimage.y, subimage.width, subimage.height), r.x, r.y, r.width, r.height, null);
		
		// Debug draw
		if(debugDraw) {
			// Parent rectangle
			g.setColor(new Color(155, 255, 155, 155));
			Rectangle col = getScaledParentRectangle();
			g.drawRect(col.x, col.y, col.width, col.height);
			
			// Connectors
			g.setColor(new Color(255, 155, 155, 155));
			for(int i = 0; i < 4; i++) {
				Rectangle rect = getScaledConnector(i);
				g.fillRect(rect.x, rect.y, rect.width, rect.height);
			}
			
			Rectangle re = getScaledRectangle();
			if(isParent() && hasChildren()) {
				// Parent with Child
				g.setColor(Color.black);
				g.drawString("PwC", re.x + 1, re.y + 1);
				g.setColor(Color.red);
				g.drawString("PwC", re.x, re.y);
			} else if(isParent()) {
				// Parent
				g.setColor(Color.black);
				g.drawString("P", re.x + 1, re.y + 1);
				g.setColor(Color.red);
				g.drawString("P", re.x, re.y);
			}
			if(locked && parent != null) {
				// Child
				g.setColor(Color.black);
				g.drawString("C", re.x + 1, re.y + 1 + g.getFont().getSize());
				g.setColor(Color.green);
				g.drawString("C", re.x, re.y + + g.getFont().getSize());
			}
		}
	}
	
	/**
	 * Returns the opposite connector from the given connector
	 * @param connector
	 * @return
	 */
	public static int getInverseConnector(int connector) {
		connector += 2;
		if(connector > 3)
			connector -= 4;
		return connector;
	}
}
