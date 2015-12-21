package settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Used to read setting file
 * @author Nico Nocher
 *
 */

public class SettingsReader {

	private ArrayList<Item> settingList;
	
	/**
	 * Initialize and reads the given settings file
	 * @param file
	 * @throws IOException
	 */
	public SettingsReader(String file) throws IOException {
		System.out.println("Loading Settings file at \"" + file + "\"");
		if(!new File(file).exists()) throw new IOException("File at path \"" + file + "\" not found");
		System.out.println("\tSettings File found");
		readSettings(file);
		System.out.println("\t" + settingList.size() + " items found!");
		System.out.println("\tSorting settings...");
		settingList = ListSorter.sortItemList(settingList);
	}
	
	/**
	 * Returns the read list, returns null if no list was read
	 * @return
	 */
	public ArrayList<Item> getList() {
		return settingList;
	}
	
	/**
	 * Load settings from given file and returns list
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Item> loadSettings(String file) throws IOException {
		SettingsReader read = new SettingsReader(file);
		return read.getList();
	}
	
	/**
	 * Read settings from given file
	 * @param file
	 * @throws IOException
	 */
	private  void readSettings(String file) throws IOException {
		// Initialize stream
		FileReader fr = new FileReader(file);
		BufferedReader read = new BufferedReader(fr);
		
		System.out.println("\tStarting to read file...");
		
		// Initialize list
		settingList = new ArrayList<Item>();
		
		String line;
		int index;
		// Add items to list
		while((line = read.readLine()) != null) {
			index = line.indexOf("=");
			String name = line.substring(0, index);
			String value = line.substring(index + 1);
			settingList.add(new Item(name, value));
		}
		
		// Close stream
		read.close();
	}
}
