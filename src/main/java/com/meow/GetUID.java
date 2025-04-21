package com.meow;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 处理玩家 UID 和 UUID 查询的类
 */
public class GetUID {
    private final JavaPlugin plugin;    
    private final LanguageManager languageManager;
    private final Connection connection;
    private final Map<UUID, Long> cache = new HashMap<>();
    private static final String TABLE_NAME = "player_uid";
    private final boolean showQueryTime;
    
    public GetUID(JavaPlugin plugin, CommandSender sender, LanguageManager languageManager, Connection connection) {
        this.plugin = plugin;
        this.languageManager = languageManager;
        this.connection = connection;
        this.showQueryTime = plugin.getConfig().getBoolean("show_query_time", false);
    }

    // 异步查找 UID
    public void findUidById(CommandSender sender, String playerId) {
        sender.sendMessage(languageManager.getMessage("finding"));
        long startTime = System.currentTimeMillis(); // 记录开始时间
        new BukkitRunnable() {
            @Override
            public void run() {
                Long uid = getPlayerUIDfromID(playerId);
                String baseMessage = uid != null
                    ? String.format(languageManager.getMessage("FoudedUIDforPlayer"), playerId, uid)
                    : languageManager.getMessage("CanNotFoundPlayerUidById") + " " + playerId;
                
                // 计算耗时并构造最终消息
                long queryTime = System.currentTimeMillis() - startTime;
                final String message = showQueryTime
                    ? baseMessage + " " + String.format(languageManager.getMessage("query_time"), queryTime)
                    : baseMessage;

                // 在主线程发送消息
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sender.sendMessage(message);
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

    // 异步查找玩家名
    public void findIdByUid(CommandSender sender, long uid) {
        sender.sendMessage(languageManager.getMessage("finding"));
        long startTime = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                String playerId = null;
                boolean dbError = false;
                try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT uuid FROM " + TABLE_NAME + " WHERE uid = ?")) {
                    ps.setLong(1, uid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String uuid = rs.getString("uuid");
                            playerId = getPlayerIdFromUUID(uuid);
                        }
                    }
                } catch (SQLException e) {
                    dbError = true;
                }

                String baseMessage = dbError ? languageManager.getMessage("DatabaseError")
                    : playerId != null
                        ? "UID " + uid + " " + languageManager.getMessage("playeridFouded") + " " + playerId
                        : languageManager.getMessage("CanNotFoundPlayerIdByUID") + " " + uid;

                long queryTime = System.currentTimeMillis() - startTime;
                final String message = showQueryTime
                    ? baseMessage + " " + String.format(languageManager.getMessage("query_time"), queryTime)
                    : baseMessage;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sender.sendMessage(message);
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

    // 统一读取UID接口
    public Long getPlayerUIDfromID(String playerId) {
        UUID playerUuid = null;
    
        // 获取在线玩家
        Player onlinePlayer = Bukkit.getPlayerExact(playerId);
        if (onlinePlayer != null) {
            // 找到玩家，获取 UUID
            playerUuid = onlinePlayer.getUniqueId();
        } else {
            // 如果在线玩家未找到，通过网络请求获取 UUID
            playerUuid = getUuidFromPlayerName(playerId);
        }
    
        // 如果 UUID 仍然为 null，返回 null
        if (playerUuid == null) {
            return null;
        }
    
        // 通过 UUID 查找 UID
        return FindUID(playerUuid);
    }

    // 通过 Mojang API 获取 UUID
    private UUID getUuidFromPlayerName(String playerName) {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                ((HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName).openConnection()).getInputStream(),
                StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            String jsonResponse = response.toString();
            if (jsonResponse.isEmpty()) {
                return null;
            }
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
            if (json.has("error")) {
                return null;
            }
            if (!json.has("id")) {
                return null;
            }
            String uuidString = json.get("id").getAsString();
            String formattedUuid = uuidString.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"
            );
            return UUID.fromString(formattedUuid);
        } catch (Exception e) {
            return null;
        }
    }

    // 使用 Mojang API 根据 UUID 获取玩家 ID
    private String getPlayerIdFromUUID(String uuid) {
        try {
            // 构建 Mojang API 的 URL
            String apiUrl = "https://api.mojang.com/user/profile/" + uuid;

            // 创建 URL 对象
            URL url = new URI(apiUrl).toURL();

            // 建立连接
            URLConnection connection = url.openConnection();

            // 读取 API 响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 解析 JSON 响应
            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            if (jsonObject.has("name")) {
                return jsonObject.get("name").getAsString();
            } else {
                // 如果 API 没有返回 name 字段，则返回 null
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null; // 发生错误时返回 null
        }
    }

    // 查找指定 UUID 的 UID
    private Long FindUID(UUID playerUuid) {
        // 从缓存中获取
        if (cache.containsKey(playerUuid)) {
            return cache.get(playerUuid);
        }

        // 如果缓存没有，查询数据库
        Long uid = null;
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT uid FROM " + TABLE_NAME + " WHERE uuid = ?"
            );
            ps.setString(1, playerUuid.toString()); // UUID转为字符串存入数据库
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                uid = rs.getLong("uid"); // 获取UID
                cache.put(playerUuid, uid); // 将 UUID 转换为 String 存入缓存
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uid; // 如果没有找到，返回null
    }
}