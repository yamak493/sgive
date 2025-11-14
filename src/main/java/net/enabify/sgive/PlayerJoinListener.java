package net.enabify.sgive;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {
    private final Sgive plugin;
    private final PendingItemsManager pendingItemsManager;

    public PlayerJoinListener(Sgive plugin, PendingItemsManager pendingItemsManager) {
        this.plugin = plugin;
        this.pendingItemsManager = pendingItemsManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (pendingItemsManager.hasPendingItems(player.getUniqueId())) {
            // グローバルリージョンスケジューラーでメインスレッドからコマンドを実行
            Bukkit.getGlobalRegionScheduler().run(plugin, (task) -> {
                List<PendingItem> items = pendingItemsManager.getPendingItems(player.getUniqueId());

                for (PendingItem item : items) {
                    String command = "give " + player.getName() + " " + item.getItemSpec() + " " + item.getAmount();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }

                if (!items.isEmpty()) {
                    player.sendMessage("§aアイテムを受け取りました！§fインベントリーが満杯だと受け取れない場合があります。");
                }

                // 保留アイテムを削除
                pendingItemsManager.removePendingItems(player.getUniqueId());
            });
        }
    }
}
