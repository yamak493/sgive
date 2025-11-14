package net.enabify.sgive;

import org.bukkit.plugin.java.JavaPlugin;

public final class Sgive extends JavaPlugin {

    private PendingItemsManager pendingItemsManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Sgive プラグインを有効化しています...");

        // PendingItemsManagerを初期化
        pendingItemsManager = new PendingItemsManager(this);

        // コマンドを登録
        getCommand("sgive").setExecutor(new SgiveCommand(this, pendingItemsManager));

        // イベントリスナーを登録
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, pendingItemsManager), this);

        getLogger().info("Sgive プラグインが有効化されました。");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Sgive プラグインを無効化しています...");

        // 保留中のアイテムを保存
        if (pendingItemsManager != null) {
            pendingItemsManager.saveData();
        }

        getLogger().info("Sgive プラグインが無効化されました。");
    }
}
