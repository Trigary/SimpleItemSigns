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
		if (event.getAction () != Action.RIGHT_CLICK_BLOCK || Main.isSign (event.getClickedBlock ().getType ()) == false
				|| (main.needUsePermission && event.getPlayer ().hasPermission ("simpleitemsigns.use") == false)) {
			return;
		}
		
		ItemSign sign = null;
		Location location = event.getClickedBlock ().getLocation ();
		for (ItemSign storedSign : main.storedSigns) {
			if (storedSign.matches (location)) {
				sign = storedSign;
				break;
			}
		}
		if (sign == null) {
			return;
		}
		
		event.getPlayer ().openInventory (sign.createInventory (main.inventorySize));
	}
	
	@SuppressWarnings ("unused")
	@EventHandler (ignoreCancelled = true)
	public void onInventoryClose (InventoryCloseEvent event) {
		if (main.dontTrash == false || event.getInventory ().getHolder () instanceof ItemSign == false) {
			return;
		}
		
		ItemSign sign = (ItemSign)event.getInventory ().getHolder ();
		Material material = sign.getItem ().getType ();
		short durability = sign.getItem ().getDurability ();
		ItemMeta meta = sign.getItem ().getItemMeta ();
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
				sign.dropItem (item);
			}
		}
		int overflow = amount - ((count + free) * sign.getItem ().getAmount ());
		if (overflow > 0) {
			while (true) {
				if (overflow <= 64) {
					sign.dropItem (new ItemStack (material, overflow));
					break;
				} else {
					sign.dropItem (new ItemStack (material, 64));
					overflow -= 64;
				}
			}
		}
	}
}