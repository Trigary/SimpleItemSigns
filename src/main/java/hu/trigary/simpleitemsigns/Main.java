package hu.trigary.simpleitemsigns;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main extends JavaPlugin {	
	@Override
	public void onEnable () {
		saveDefaultConfig ();
		needUsePermission = getConfig ().getBoolean ("needUsePermission");
		size = getConfig ().getInt ("rows") * 9;
		dontTrash = getConfig ().getBoolean ("dontTrash");
		
		Map<String, Object> data = customLoad ("data.data");
		itemSigns = new HashSet<> ();
		if (data != null) {
			loadItemSigns (data.get ("itemSigns"));
		}
		
		getCommand ("simpleitemsigns").setExecutor (new CommandListener (this));
		getServer ().getPluginManager ().registerEvents (new EventListener (this), this);
	}
	
	Set<ItemSign> itemSigns;
	boolean needUsePermission;
	int size;
	boolean dontTrash;
	
	
	
	void saveData () { //TODO rename to save
		Map<String, Object> data = new HashMap<> ();
		
		Set<Map<String, Object>> serializedItemSigns = new HashSet<> ();
		for (ItemSign itemSign : itemSigns) {
			serializedItemSigns.add (itemSign.serialize ());
		}
		data.put ("itemSigns", serializedItemSigns);
		
		customSave (data, "data.data");
	}
	
	
	
	private void loadItemSigns (Object mapValue) {
		@SuppressWarnings ("unchecked")
		Set<Map<String, Object>> serializedItemSigns = (Set<Map<String, Object>>)mapValue;
		for (Map<String, Object> serialized : serializedItemSigns) {
			itemSigns.add (ItemSign.deserialize (serialized));
		}
	}
	
	private Map<String, Object> customLoad (String path) {
		if (new File (getDataFolder () + File.separator + path).exists ()) {
			try {
				ObjectInputStream in = new ObjectInputStream (new FileInputStream (getDataFolder () + File.separator + path));
				@SuppressWarnings ("unchecked")
				Map<String, Object> data = (Map<String, Object>)in.readObject ();
				in.close ();
				return data;
			} catch (IOException | ClassNotFoundException exception) {
				exception.printStackTrace ();
			}
		}
		return null;
	}
	
	private void customSave (Map<String, Object> object, String path) {
		try {
			ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream (getDataFolder () + File.separator + path));
			out.writeObject (object);
			out.flush ();
			out.close ();
		} catch (IOException exception) {
			exception.printStackTrace ();
		}
	}
	
	
	
	static void sendMessage (CommandSender recipient, String message) {
		recipient.sendMessage (ChatColor.GOLD + "[SimpleItemSigns] " + ChatColor.WHITE + message);
	}
	
	static void sendError (CommandSender recipient, String error) {
		recipient.sendMessage (ChatColor.GOLD + "[SimpleItemSigns] " + ChatColor.RED + error);
	}
	
	static boolean isSign (Material material) {
		return (material == Material.SIGN_POST || material == Material.WALL_SIGN);
	}
}
