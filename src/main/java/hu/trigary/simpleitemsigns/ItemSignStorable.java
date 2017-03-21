package hu.trigary.simpleitemsigns;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.Map;

class ItemSignStorable implements Serializable {
	ItemSignStorable (Location location, ItemStack item, String title) {
		this.location = location.serialize ();
		if (item.hasItemMeta ()) {
			meta = item.getItemMeta ().serialize ();
		} else {
			meta = null;
		}
		this.item = new ItemStack (item.getType (), item.getAmount (), item.getDurability ()).serialize ();
		this.title = title;
	}
	
	private final Map<String,Object> location;
	private final Map<String,Object> meta;
	private final Map<String,Object> item;
	private final String title;
	
	ItemSign getItemSign () {
		ItemStack itemStack = ItemStack.deserialize (item);
		if (meta != null) {
			itemStack.setItemMeta ((ItemMeta)ConfigurationSerialization.deserializeObject (meta, ConfigurationSerialization.getClassByAlias ("ItemMeta")));
		}
		return new ItemSign (Location.deserialize (location), itemStack, title);
	}
}
