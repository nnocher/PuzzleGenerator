package settings;

import java.util.ArrayList;

/**
 * Sorts the given list
 * @author Nico Nocher
 *
 */

public class ListSorter {

	/**
	 * Sorts the given list
	 * @param list
	 * @return
	 */
	public static ArrayList<Item> sortItemList(ArrayList<Item> list) {
		// Item array from list
		Item[] items = list.toArray(new Item[0]);
		
		boolean sorted = false;
		int totalIterations = 0;
		
		// Loop until sorted
		while(!sorted) {
			// Check to make sure the loop doesn't run infinitely
			if(totalIterations > list.size()) {
				System.err.println("\tTaking too long to sort list... ");
				return null;
			}
			
			sorted = true;
			
			// Loop through list
			for(int i = 1; i < list.size(); i++) {
				// Compare current item and previous item
				Item current = items[i];
				Item previous = items[i - 1];
				
				boolean largerThan = largerThan(current.name, previous.name);
				
				// if the previous item is larger than the previous item, switch them
				if(!largerThan) {
					items[i - 1] = current;
					items[i] = previous;
					sorted = false;
				}
			}
			
			totalIterations++;
		}
		
		// Turn Item array into ArrayList and return
		ArrayList<Item> sortedList = new ArrayList<Item>();
		for(Item i : items) {
			sortedList.add(i);
		}
		return sortedList;
	}
	
	/**
	 * Compare two strings and return if the first is larger than the second
	 * @param first
	 * @param second
	 * @return
	 */
	public static boolean largerThan(String first, String second) {
		// Turn first and secon to char array
		char[] f = first.toLowerCase().toCharArray();
		char[] s = second.toLowerCase().toCharArray();
		
		// Get length of shorter array
		int shorterLength = Math.min(f.length,s.length);
		
		// Loop through arrays and compare characters until one is larger or smaller
		for(int i = 0; i < shorterLength; i++) {
			if((byte)f[i] != (byte)s[i]) {
				if((byte) f[i] < (byte) s[i]) 
					return false;
				else
					return true;
			}
		}
		
		// If both strings are identical up to shorterLength, the longer is larger
		if(f.length > s.length) 
			return true;
		return false;
	}
}
