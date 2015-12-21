package settings;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Nico Nocher
 *
 */

public class SettingsContainer {

	private ArrayList<Item> settingList;
	public boolean writeable = false;
	
	public String file;
	
	public SettingsContainer() { }
	public SettingsContainer(String file) {
		this.file = file;
		try {
			settingList = SettingsReader.loadSettings(file);
		} catch(IOException ex) {
			System.out.println("Unable to load settings. Error Message: " + ex.getMessage());
			settingList = new ArrayList<Item>();
		}
	}
	
	/**
	 * Add the given item to the container, places in correct place to sort
	 * @param newItem
	 * @throws Exception
	 */
	public void addValue(Item newItem) throws Exception {
		if(!writeable) throw new Exception("Container is not writeable");
		
		// Find location to add new item
		for(int i = 0; i < settingList.size(); i++) {
			if(ListSorter.largerThan(settingList.get(i).name, newItem.name)) {
				settingList.add(i, newItem);
				return;
			}
		}
		settingList.add(newItem);
	}
	
	/**
	 * Removes the given item from the list
	 * @param name
	 * @throws Exception
	 */
	public void removeValue(String name) throws Exception {
		if(!writeable) throw new Exception("Container is not writeable");
		
		Item i = getItem(name);
		settingList.remove(i);
	}
	
	/**
	 * Returns the value with the given name
	 * Else it will return ValueNotFound_name
	 * @param name
	 * @return
	 */
	public String getValue(String name) {
		Item i = getItem(name);
		if(i == null) return "ValueNotFound_" + name;
		return i.value;
	}
	
	/**
	 * Set the value of the given item to the given value
	 * @param name
	 * @param value
	 * @return
	 */
	public boolean setValue(String name, String value) {
		Item i = getItem(name);
		if(i == null) return false;
		i.value = value;
		return true;
	}
	
	/**
	 * Returns the item with the given name
	 * @param name
	 * @return
	 */
	public Item getItem(String name) {
		// Return null if no items exist
		if(settingList.size() == 0) return null;
		
		// Get min max and med
		int max = settingList.size();
		int min = 0;
		int med = 1;
		
		while(true) {
			/*
			 * Sort by checking items at min, med, and max using
			 * the following technique:
			 * 		- determine med
			 * 		- if item at med is the item return it
			 * 		- if item at min is the item return it
			 * 		- if med is larger than the item set new max
			 * 				to current med, then search again using
			 * 				the new min and max
			 * 		- if med is smaller than the item set the new min
			 * 				to current med, then search again using
			 * 				the new min and max
			 */
			
			med = min + (max - min) / 2;
			if(name.equals(settingList.get(med).name))
				return settingList.get(med);
			if(name.equals(settingList.get(min).name))
				return settingList.get(min);
			if(med == min) break;
			
			if(ListSorter.largerThan(name, settingList.get(med).name))
				min = med;
			else
				max = med;
		}
		
		return null;
	}
	
	/**
	 * Return Item array
	 * @return
	 */
	public Item[] getItems() {
		return settingList.toArray(new Item[0]);
	}
	
	/**
	 * Print all items to the console
	 */
	public void printContainer() {
		if(settingList.size() == 0) {
			System.out.println("\t-None-");
			return;
		}
		
		// Print index number right aligned
		int indexLength = (settingList.size() + "").length();
		for(int i = 0; i < settingList.size(); i++) {
			System.out.print(" ");
			for(int j = (i + "").length(); j < indexLength; j++) {
				System.out.print(" ");
			}
			System.out.print(i);
			System.out.println(" | " + settingList.get(i).name + "=" + settingList.get(i).value);
		}
	}
	
	/**
	 * Return a new container with empty settingList
	 * @return
	 */
	public static SettingsContainer createEmpty() {
		SettingsContainer sc = new SettingsContainer();
		sc.settingList = new ArrayList<Item>();
		return sc;
	}
}
