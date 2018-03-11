package hu.trigary.simpleitemsigns;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
	private final Main main;
	private final boolean needUsePermission;
	private final int size;
	private final boolean dontTrash;
	
	public EventListener(Main main, boolean needUsePermission, int size, boolean dontTrash) {
		this.main = main;
		this.needUsePermission = needUsePermission;
		this.size = size;
		this.dontTrash = dontTrash;
	}
	
	
	
	@EventHandler(ignoreCancelled = true)
	private void onPlayerClickSign(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !Utils.isSign(event.getClickedBlock())) {
			return;
		}
		
		if (needUsePermission && !event.getPlayer().hasPermission("simpleitemsigns.use")) {
			return;
		}
		
		ItemSign itemSign = main.getItemSign(event.getClickedBlock());
		if (itemSign != null) {
			itemSign.openInventory(event.getPlayer(), size);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void onSignInventoryClose(InventoryCloseEvent event) {
		if (!dontTrash || !(event.getInventory().getHolder() instanceof ItemSign) || !(event.getPlayer() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getPlayer();
		ItemSign itemSign = (ItemSign) event.getInventory().getHolder();
		ItemStack clone = itemSign.getItemClone();
		
		int free = 0;
		int count = 0;
		int amount = 0;
		
		for (ItemStack item : event.getInventory().getContents()) {
			if (item == null) {
				free++;
			} else if (clone.isSimilar(item)) {
				count++;
				amount += item.getAmount();
			} else {
				free++;
				dropItem(player, item);
			}
		}
		
		int overflow = amount - ((count + free) * clone.getAmount());
		if (overflow > 0) {
			int maxStackSize = clone.getMaxStackSize();
			clone.setAmount(maxStackSize);
			
			while (true) {
				if (overflow <= maxStackSize) {
					clone.setAmount(overflow);
					dropItem(player, clone);
					break;
				} else {
					dropItem(player, clone);
					overflow -= maxStackSize;
				}
			}
		}
	}
	
	
	
	private void dropItem(Player player, ItemStack item) {
		player.getWorld().dropItem(player.getLocation(), item);
	}
}
