package com.meow.meowfly;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Arrays;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;

public class MeowFly extends JavaPlugin implements Listener {

    // 一堆存储消息的变量
    private String startupMessage;
    private String shutdownMessage;
    private String nowusingversionMessage;
    private String checkingupdateMessage;
    private String checkfailedMessage;
    private String updateavailableMessage;
    private String updateurlMessage;
    private String oldversionmaycauseproblemMessage;
    private String nowusinglatestversionMessage;
    private String reloadedMessage;
    private String nopermissionMessage;
    private String flyModeEnabledMessage;
    private String flyModeDisabledMessage;
    private String usageMessage;
    private String notplayerMessage;
    private String failedtocreateymlfileMessage;
    private String failedtoreadymlstatusMessage;
    private String failedtosaveymlstatusMessage;
    private String failedtoclosedatabaseconnectionMessage;
    private String failedtoconnectdatabaseMessage;
    private String failedtoreaddatabaseMessage;
    private String failedtosavedatabaseMessage;

    // 数据库或本地存储配置
    private String storageType;
    private Connection connection;
    private File dataFile;
    private Map<String, Boolean> flightData;

    @Override
    public void onEnable() {
        // bstats
        int pluginId = 23964;
        Metrics metrics = new Metrics(this, pluginId);
        // 加载语言配置和默认配置文件
        saveDefaultConfig();
        loadLanguage();
        // 初始化存储
        storageType = getConfig().getString("storage", "yml");
        if (storageType.equalsIgnoreCase("mysql")) {
            initMySQL();
        } else {
            initLocalStorage();
        }

        // 注册事件
        getServer().getPluginManager().registerEvents(this, this);

        // 启动时的版本检查
        getLogger().info(startupMessage);
        String currentVersion = getDescription().getVersion();
        getLogger().info(nowusingversionMessage + " v" + currentVersion);
        getLogger().info(checkingupdateMessage);
        new BukkitRunnable() {
            @Override
            public void run() {
                check_update();
            }
        }.runTaskAsynchronously(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // 如果没有提供任何参数，提示 "use" 和 "reload"
        if (args.length == 1) {
            // 创建一个匹配的候选列表
            List<String> commands = Arrays.asList("use", "reload");

            // 根据输入的字符串，找出部分匹配的命令
            for (String commandOption : commands) {
                if (commandOption.startsWith(args[0].toLowerCase())) {
                    suggestions.add(commandOption);  // 添加匹配的建议到 suggestions 列表
                }
            }
        }
        // 如果是 "mfly use"，没有额外的参数，返回空列表
        else if (args.length == 2 && args[0].equalsIgnoreCase("use")) {
            // 无额外补全需要
        }
        // 如果是 "mfly reload"，没有额外的参数，返回空列表
        else if (args.length == 2 && args[0].equalsIgnoreCase("reload")) {
            // 无额外补全需要
        }

        return suggestions;
    }


    private void loadLanguage() {
        FileConfiguration config = getConfig();
        String language = config.getString("language", "zh_cn");

        if ("zh_cn".equalsIgnoreCase(language)) {
            // 中文消息
            startupMessage = "MeowFly 已加载!";
            shutdownMessage = "MeowFly 已卸载!";
            nowusingversionMessage = "当前使用版本:";
            checkingupdateMessage = "正在检查更新...";
            checkfailedMessage = "检查更新失败，请检查你的网络状况!";
            updateavailableMessage = "发现新版本:";
            updateurlMessage = "新版本下载地址:";
            oldversionmaycauseproblemMessage = "旧版本可能会导致问题，请尽快更新!";
            nowusinglatestversionMessage = "您正在使用最新版本!";
            reloadedMessage = "配置文件已重载!";
            nopermissionMessage = "你没有权限执行此命令!";
            flyModeEnabledMessage = "§a飞行模式已开启!";
            flyModeDisabledMessage = "§c飞行模式已关闭!";
            usageMessage = "用法:";
            notplayerMessage = "只有玩家才能执行此命令!";
            failedtocreateymlfileMessage = "无法创建飞行状态文件, 请检查你的硬盘是否已满!";
            failedtoreadymlstatusMessage = "无法读取飞行状态文件, 请检查你的配置文件是否损坏!";
            failedtosaveymlstatusMessage = "无法保存飞行状态文件, 请检查你的配置文件是否损坏!";
            failedtoclosedatabaseconnectionMessage = "无法关闭数据库连接, 请检查你的数据库配置!";
            failedtoconnectdatabaseMessage = "无法连接至数据库, 请检查你的数据库配置!";
            failedtoreaddatabaseMessage = "无法读取数据库数据, 请检查你的数据库配置!";
            failedtosavedatabaseMessage = "无法保存数据库数据, 请检查你的数据库配置!";
        } else {
            // English message
            startupMessage = "MeowFly has been loaded!";
            shutdownMessage = "MeowFly has been disabled!";
            nowusingversionMessage = "Currently using version:";
            checkingupdateMessage = "Checking for updates...";
            checkfailedMessage = "Update check failed, please check your network!";
            updateavailableMessage = "A new version is available:";
            updateurlMessage = "Download update at:";
            oldversionmaycauseproblemMessage = "Old versions may cause problems!";
            nowusinglatestversionMessage = "You are using the latest version!";
            reloadedMessage = "Configuration file has been reloaded!";
            nopermissionMessage = "You do not have permission to execute this command!";
            flyModeEnabledMessage = "§aFly mode enabled!";
            flyModeDisabledMessage = "§cFly mode disabled!";
            usageMessage = "Usage:";
            notplayerMessage = "Only players can execute this command!";
            failedtocreateymlfileMessage = "Failed to create the flight status file. Please check if your hard drive is full!";
            failedtoreadymlstatusMessage = "Failed to read the flight status file. Please check if your configuration file is corrupted!";
            failedtosaveymlstatusMessage = "Failed to save the flight status file. Please check if your configuration file is corrupted!";
            failedtoclosedatabaseconnectionMessage = "Failed to close the database connection. Please check your database configuration!";
            failedtoconnectdatabaseMessage = "Failed to connect to the database. Please check your database configuration!";
            failedtoreaddatabaseMessage = "Failed to read data from the database. Please check your database configuration!";
            failedtosavedatabaseMessage = "Failed to save data to the database. Please check your database configuration!";
        }
    }

    private void initLocalStorage() {
        flightData = new HashMap<>();
        dataFile = new File(getDataFolder(), "flight_data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                getLogger().warning(failedtocreateymlfileMessage);
            }
        } else {
            loadLocalFlightData();
        }
    }

    private void loadLocalFlightData() {
        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
            for (String playerName : config.getKeys(false)) {
                flightData.put(playerName, config.getBoolean(playerName, false));
            }
        } catch (Exception e) {
            getLogger().warning(failedtoreadymlstatusMessage);
        }
    }

    private void saveLocalFlightData() {
        try {
            FileConfiguration config = new YamlConfiguration();
            for (Map.Entry<String, Boolean> entry : flightData.entrySet()) {
                config.set(entry.getKey(), entry.getValue());
            }
            config.save(dataFile);
        } catch (IOException e) {
            getLogger().warning(failedtosaveymlstatusMessage);
        }
    }

    private void initMySQL() {
        try {
            String host = getConfig().getString("mysql.host");
            int port = getConfig().getInt("mysql.port");
            String database = getConfig().getString("mysql.database");
            String username = getConfig().getString("mysql.username");
            String password = getConfig().getString("mysql.password");

            connection = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);

            // 创建表
            connection.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS player_flight (player_name VARCHAR(50) PRIMARY KEY, flight_status BOOLEAN)");

        } catch (SQLException e) {
            getLogger().warning(failedtoconnectdatabaseMessage);
        }
    }

    private boolean getFlightStatus(String playerName) {
        if (storageType.equalsIgnoreCase("mysql")) {
            try {
                PreparedStatement ps = connection.prepareStatement(
                    "SELECT flight_status FROM player_flight WHERE player_name = ?");
                ps.setString(1, playerName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getBoolean("flight_status");
                }
            } catch (SQLException e) {
                getLogger().warning(failedtoreaddatabaseMessage);
            }
        } else {
            return flightData.getOrDefault(playerName, false);
        }
        return false;
    }

    private void setFlightStatus(String playerName, boolean status) {
        if (storageType.equalsIgnoreCase("mysql")) {
            try {
                PreparedStatement ps = connection.prepareStatement(
                    "REPLACE INTO player_flight (player_name, flight_status) VALUES (?, ?)");
                ps.setString(1, playerName);
                ps.setBoolean(2, status);
                ps.executeUpdate();
            } catch (SQLException e) {
                getLogger().warning(failedtosavedatabaseMessage);
            }
        } else {
            flightData.put(playerName, status);
            saveLocalFlightData();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean shouldAllowFlight = getFlightStatus(player.getName());
                // 检查权限
                if (!player.hasPermission("meowfly.use")) {
                    // 恢复飞行权限但不直接飞行
                    player.setAllowFlight(shouldAllowFlight);
                    player.setFlying(false);
                }
            }
        }.runTaskLater(this, 5L);  // 5L 是延迟的 tick 数量
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        setFlightStatus(player.getName(), player.getAllowFlight());
    }

    @Override
    public void onDisable() {
        getLogger().info(shutdownMessage);
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                getLogger().warning(failedtoclosedatabaseconnectionMessage);
            }
        } else {
            saveLocalFlightData();
        }
    }

    public void closeDatabaseConnection() {
        if (connection != null) {
            try {
                connection.close(); // 关闭数据库连接
                connection = null;  // 将连接设置为 null，表示已关闭
            } catch (SQLException e) {
                // 如果关闭连接时发生错误，捕获并打印异常信息
                getLogger().warning("Failed to close database connection: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mfly")) {
            if (args.length == 0) {
                sender.sendMessage(usageMessage + " /mfly <use|reload>");
                return true;
            }

            // 重新加载配置命令
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("meowfly.reload")) {
                    // 获取当前存储类型
                    String currentStorageType = getConfig().getString("storage", "yml"); // 默认是 yml

                    // 调用配置加载方法
                    saveDefaultConfig();
                    reloadConfig();
                    loadLanguage();

                    // 获取新的存储类型
                    String newStorageType = getConfig().getString("storage", "yml");

                    // 如果存储类型改变了，进行相应的处理
                    if (!currentStorageType.equals(newStorageType)) {
                        if (currentStorageType.equals("mysql") && newStorageType.equals("yml")) {
                            // 从 MySQL 改为 yml，关闭数据库连接并初始化本地文件
                            closeDatabaseConnection();
                            initLocalStorage(); // 重新初始化
                        } else if (currentStorageType.equals("yml") && newStorageType.equals("mysql")) {
                            // 从 yml 改为 MySQL，进行数据库初始化
                            initLocalStorage(); // 初始化
                        }
                    }

                    sender.sendMessage(ChatColor.GREEN + reloadedMessage);
                } else {
                    sender.sendMessage(ChatColor.RED + nopermissionMessage);
                }
                return true;
            }


            if (args[0].equalsIgnoreCase("use")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + notplayerMessage);
                    return true;
                }
                Player player = (Player) sender;

                // 检查权限
                if (!player.hasPermission("meowfly.use")) {
                    player.sendMessage(ChatColor.RED + nopermissionMessage);
                    return true;
                }

                // 切换飞行模式
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(ChatColor.RED + flyModeDisabledMessage);
                } else {
                    player.setAllowFlight(true);
                    player.sendMessage(ChatColor.GREEN + flyModeEnabledMessage);
                }
                return true;
            }
        }
        return false;
    }

    // 检查更新方法
    private void check_update() {
        // 获取当前版本号
        String currentVersion = getDescription().getVersion();
        // github加速地址，挨个尝试
        String[] githubUrls = {
            "https://ghp.ci/",
            "https://raw.fastgit.org/",
            ""
            //最后使用源地址
        };
        // 获取 github release 最新版本号作为最新版本
        // 仓库地址：https://github.com/Zhang12334/MeowFly
        String latestVersionUrl = "https://github.com/Zhang12334/MeowFly/releases/latest";
        // 获取版本号
        try {
            String latestVersion = null;
            for (String url : githubUrls) {
                HttpURLConnection connection = (HttpURLConnection) new URL(url + latestVersionUrl).openConnection();
                connection.setInstanceFollowRedirects(false); // 不自动跟随重定向
                int responseCode = connection.getResponseCode();
                if (responseCode == 302) { // 如果 302 了
                    String redirectUrl = connection.getHeaderField("Location");
                    if (redirectUrl != null && redirectUrl.contains("tag/")) {
                        // 从重定向URL中提取版本号
                        latestVersion = extractVersionFromUrl(redirectUrl);
                        break; // 找到版本号后退出循环
                    }
                }
                connection.disconnect();
                if (latestVersion != null) {
                    break; // 找到版本号后退出循环
                }
            }
            if (latestVersion == null) {
                getLogger().warning(checkfailedMessage);
                return;
            }
            // 比较版本号
            if (isVersionGreater(latestVersion, currentVersion)) {
                // 如果有新版本，则提示新版本
                getLogger().warning(updateavailableMessage + " v" + latestVersion);
                // 提示下载地址（latest release地址）
                getLogger().warning(updateurlMessage + " https://github.com/Zhang12334/MeowFly/releases/latest");
                getLogger().warning(oldversionmaycauseproblemMessage);
            } else {
                getLogger().info(nowusinglatestversionMessage);
            }
        } catch (Exception e) {
            getLogger().warning(checkfailedMessage);
        }
    }

    // 版本比较
    private boolean isVersionGreater(String version1, String version2) {
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");
        for (int i = 0; i < Math.max(v1Parts.length, v2Parts.length); i++) {
            int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;
            if (v1Part > v2Part) {
                return true;
            } else if (v1Part < v2Part) {
                return false;
            }
        }
        return false;
    }
    
    private String extractVersionFromUrl(String url) {
        // 解析 302 URL 中的版本号
        int tagIndex = url.indexOf("tag/");
        if (tagIndex != -1) {
            int endIndex = url.indexOf('/', tagIndex + 4);
            if (endIndex == -1) {
                endIndex = url.length();
            }
            return url.substring(tagIndex + 4, endIndex);
        }
        return null;
    }
}
