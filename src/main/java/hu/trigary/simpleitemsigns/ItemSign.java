package hu.trigary.simpleitemsigns;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemSign implements InventoryHolder {
	public ItemSign(ItemStack item, String title) {
		this.item = item.clone();
		this.title = title;
	}
	
	private final ItemStack item;
	private final String title;
	
	
	
	@Override
	public Inventory getInventory() {
		return null;
	}
	
	public ItemStack getItemClone() {
		return item.clone();
	}
	
	public void openInventory(Player player, int size) {
		Inventory inventory = Bukkit.createInventory(this, size, title);
		ItemStack[] items = new ItemStack[size];
		Arrays.fill(items, item);
		inventory.setContents(items);
		player.openInventory(inventory);
	}
	
	
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("item", serializeItemStack());
		map.put("title", title);
		return map;
	}
	
	public static ItemSign deserialize(Map<String, Object> serialized) {
		return new ItemSign(deserializeItemStack((String)serialized.get("item")), (String)serialized.get("title"));
	}
	
	
	
	private String serializeItemStack() {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream);
			bukkitOutputStream.writeObject(item);
			bukkitOutputStream.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static ItemStack deserializeItemStack(String data) {
		if (data != null && !data.isEmpty()) {
			try {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
				BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream);
				ItemStack item = (ItemStack)bukkitInputStream.readObject();
				bukkitInputStream.close();
				return item;
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
