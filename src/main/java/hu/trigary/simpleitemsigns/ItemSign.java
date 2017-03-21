package hu.trigary.simpleitemsigns;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

class ItemSign implements InventoryHolder {
	ItemSign (Location location, ItemStack item, String title) {
		this.location = location;
		this.item = new ItemStack (item.getType (), item.getAmount (), item.getDurability ());
		if (item.hasItemMeta ()) {
			this.item.setItemMeta (item.getItemMeta ());
		}
		this.title = title;
	}
	
	private final Location location;
	private final ItemStack item;
	private final String title;
	
	@Override
	public Inventory getInventory () {
		return null;
	}
	
	boolean isSign () {
		return Main.isSign (location.getBlock ().getType ());
	}
	
	boolean matches (Location loc) {
		return location.equals (loc);
	}
	
	Inventory createInventory (int inventorySize) {
		Inventory inventory = Bukkit.createInventory (this, inventorySize, title);
		ItemStack[] items = new ItemStack[inventorySize];
		for (int i = 0; i < items.length; i++) {
			items[i] = item;
		}
		inventory.setContents (items);
		return inventory;
	}
	
	ItemStack getItem () {
		return item;
	}
	
	void dropItem (ItemStack item) {
		location.getWorld ().dropItem (location.clone ().add (0.5, 0.5, 0.5), item);
	}
	
	ItemSignStorable getStorable () {
		return new ItemSignStorable (location, item, title);
	}
}