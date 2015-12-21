package main;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handles Animation, can load
 * GIF, PNG, JPG
 * @author Nico Nocher
 *
 */

public class Animation implements Runnable {
	
	public int frameTime = 100;
	private long lastUpdate = 0;
	
	private URL fileURL;
	private ArrayList<Frame> frames;
	public boolean running = false;
	
	private int currentFrame = 0;
	
	public int width;
	public int height;
	
	/**
	 * Loads animation at the given path
	 * @param path
	 */
	public Animation(String path) {
		try {
			loadImage(new URL(path));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Loads animation at the given URL
	 * @param url
	 */
	public Animation(URL url) {
		loadImage(url);
	}
	
	/**
	 * Returns the URL of the currently loaded file
	 * @return
	 */
	public URL getFileURL() {
		return fileURL;
	}
	
	/**
	 * Loads file at the given URL
	 * Can be GIF, PNG, or JPG
	 * @param url
	 */
	public void loadImage(URL url) {
		this.fileURL = url;
		try {
			if(url.getPath().endsWith(".gif"))	// load gif if file ends in .gif
				frames = loadGIF(url);
			else	// Load other image format
				frames = loadIMG(url);
			if(frames == null)
				throw new IOException("No images loaded!");
			else
				System.out.println(frames.size() + " images loaded");
			width = frames.get(0).frame.getWidth();
			height = frames.get(0).frame.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the current BufferedImage of the Animation
	 * @return
	 */
	public BufferedImage getCurrentFrame() {
		return frames.get(currentFrame).frame;
	}
	
	@Override
	public void run() {
		/**
		 * Handles animation
		 */
		
		// Set up for animation loop
		lastUpdate = System.currentTimeMillis();
		running = true;
		
		while(running) {
			if(frames.get(0).delay == -1) {
				// If no GIF was loaded, set the currentFrame to 0 and stop animation
				// 		to improve performance
				currentFrame = 0;
				running = false;
				return;
			}
			
			if(lastUpdate + frameTime < System.currentTimeMillis()) {
				// Update frame
				currentFrame++;
				if(currentFrame == frames.size())
					currentFrame = 0;
				lastUpdate = System.currentTimeMillis();
				frameTime = frames.get(currentFrame).delay * 10;
				
				Main.puzzleController.puzzlePanel.update(); // Update the panel
			}
		}
	}
	
	/**
	 * Loads Image at given URL
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Frame> loadIMG(URL url) throws IOException {
		ArrayList<Frame> frames = new ArrayList<Frame>();
		
		BufferedImage img = ImageIO.read(url);
		frames.add(new Frame(img, -1));
		
		return frames;
	}

	/**
	 * Loads GIF at given URL
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Frame> loadGIF(URL url) throws IOException {
		ArrayList<Frame> frames = new ArrayList<Frame>();
		
		// Read GIF data
		try {
	        String[] imageatt = new String[]{
	                "imageLeftPosition",
	                "imageTopPosition",
	                "imageWidth",
	                "imageHeight"
	            };

	        ImageReader reader = (ImageReader)ImageIO.getImageReadersByFormatName("gif").next();
	        InputStream is = url.openStream();
	        ImageInputStream ciis = ImageIO.createImageInputStream(is);
	        reader.setInput(ciis);

	        int noi = reader.getNumImages(true);

	        BufferedImage master = null;

	        for (int i = 0; i < noi; i++) {
	        	System.out.println("Loading " + i + " out of " + noi);

	        	int delayTime = 1;
	            BufferedImage image = reader.read(i);
	            IIOMetadata metadata = reader.getImageMetadata(i);

	            Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
	            
	            try {
	            	//get delay
	            	IIOMetadataNode gce = (IIOMetadataNode) ((IIOMetadataNode) tree).getElementsByTagName("GraphicControlExtension").item(0);
	            	String str = gce.getAttribute("delayTime");
	            	delayTime = Integer.valueOf(str);
	            } catch(Exception e) { }
	            
	            NodeList children = tree.getChildNodes();

	            for (int j = 0; j < children.getLength(); j++) {
	                Node nodeItem = children.item(j);

	                if(nodeItem.getNodeName().equals("ImageDescriptor")){
	                    Map<String, Integer> imageAttr = new HashMap<String, Integer>();

	                    for (int k = 0; k < imageatt.length; k++) {
	                        NamedNodeMap attr = nodeItem.getAttributes();
	                        Node attnode = attr.getNamedItem(imageatt[k]);
	                        imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
	                    }

	                    if(i==0){
	                        master = new BufferedImage(imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
	                    }
	                    master.getGraphics().drawImage(image, imageAttr.get("imageLeftPosition"), imageAttr.get("imageTopPosition"), null);
	                }
	            }

	            frames.add(new Frame(Animation.deepCopy(master), delayTime));
	         //   ImageIO.write(master, "GIF", new File( i + ".gif")); 
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } 
		return frames;
	}
	
	/**
	 * Returns a copy of the BufferedImage
	 * @param bi
	 * @return
	 */
	private static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	/**
	 * Contains Frame Image and the delay time
	 *
	 */
	public static class Frame {
		BufferedImage frame;
		int delay;
		
		public Frame(BufferedImage frame, int delay) {
			this.frame = frame;
			this.delay = delay;
		}
	}
}
