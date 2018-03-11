package hu.trigary.simpleitemsigns;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

public class Utils {
	private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GOLD + "SimpleItemSigns" + ChatColor.GRAY + "] ";
	
	
	
	public static String argsToString(String[] args, int start) {
		StringBuilder builder = new StringBuilder();
		for (int i = start; i < args.length; ) {
			builder.append(args[i++]);
			if (i < args.length) {
				builder.append(' ');
			} else {
				break;
			}
		}
		return builder.toString();
	}
	
	
	
	public static void sendMessage(CommandSender recipient, String message) {
		recipient.sendMessage(PREFIX + ChatColor.WHITE + message);
	}
	
	public static void sendError(CommandSender recipient, String error) {
		recipient.sendMessage(PREFIX + ChatColor.RED + error);
	}
	
	
	
	public static boolean isSign(Block block) {
		return block != null && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN);
	}
}
