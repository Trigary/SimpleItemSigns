package hu.trigary.simpleitemsigns;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class CommandListener implements CommandExecutor {
	CommandListener (Main main) {
		this.main = main;
	}
	
	private Main main;
	
	@Override
	public boolean onCommand (CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission ("simpleitemsigns.admin")) {
			if (args.length != 0) {
				switch (args[0]) {
					case "create":
						if (args.length > 1) {
							if (playerCheck (sender)) {
								commandCreate ((Player)sender, args);
							}
							return true;
						} else {
							return false;
						}
					case "delete":
						if (args.length == 1) {
							if (playerCheck (sender)) {
								commandDelete ((Player)sender);
							}
							return true;
						} else {
							return false;
						}
					case "check":
						if (args.length == 1) {
							commandCheck (sender);
							return true;
						} else {
							return false;
						}
					case "color":
						if (args.length == 1) {
							if (playerCheck (sender)) {
								commandColor ((Player)sender);
							}
							return true;
						} else {
							return false;
						}
					case "edit":
						if (args.length > 2) {
							if (playerCheck (sender)) {
								commandEdit ((Player)sender, args);
							}
							return true;
						} else {
							return false;
						}
					default:
						return false;
				}
			} else {
				return false;
			}
		} else {
			Main.sendError (sender, "You have insufficient permissions!");
			return true;
		}
	}
	
	
	
	private void commandCreate (Player player, String[] args) {
		Block block = player.getTargetBlock ((Set<Material>) null, 16);
		if (block != null && Main.isSign (block.getType ())) {
			if (getItemInHand (player).getType () != Material.AIR) {
				Location location = block.getLocation ();
				main.itemSigns.removeIf (itemSign -> itemSign.location.equals (location));
				
				String title = "";
				for (int i = 1; i < args.length; i++) {
					title += args[i] + " ";
				}
				title = title.substring (0, title.length () - 1);
				
				main.itemSigns.add (new ItemSign (block.getLocation (), getItemInHand (player), ChatColor.translateAlternateColorCodes ('&', title)));
				main.saveData ();
				Main.sendMessage (player, "The ItemSign has been successfully created.");
			} else {
				Main.sendError (player, "You don't have anything in your hand!");
			}
		} else {
			Main.sendError (player, "You aren't looking at a sign or you are too far away!");
		}
	}
	
	private void commandDelete (Player player) {
		Block block = player.getTargetBlock ((Set<Material>) null, 16);
		if (block != null && Main.isSign (block.getType ())) {
			Location location = block.getLocation ();
			for (ItemSign sign : main.itemSigns) {
				if (sign.location.equals (location)) {
					main.itemSigns.remove (sign);
					main.saveData ();
					Main.sendMessage (player, "The sign has been successfully removed.");
					return;
				}
			}
			Main.sendError (player, "That sign is not an ItemSign!");
		} else {
			Main.sendError (player, "You aren't looking at a sign or you are too far away!");
		}
	}
	
	private void commandCheck (CommandSender sender) {
		int count = main.itemSigns.size ();
		main.itemSigns.removeIf (itemSign -> !Main.isSign (itemSign.location.getBlock ().getType ()));
		main.saveData ();
		Main.sendMessage (sender, Integer.toString (count - main.itemSigns.size ()) + " no longer valid ItemSigns have been removed.");
	}
	
	private void commandColor (Player player) {
		Block block = player.getTargetBlock ((Set<Material>) null, 16);
		if (block != null && Main.isSign (block.getType ())) {
			Sign sign = (Sign)block.getState ();
			for (int i = 0; i < sign.getLines ().length; i++) {
				sign.setLine (i, ChatColor.translateAlternateColorCodes ('&', sign.getLine (i)));
			}
			sign.update ();
			Main.sendMessage (player, "You have successfully colored the sign's text.");
		} else {
			Main.sendError (player, "You aren't looking at a sign or you are too far away!");
		}
	}
	
	private void commandEdit (Player player, String[] args) {
		if (args[1].matches ("[1-4]")) {
			int number = Integer.valueOf (args[1]);
			Block block = player.getTargetBlock ((Set<Material>)null, 16);
			if (block != null && Main.isSign (block.getType ())) {
				Sign sign = (Sign)block.getState ();
				
				String text = "";
				for (int i = 2; i < args.length; i++) {
					text += args[i] + " ";
				}
				text = text.substring (0, text.length () - 1);
				
				sign.setLine (number - 1, ChatColor.translateAlternateColorCodes ('&', text));
				sign.update ();
				Main.sendMessage (player, "You have successfully set the sign's " + number + ". line.");
			} else {
				Main.sendError (player, "You aren't looking at a sign or you are too far away!");
			}
		} else {
			Main.sendError (player, "You have specified an incorrect line number! It must be either 1, 2, 3 or 4.");
		}
	}
	
	
	
	private ItemStack getItemInHand (Player player) {
		//return player.getInventory ().getItemInMainHand ();
		
		//noinspection deprecation
		return player.getItemInHand (); //For 1.8 and below
	}
	
	private boolean playerCheck (CommandSender sender) {
		if (sender instanceof Player) {
			return true;
		} else {
			Main.sendError (sender, "Only players can use this command!");
			return false;
		}
	}
}