package net.enabify.sgive;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PendingItemsManager {
    private final Sgive plugin;
    private final File dataFile;
    private FileConfiguration data;
    private final Map<UUID, List<PendingItem>> pendingItems;

    public PendingItemsManager(Sgive plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "pending_items.yml");
        this.pendingItems = new HashMap<>();
        loadData();
    }

    private void loadData() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("保留中のアイテムファイルを作成できませんでした: " + e.getMessage());
            }
        }

        data = YamlConfiguration.loadConfiguration(dataFile);

        // YAMLファイルからデータを読み込み
        for (String uuidString : data.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                List<PendingItem> items = new ArrayList<>();

                List<Map<?, ?>> itemList = data.getMapList(uuidString);
                for (Map<?, ?> itemMap : itemList) {
                    String itemSpec = (String) itemMap.get("itemSpec");
                    int amount = (Integer) itemMap.get("amount");

                    if (itemSpec != null && !itemSpec.isEmpty()) {
                        items.add(new PendingItem(itemSpec, amount));
                    }
                }

                if (!items.isEmpty()) {
                    pendingItems.put(uuid, items);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("無効なUUID: " + uuidString);
            }
        }
    }

    public void saveData() {
        data = new YamlConfiguration();

        for (Map.Entry<UUID, List<PendingItem>> entry : pendingItems.entrySet()) {
            List<Map<String, Object>> itemList = new ArrayList<>();

            for (PendingItem item : entry.getValue()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("itemSpec", item.getItemSpec());
                itemMap.put("amount", item.getAmount());
                itemList.add(itemMap);
            }

            data.set(entry.getKey().toString(), itemList);
        }

        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("保留中のアイテムを保存できませんでした: " + e.getMessage());
        }
    }

    public void addPendingItem(UUID playerUuid, String itemSpec, int amount) {
        pendingItems.computeIfAbsent(playerUuid, k -> new ArrayList<>())
                .add(new PendingItem(itemSpec, amount));
        saveData();
    }

    public List<PendingItem> getPendingItems(UUID playerUuid) {
        return pendingItems.getOrDefault(playerUuid, new ArrayList<>());
    }

    public void removePendingItems(UUID playerUuid) {
        pendingItems.remove(playerUuid);
        saveData();
    }

    public boolean hasPendingItems(UUID playerUuid) {
        return pendingItems.containsKey(playerUuid) && !pendingItems.get(playerUuid).isEmpty();
    }
}
