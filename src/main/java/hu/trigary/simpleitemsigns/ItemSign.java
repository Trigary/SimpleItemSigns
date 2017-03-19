package hu.trigary.simpleitemsigns;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

class ItemSign {
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
	
	boolean isSign () {
		return Main.isSign (location.getBlock ().getType ());
	}
	
	boolean matches (Location loc) {
		return location.equals (loc);
	}
	
	ItemStack getItem () {
		return item;
	}
	
	String getTitle () {
		return title;
	}
	
	ItemSignStorable getStorable () {
		return new ItemSignStorable (location, item, title);
	}
}