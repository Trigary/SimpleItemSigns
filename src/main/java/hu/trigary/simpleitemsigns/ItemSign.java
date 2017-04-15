package hu.trigary.simpleitemsigns;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

class ItemSign implements InventoryHolder {
	ItemSign (Location location, ItemStack item, String title) {
		this.location = location;
		/*this.item = new ItemStack (item.getType (), item.getAmount (), item.getDurability ());
		if (item.hasItemMeta ()) {
			this.item.setItemMeta (item.getItemMeta ());
		}*/
		this.item = item.clone ();
		this.title = title;
	}
	
	final Location location;
	final ItemStack item;
	private final String title;
	
	@Override
	public Inventory getInventory () {
		return null;
	}
	
	Inventory createInventory (int size) {
		Inventory inventory = Bukkit.createInventory (this, size, title);
		ItemStack[] items = new ItemStack[size];
		for (int i = 0; i < items.length; i++) {
			items[i] = item;
		}
		inventory.setContents (items);
		return inventory;
	}
	
	void dropItem (ItemStack item) {
		location.getWorld ().dropItem (location.clone ().add (0.5, 0.5, 0.5), item);
	}
	
	Map<String, Object> serialize () {
		Map<String, Object> map = new HashMap<> ();
		map.put ("location", location.serialize ());
		map.put ("item", serializeItemStack ());
		map.put ("title", title);
		return map;
	}
	
	static ItemSign deserialize (Map<String, Object> serialized) {
		//noinspection unchecked
		return new ItemSign (Location.deserialize ((Map<String, Object>)serialized.get ("location")), deserializeItemStack (serialized.get ("item")), (String)serialized.get ("title"));
	}
	
	private Map<String, Object> serializeItemStack () {
		Map<String, Object> data = new HashMap<> ();
		if (item.hasItemMeta ()) {
			data.put ("meta", item.getItemMeta ().serialize ());
		}
		data.put ("item", new ItemStack (item.getType (), item.getAmount (), item.getDurability ()).serialize ());
		return data;
	}
	
	private static ItemStack deserializeItemStack (Object mapValue) {
		@SuppressWarnings ("unchecked")
		Map<String, Object> serialized = (Map<String, Object>)mapValue;
		
		@SuppressWarnings ("unchecked")
		ItemStack item = ItemStack.deserialize ((Map<String, Object>)serialized.get ("item"));
		
		if (serialized.containsKey ("meta")) {
			//noinspection unchecked
			item.setItemMeta ((ItemMeta)ConfigurationSerialization.deserializeObject ((Map<String, Object>)serialized.get ("meta"), ConfigurationSerialization.getClassByAlias ("ItemMeta")));
		}
		return item;
	}
}