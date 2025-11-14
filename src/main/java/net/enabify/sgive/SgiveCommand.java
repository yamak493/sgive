package net.enabify.sgive;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SgiveCommand implements CommandExecutor {
    private final Sgive plugin;
    private final PendingItemsManager pendingItemsManager;

    public SgiveCommand(Sgive plugin, PendingItemsManager pendingItemsManager) {
        this.plugin = plugin;
        this.pendingItemsManager = pendingItemsManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sgive.use")) {
            sender.sendMessage("§c権限がありません。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c使用法: /sgive <プレイヤー名> <アイテム指定> [アイテム数]");
            return true;
        }

        String targetPlayerName = args[0];
        
        // アイテム指定を再構築（スペースを含む可能性がある）
        int amountIndex = -1;
        int amount = 1;
        
        // 最後の引数が数字かチェック
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[args.length - 1]);
                if (amount <= 0) {
                    sender.sendMessage("§cアイテム数は1以上でなければなりません。");
                    return true;
                }
                amountIndex = args.length - 1;
            } catch (NumberFormatException e) {
                // 最後の引数は数字ではない
                amountIndex = args.length;
            }
        } else {
            amountIndex = args.length;
        }

        // アイテム指定を再構築
        StringBuilder itemSpecBuilder = new StringBuilder();
        for (int i = 1; i < amountIndex; i++) {
            if (i > 1) itemSpecBuilder.append(" ");
            itemSpecBuilder.append(args[i]);
        }
        String itemSpec = itemSpecBuilder.toString();

        if (itemSpec.isEmpty()) {
            sender.sendMessage("§c使用法: /sgive <プレイヤー名> <アイテム指定> [アイテム数]");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

        if (targetPlayer != null && targetPlayer.isOnline()) {
            // プレイヤーがオンラインの場合、グローバルリージョンスケジューラーでメインスレッドから/giveコマンドを実行
            final int finalAmount = amount;
            Bukkit.getGlobalRegionScheduler().run(plugin, (task) -> {
                executeGiveCommand(targetPlayerName, itemSpec, finalAmount);
            });
            sender.sendMessage("§a" + targetPlayerName + " に アイテムを付与しました。");
            targetPlayer.sendMessage("§aアイテムを受け取りました！§fインベントリーが満杯だと受け取れない場合があります。");

        } else {
            // プレイヤーがオフラインの場合、保留リストに追加
            UUID targetUuid = Bukkit.getOfflinePlayer(targetPlayerName).getUniqueId();
            pendingItemsManager.addPendingItem(targetUuid, itemSpec, amount);
            sender.sendMessage("§e" + targetPlayerName + " はオフラインです。次回ログイン時に アイテムを付与します。");
        }

        return true;
    }

    private void executeGiveCommand(String playerName, String itemSpec, int amount) {
        String command = "give " + playerName + " " + itemSpec + " " + amount;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
