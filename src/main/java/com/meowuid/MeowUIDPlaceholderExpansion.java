package com.meowuid;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MeowUIDPlaceholderExpansion extends PlaceholderExpansion {

    private final MeowUID plugin;
    private final GetUID getUID;

    // 构造函数，传入插件实例
    public MeowUIDPlaceholderExpansion(MeowUID plugin) {
        this.plugin = plugin;
        // 初始化 GetUID 实例
        this.getUID = new GetUID(plugin, null, new LanguageManager(plugin.getConfig()), plugin.getConnection());
    }

    // 返回扩展标识符
    @Override
    public String getIdentifier() {
        return "meowuid";  // PAPI中的标识符
    }

    // 返回作者名
    @Override
    public String getAuthor() {
        return "Zhang1233";  // 插件作者名
    }

    // 返回插件版本
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();  // 返回当前插件版本
    }
    // 是否保持注册状态
    @Override
    public boolean persist() {
        return true; // 确保占位符在服务器重载后仍然有效
    }

    // 处理占位符请求
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "Player not found";
        }

        String playerId = player.getName();
        Long uid = getUID.getPlayerUIDfromID(playerId); // 调用 GetUID 获取 UID

        // 检查 UID 是否为 null
        return uid != null ? uid.toString() : "UID not found";
    } 
}
