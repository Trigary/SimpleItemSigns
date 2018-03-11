package hu.trigary.simpleitemsigns;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {
	private final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
	private Map<Location, ItemSign> itemSigns;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		int rows = getConfig().getInt("rows");
		if (rows < 1 || rows > 6) {
			rows = 3;
			getLogger().severe("The specified row amount is invalid; using the default value of 3.");
		}
		
		loadData();
		getCommand("simpleitemsigns").setExecutor(new CommandListener(this));
		
		getServer().getPluginManager().registerEvents(new EventListener(
				this,
				getConfig().getBoolean("needUsePermission"),
				rows * 9,
				getConfig().getBoolean("dontTrash")
		), this);
	}
	
	
	
	public ItemSign getItemSign(Block block) {
		return itemSigns.get(block.getLocation());
	}
	
	public void addItemSign(Block block, ItemSign itemSign) {
		itemSigns.put(block.getLocation(), itemSign);
		saveData();
	}
	
	public boolean removeItemSign(Block block) {
		if (itemSigns.remove(block.getLocation()) != null) {
			saveData();
			return true;
		}
		return false;
	}
	
	public int cleanItemSigns() {
		int size = itemSigns.size();
		itemSigns.keySet().removeIf((location -> !Utils.isSign(location.getBlock())));
		
		int removed = size - itemSigns.size();
		if (removed > 0) {
			saveData();
		}
		return removed;
	}
	
	
	
	private void loadData() {
		itemSigns = new HashMap<>();
		Map<Map<String, Object>, Map<String, Object>> map = loadJson(new TypeToken<Map<Map<String, Object>, Map<String, Object>>>() {}.getType());
		if (map == null) {
			return;
		}
		
		for (Map.Entry<Map<String, Object>, Map<String, Object>> entry : map.entrySet()) {
			itemSigns.put(Location.deserialize(entry.getKey()), ItemSign.deserialize(entry.getValue()));
		}
	}
	
	private void saveData() {
		Map<Map<String, Object>, Map<String, Object>> map = new HashMap<>();
		for (Map.Entry<Location, ItemSign> entry : itemSigns.entrySet()) {
			map.put(entry.getKey().serialize(), entry.getValue().serialize());
		}
		saveJson(map);
	}
	
	
	
	private <T> T loadJson(Type type) {
		File file = new File(getDataFolder(), "data.json");
		if (!file.exists() || file.length() <= 0) {
			return null;
		}
		
		try (Reader reader = new FileReader(file)) {
			return gson.fromJson(reader, type);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load the data file", e);
		}
	}
	
	private void saveJson(Object serializable) {
		File file = new File(getDataFolder(), "data.json");
		try (Writer writer = new FileWriter(file)) {
			writer.write(gson.toJson(serializable));
		} catch (IOException e) {
			throw new RuntimeException("Failed to save the data file", e);
		}
	}
}
