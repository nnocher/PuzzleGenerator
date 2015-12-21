package settings;

import java.io.IOException;
import java.util.Scanner;

/**
 * Editor to create and edit setting files
 * @author Nico Nocher
 *
 */

public class SettingsEditor {

	Scanner s;
	SettingsContainer container;
	
	public static void main(String[] args) {
		new SettingsEditor();
	}
	
	/**
	 *  Start editor
	 */
	public SettingsEditor() {
		s = new Scanner(System.in);
		mainMenu();
	}

	/**
	 * Main menu for editor
	 */
	public void mainMenu() {
		System.out.println("Settings Editor v1.0");
		int selection = 0;
		while(selection == 0) {
			// Print menu
			System.out.println("--- Main Menu --");
			System.out.println("1 - New File");
			System.out.println("2 - Load File");
			System.out.println("3 - Exit");
			System.out.print("Enter Selection: ");
			
			// Get input
			String input = s.nextLine();
			switch(input) {
			case "1":
				newFile();
				editor();
				break;
			case "2":
				loadFile();
				editor();
				break;
			case "3":
				System.exit(0);
				break;
			default:
				System.err.println("Invalid Input");
			}
		}
	}
	
	/**
	 * Creates new file
	 */
	public void newFile() {
		System.out.println("---- New File --");
		container = SettingsContainer.createEmpty();
		System.out.println("New Container Created");
		container.writeable = true;
	}
	
	/**
	 * Asks for and loads the file
	 */
	public void loadFile() {
		// Print
		System.out.println("--- Load File --");
		System.out.print("Enter file path: ");
		
		// Get input
		String path = s.nextLine();
		container = new SettingsContainer(path);
		container.writeable = true;
	}
	
	/**
	 * Save loaded file
	 */
	public void saveFile() {
		// Print
		System.out.println("--- Save File --");
		System.out.print("Enter file path: ");
		
		// Get input
		String path = s.nextLine();
		
		try {
			// Save
			SettingsWriter.WriteSettings(container.getItems(), path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Editor for loaded file
	 */
	public void editor() {
		// Print
		System.out.println("---- Editor ----");
		while(true) {
			System.out.println("Current Items:");
			container.printContainer();
			System.out.println("\n 1 - New Item");
			System.out.println(" 2 - Edit Item");
			System.out.println(" 3 - Delete Item");
			System.out.println(" 4 - Save List");
			System.out.println(" 5 - Exit");
			
			// Get input
			String input = s.nextLine();

			
			switch(input) {
			case "1":
				newItem();
				break;
			case "2":
				editItem();
				break;
			case "3":
				deleteItem();
				break;
			case "4":
				saveFile();
				break;
			case "5":
				return;
			default:
				System.err.println("Invalid Input");
			}
		}
	}
	
	/**
	 * Add new item to list
	 */
	public void newItem() {
		// Print
		System.out.println("---- New Item --");
		String name;
		String value;
		while(true) {
			// Name
			while(true) {
				System.out.print("Enter Name: ");
				name = s.nextLine();
				// Validade name
				if (name.length() > 0 && !name.contains("=") && container.getItem(name) == null)
					break;
				System.err.println("Invalid Name or Name already used!");
			}
		
			// Value
			System.out.print("Enter Value: ");
			value = s.nextLine();
			
			// Check
			System.out.println("Name=" + name + "\nValue=" + value);
			System.out.print("Is that correct? [1-Yes;0-No] ");
			String input = s.nextLine();
			
			if(input.equals("1"))
				break;
		}
		
		// Add value
		try {
			container.addValue(new Item(name, value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Edit item
	 */
	public void editItem() {
		// Print
		System.out.println("--- Edit Item --");
		Item item;
		
		// Name
		while(true) {
			System.out.print("Enter Name [Leave blank to cancel]: ");
			String name = s.nextLine();
			if(name.equals("")) return;
			item = container.getItem(name);
			if(item != null)
				break;
			System.err.println("Name not found!");
		}
		
		// Print info
		System.out.println("Item:");
		System.out.println("Name=" + item.name);
		System.out.println("Value=" + item.value);
		
		// Get and set new value
		System.out.print("Enter new value: ");
		String newValue = s.nextLine();
		container.setValue(item.name, newValue);
	}
	
	/**
	 * delete item
	 */
	public void deleteItem() {
		// Print
		System.out.println("-- Delete Item -");
		String name;
		
		// Name
		while(true) {
			System.out.print("Enter Name [Leave blank to cancel]: ");
			name = s.nextLine();
			if(name.equals("")) return;
			if(container.getItem(name) != null)
				break;
			System.err.println("Name not found!");
		}
		
		// Remove value
		try {
			container.removeValue(name);
		} catch (Exception e) {
			System.err.println("Item is write protected!");
		}
	}
}
