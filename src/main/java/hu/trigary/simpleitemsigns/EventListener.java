package hu.trigary.simpleitemsigns;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventListener implements Listener {
	EventListener (Main main) {
		this.main = main;
	}
	
	private Main main;
	
	@SuppressWarnings ("unused")
	@EventHandler (ignoreCancelled = true)
	public void onPlayerInteract (PlayerInteractEvent event) {
		if (event.getAction () == Action.RIGHT_CLICK_BLOCK && Main.isSign (event.getClickedBlock ().getType ()) &&
				(!main.needUsePermission || event.getPlayer ().hasPermission ("simpleitemsigns.use"))) {
			Location location = event.getClickedBlock ().getLocation ();
			for (ItemSign itemSign : main.itemSigns) {
				if (itemSign.location.equals (location)) {
					event.getPlayer ().openInventory (itemSign.createInventory (main.size));
					event.setCancelled (true);
					break;
				}
			}
		}
	}
	
	@SuppressWarnings ("unused")
	@EventHandler (ignoreCancelled = true)
	public void onInventoryClose (InventoryCloseEvent event) {
		if (main.dontTrash && event.getInventory ().getHolder () instanceof ItemSign) {
			ItemSign itemSign = (ItemSign)event.getInventory ().getHolder ();
			
			Material material = itemSign.item.getType ();
			short durability = itemSign.item.getDurability ();
			ItemMeta meta = itemSign.item.getItemMeta ();
			
			int free = 0;
			int count = 0;
			int amount = 0;
			
			for (ItemStack item : event.getInventory ().getContents ()) {
				if (item == null) {
					free++;
				} else if (item.getType () == material && item.getDurability () == durability && item.getItemMeta ().equals (meta)) {
					count++;
					amount += item.getAmount ();
				} else {
					free++;
					itemSign.dropItem (item);
				}
			}
			
			int overflow = amount - ((count + free) * itemSign.item.getAmount ());
			if (overflow > 0) {
				while (true) {
					if (overflow <= 64) {
						itemSign.dropItem (new ItemStack (material, overflow));
						break;
					} else {
						itemSign.dropItem (new ItemStack (material, 64));
						overflow -= 64;
					}
				}
			}
		}
	}
}