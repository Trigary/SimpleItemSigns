package hu.trigary.simpleitemsigns;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

import static hu.trigary.simpleitemsigns.Main.isSign;
import static hu.trigary.simpleitemsigns.Main.sendMessage;

public class CommandListener implements CommandExecutor {
	CommandListener (Main main) {
		this.main = main;
	}
	
	private Main main;
	
	@Override
	public boolean onCommand (CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.isOp () == false && sender.hasPermission ("simpleitemsigns.admin") == false) {
			sendMessage (sender, "You have insufficient permissions!");
			return true;
		}
		
		
		
		if (args.length != 1 && args[0].equals ("create") == false) {
			return false;
		}
		
		switch (args[0]) {
			case "create":
				if (sender instanceof Player) {
					commandCreate ((Player)sender, args);
				} else {
					sendMessage (sender, "Only players can use this command!");
				}
				return true;
			case "delete":
				if (sender instanceof Player) {
					commandDelete ((Player)sender);
				} else {
					sendMessage (sender, "Only players can use this command!");
				}
				return true;
			case "reload":
				commandReload (sender);
				return true;
			default:
				return false;
		}
	}
	
	private void commandCreate (Player player, String[] args) {
		Block block = player.getTargetBlock ((Set<Material>) null, 16);
		if (block == null || isSign (block.getType ()) == false) {
			sendMessage (player, "You aren't looking at a sign or you are too far away!");
		} else if (player.getInventory ().getItemInMainHand ().getType () == Material.AIR) { //Below 1.8 (inclusive) use: getItemInHand ()
			sendMessage (player, "You don't have anything in your hand!");
		} else {
			Location location = block.getLocation ();
			for (ItemSign sign : main.storedSigns) {
				if (sign.matches (location)) {
					main.storedSigns.remove (sign);
					break;
				}
			}
			
			String title = "";
			for (int i = 1; i < args.length; i++) {
				title += args[i] + " ";
			}
			title = title.substring (0, title.length () - 1);
			
			main.storedSigns.add (new ItemSign (block.getLocation (), player.getInventory ().getItemInMainHand (), ChatColor.translateAlternateColorCodes ('&', title)));  //Below 1.8 (inclusive) use: getItemInHand ()
			main.saveStoredSigns ();
			sendMessage (player, "The ItemSign was successfully created!");
		}
	}
	
	private void commandDelete (Player player) {
		Block block = player.getTargetBlock ((Set<Material>) null, 16);
		if (block == null || isSign (block.getType ()) == false) {
			sendMessage (player, "You aren't looking at a sign or you are too far away!");
		} else {
			Location location = block.getLocation ();
			for (ItemSign sign : main.storedSigns) {
				if (sign.matches (location)) {
					main.storedSigns.remove (sign);
					main.saveStoredSigns ();
					sendMessage (player, "The sign was successfully removed!");
					return;
				}
			}
			sendMessage (player, "That sign is not an ItemSign!");
		}
	}
	
	private void commandReload (CommandSender sender) {
		int count = main.storedSigns.size ();
		main.storedSigns.removeIf (element -> element.isSign () == false);
		main.saveStoredSigns ();
		sendMessage (sender, Integer.toString (count - main.storedSigns.size ()) + " obsolete signs were found and removed.");
	}
}