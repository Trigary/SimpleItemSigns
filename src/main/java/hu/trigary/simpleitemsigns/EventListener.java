package hu.trigary.simpleitemsigns;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
	EventListener (Main main) {
		this.main = main;
	}
	
	private Main main;
	
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
		
		Inventory inventory = Bukkit.createInventory (event.getPlayer (), main.inventorySize, sign.getTitle ());
		ItemStack[] items = new ItemStack[main.inventorySize];
		for (int i = 0; i < items.length; i++) {
			items[i] = sign.getItem ();
		}
		inventory.setContents (items);
		event.getPlayer ().openInventory (inventory);
	}
}