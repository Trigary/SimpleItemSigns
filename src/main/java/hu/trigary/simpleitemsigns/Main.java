package hu.trigary.simpleitemsigns;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Main extends JavaPlugin {
	Set<ItemSign> storedSigns;
	int inventorySize;
	boolean needUsePermission;
	boolean dontTrash;
	
	@Override
	public void onEnable () {
		loadConfig ();
		loadStoredSigns ();
		getCommand ("simpleitemsigns").setExecutor (new CommandListener (this));
		getServer ().getPluginManager ().registerEvents (new EventListener (this), this);
	}
	
	private void loadConfig () {
		saveDefaultConfig ();
		FileConfiguration config = getConfig ();
		FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration (getTextResource ("config.yml"));
		
		inventorySize = (int) getConfigEntry (config, defaultConfig, "inventorySize");
		needUsePermission = (boolean) getConfigEntry (config, defaultConfig, "needUsePermission");
		dontTrash = (boolean) getConfigEntry (config, defaultConfig, "dontTrash");
	}
	
	private void loadStoredSigns () {
		storedSigns = new HashSet<> ();
		
		if (new File (getFileName ()).exists ()) {
			try {
				ObjectInputStream in = new ObjectInputStream (new FileInputStream (getFileName ()));
				@SuppressWarnings("unchecked")
				Set<ItemSignStorable> signs = (Set<ItemSignStorable>)in.readObject ();
				for (ItemSignStorable sign : signs) {
					storedSigns.add (sign.getItemSign ());
				}
				in.close ();
			} catch (IOException | ClassNotFoundException exception) {
				getLogger ().severe ("An error occurred while reading the stored ItemSigns. If you believe this is a bug in the plugin, please contact the developer!");
				exception.printStackTrace ();
			}
		}
	}
	
	void saveStoredSigns () {
		try {
			ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream (getFileName ()));
			Set<ItemSignStorable> signs = new HashSet<> ();
			for (ItemSign sign : storedSigns) {
				signs.add (sign.getStorable ());
			}
			out.writeObject (signs);
			out.flush ();
			out.close ();
		} catch (IOException exception) {
			getLogger ().severe ("An error occurred while saving the ItemSigns. If you believe this is a bug in the plugin, please contact the developer!");
			exception.printStackTrace ();
		}
	}
	
	private String getFileName () {
		return getDataFolder () + File.separator + "storage.data";
	}
	
	private Object getConfigEntry (FileConfiguration activeConfig, FileConfiguration defaultConfig, String path) {
		if (activeConfig.isSet (path)) {
			return activeConfig.get (path);
		} else {
			getLogger ().severe ("Your configuration has missing fields! Delete the config.yml, restart the server and re-enter your settings or look for a fix in the spigot resource update post!");
			return defaultConfig.get (path);
		}
	}
	
	static void sendMessage (CommandSender recipient, String input) {
		recipient.sendMessage (ChatColor.GOLD + "[SimpleItemSigns] " + ChatColor.GRAY + input);
	}
	
	static boolean isSign (Material material) {
		return (material == Material.SIGN_POST || material == Material.WALL_SIGN);
	}
}