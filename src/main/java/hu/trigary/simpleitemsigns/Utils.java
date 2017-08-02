package hu.trigary.simpleitemsigns;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

public class Utils {
	private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "SimpleItemSigns" + ChatColor.GRAY + "] ";
	
	public static String argsToString(String[] args, int start) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = start; i < args.length; ) {
			stringBuilder.append(args[i]);
			i++;
			
			if (i < args.length) {
				stringBuilder.append(' ');
			} else {
				break;
			}
		}
		
		return stringBuilder.toString();
	}
	
	
	
	public static void sendMessage(CommandSender recipient, String message) {
		recipient.sendMessage(PREFIX + ChatColor.WHITE + message);
	}
	
	public static void sendError(CommandSender recipient, String error) {
		recipient.sendMessage(PREFIX + ChatColor.RED + error);
	}
	
	public static boolean isSign(Block block) {
		if (block == null) {
			return false;
		}
		
		Material material = block.getType();
		return material == Material.SIGN_POST || material == Material.WALL_SIGN;
	}
}
