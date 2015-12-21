package settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Nico Nocher
 *
 */

public class SettingsWriter {

	/**
	 * Write given container to file saved in container
	 * @param container
	 * @throws IOException
	 */
	public static void WriteSettings(SettingsContainer container) throws IOException {
		WriteSettings(container.getItems(), container.file);
	}
	
	/**
	 * Write given items to given file
	 * @param items
	 * @param file
	 * @throws IOException
	 */
	public static void WriteSettings(Item[] items, String file) throws IOException {
		// Create directory and file
		file = file.replace("\\", "/");
		File f = new File(file);
		new File(file.substring(0, file.lastIndexOf("/"))).mkdirs();
		f.createNewFile();
		
		// Initialize stream
		FileWriter fw = new FileWriter(f);
		BufferedWriter write = new BufferedWriter(fw);
		
		// Write items
		for(Item i : items) {
			write.write(i.name + "=" + i.value);
			write.newLine();
		}
		
		// Close stream
		write.close();
	}
}
