package com.meow.meowuid;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.meow.meowuid.MeowUID;

public class MeowUIDPlaceholderExpansion extends PlaceholderExpansion {

    private final MeowUID plugin;

    public MeowUIDPlaceholderExpansion(MeowUID plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "meow_uid";  // PAPI中的标识符
    }

    @Override
    public String getAuthor() {
        return "Zhang1233";  // 插件作者名
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();  // 返回当前插件版本
    }

    @Override
    public String onRequest(Player player, String params) {
        if (player == null) {
            return null;  // 如果玩家对象为null，则返回null
        }

        // 获取玩家的 UID
        String playerUID = plugin.FindUID(player.getUniqueId().toString());

        if (playerUID == null) {
            return "Unknown UID";  // 如果没有找到 UID，返回 "Unknown UID"
        }

        return playerUID;  // 如果找到 UID，直接返回该 UID
    }
}
