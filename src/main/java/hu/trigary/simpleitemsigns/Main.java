package hu.trigary.simpleitemsigns;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Main extends JavaPlugin {
	Set<ItemSign> storedSigns;
	int inventorySize;
	boolean usePermissions;
	boolean needUsePermission;
	
	@Override
	public void onEnable () {
		loadConfig ();
		loadStoredSigns ();
		getCommand ("simpleitemsigns").setExecutor (new CommandListener (this));
		getServer ().getPluginManager ().registerEvents (new EventListener (this), this);
	}
	
	
	static void sendMessage (CommandSender recipient, String input) {
		recipient.sendMessage (ChatColor.GOLD + "[SimpleItemSigns] " + ChatColor.GRAY + input);
	}
	
	static boolean isSign (Material material) {
		return (material == Material.SIGN || material == Material.SIGN_POST || material == Material.WALL_SIGN);
	}
	
	
	private void loadConfig () {
		saveDefaultConfig ();
		inventorySize = getConfig ().getInt ("inventorySize");
		usePermissions = getConfig ().getBoolean ("usePermissions");
		needUsePermission = getConfig ().getBoolean ("needUsePermission");
	}
	
	private void loadStoredSigns () {
		storedSigns = new HashSet<> ();
		
		if (new File (getFileName ()).exists ()) {
			try {
				ObjectInputStream in = new ObjectInputStream (new FileInputStream (getFileName ()));
				Set<ItemSignStorable> signs = (Set<ItemSignStorable>)in.readObject ();
				for (ItemSignStorable sign : signs) {
					storedSigns.add (sign.getItemSign ());
				}
				in.close ();
			} catch (IOException | ClassNotFoundException exception) {
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
			exception.printStackTrace ();
		}
	}
	
	private String getFileName () {
		return getDataFolder () + "/storage.data";
	}
}