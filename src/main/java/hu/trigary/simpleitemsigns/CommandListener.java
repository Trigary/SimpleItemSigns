package hu.trigary.simpleitemsigns;

import org.bukkit.ChatColor;
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
	private final Main main;
	
	public CommandListener(Main main) {
		this.main = main;
	}
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("simpleitemsigns.admin")) {
			Utils.sendError(sender, "You don't have permission to use this command!");
			return true;
		}
		
		if (args.length == 0) {
			return false;
		}
		
		switch (args[0].toLowerCase()) {
			case "create":
				if (args.length > 1) {
					if (playerCheck(sender)) {
						commandCreate((Player) sender, args);
					}
					return true;
				}
				return false;
			case "delete":
				if (args.length == 1) {
					if (playerCheck(sender)) {
						commandDelete((Player) sender);
					}
					return true;
				}
				return false;
			case "check":
				if (args.length == 1) {
					commandCheck(sender);
					return true;
				}
				return false;
			case "color":
				if (args.length == 1) {
					if (playerCheck(sender)) {
						commandColor((Player) sender);
					}
					return true;
				}
				return false;
			case "edit":
				if (args.length > 2) {
					if (playerCheck(sender)) {
						commandEdit((Player) sender, args);
					}
					return true;
				}
				return false;
		}
		
		return false;
	}
	
	
	
	private void commandCreate(Player player, String[] args) {
		Block block = getTargetBlock(player);
		if (block == null) {
			return;
		}
		
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null || item.getType() == Material.AIR) {
			Utils.sendError(player, "You don't have anything in your hand!");
			return;
		}
		
		main.addItemSign(block, new ItemSign(item, ChatColor.translateAlternateColorCodes('&', Utils.argsToString(args, 1))));
		Utils.sendMessage(player, "The ItemSign has been successfully created.");
	}
	
	private void commandDelete(Player player) {
		Block block = getTargetBlock(player);
		if (block == null) {
			return;
		}
		
		if (main.removeItemSign(block)) {
			Utils.sendMessage(player, "The sign has been successfully removed.");
		} else {
			Utils.sendError(player, "That sign is not an ItemSign!");
		}
	}
	
	private void commandCheck(CommandSender sender) {
		Utils.sendMessage(sender, main.cleanItemSigns() + " no longer valid ItemSigns have been removed.");
	}
	
	private void commandColor(Player player) {
		Block block = getTargetBlock(player);
		if (block == null) {
			return;
		}
		
		Sign sign = (Sign) block.getState();
		for (int i = 0; i < sign.getLines().length; i++) {
			sign.setLine(i, ChatColor.translateAlternateColorCodes('&', sign.getLine(i)));
		}
		
		sign.update();
		Utils.sendMessage(player, "You have successfully colored the sign's text.");
	}
	
	private void commandEdit(Player player, String[] args) {
		if (!args[1].matches("[1-4]")) {
			Utils.sendError(player, "You have specified an incorrect line number! It must be either 1, 2, 3 or 4.");
			return;
		}
		
		int number = Integer.valueOf(args[1]);
		Block block = getTargetBlock(player);
		if (block == null) {
			return;
		}
		
		Sign sign = (Sign) block.getState();
		sign.setLine(number - 1, ChatColor.translateAlternateColorCodes('&', Utils.argsToString(args, 2)));
		sign.update();
		Utils.sendMessage(player, "You have successfully set the sign's " + number + ". line.");
	}
	
	
	
	private boolean playerCheck(CommandSender sender) {
		if (sender instanceof Player) {
			return true;
		} else {
			Utils.sendError(sender, "Only players can use this command!");
			return false;
		}
	}
	
	private Block getTargetBlock(Player player) {
		Block block = player.getTargetBlock((Set<Material>) null, 16);
		if (block != null && Utils.isSign(block)) {
			return block;
		}
		
		Utils.sendError(player, "You aren't looking at a sign or you are too far away!");
		return null;
	}
}
