package com.meow.meowuid;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MeowUID extends JavaPlugin implements Listener {

    private Connection connection;
    private String host, database, username, password;
    private int port;
    private boolean enablePlugin;
    private long startingUid;
    private static final String TABLE_NAME = "player_uid";
    private final ConcurrentHashMap<String, Long> cache = new ConcurrentHashMap<>(); // 缓存
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        // bstats
        int pluginId = 24002;
        Metrics metrics = new Metrics(this, pluginId);        
        saveDefaultConfig();
        loadConfig();

        // 初始化 LanguageManager
        languageManager = new LanguageManager(getConfig());

        // 检查插件是否启用
        if (!enablePlugin) {
            // 启动消息
            getLogger().info(languageManager.getMessage("startup"));
            String currentVersion = getDescription().getVersion();
            getLogger().info(languageManager.getMessage("nowusingversion") + " v" + currentVersion);
            getLogger().info(languageManager.getMessage("checkingupdate"));
            // 禁用消息
            getLogger().info(languageManager.getMessage("PluginDisabled"));
            return; // 如果未启用插件，则停止后续操作
        }

        // 检查前置库是否加载
        if (!Bukkit.getPluginManager().isPluginEnabled("MeowLibs")) {
            getLogger().warning(languageManager.getMessage("CanNotFoundMeowLibs"));
            // 禁用插件
            getServer().getPluginManager().disablePlugin(this); 
            return;           
        }

        // 开始加载插件

        // 翻译者
        getLogger().info(languageManager.getMessage("TranslationContributors"));

        // 注册事件和命令
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("uid").setExecutor(this);
        getCommand("uid").setTabCompleter(this);

        // 连接数据库
        connectDatabase();

        // 注册自定义占位符
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new MeowUIDPlaceholderExpansion(this).register();  // 注册占位符扩展
        } else {
            getLogger().warning(languageManager.getMessage("CanNotFoundPAPI"));
        }

        // 启动消息
        getLogger().info(languageManager.getMessage("startup"));
        String currentVersion = getDescription().getVersion();
        getLogger().info(languageManager.getMessage("nowusingversion") + " v" + currentVersion);
        getLogger().info(languageManager.getMessage("checkingupdate"));

        // 创建 CheckUpdate 实例
        CheckUpdate updateChecker = new CheckUpdate(
            getLogger(), // log记录器
            languageManager, // 语言管理器
            getDescription() // 插件版本信息
        );        

        // 异步执行更新检查
        new BukkitRunnable() {
            @Override
            public void run() {
                updateChecker.checkUpdate();
            }
        }.runTaskAsynchronously(this);
    }

    @Override
    public void onDisable() {
        // 关闭数据库连接
        disconnectDatabase();
    }

    // 加载配置文件
    private void loadConfig() {
        enablePlugin = getConfig().getBoolean("enable-plugin", false);
        startingUid = getConfig().getLong("starting_uid", 1000000);
        host = getConfig().getString("mysql.host", "localhost");
        port = getConfig().getInt("mysql.port", 3306);
        database = getConfig().getString("mysql.database", "meow_uid");
        username = getConfig().getString("mysql.username", "root");
        password = getConfig().getString("mysql.password", "");
    }

    // 数据库连接
    private void connectDatabase() {
        try {
            if (connection != null && !connection.isClosed()) return;
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true";
            connection = DriverManager.getConnection(url, username, password);

            // 创建表格
            try (PreparedStatement ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "uid BIGINT NOT NULL)"
            )) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            getLogger().severe(languageManager.getMessage("CanNotConnectDatabase") + " " + e.getMessage());
        }
    }

    private void disconnectDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }    

    // 处理玩家进入事件
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enablePlugin) return; // 如果插件未启用，则不执行任何逻辑

        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();
        String playerId = player.getName();

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement ps = connection.prepareStatement(
                        "SELECT uid FROM " + TABLE_NAME + " WHERE uuid = ?"
                    );
                    ps.setString(1, playerUUID);
                    ResultSet rs = ps.executeQuery();

                    if (!rs.next()) {
                        // 为新玩家分配 UID
                        long newUid = getNextAvailableUid();
                        PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO " + TABLE_NAME + " (uuid, uid) VALUES (?, ?)"
                        );
                        insert.setString(1, playerUUID);
                        insert.setLong(2, newUid);
                        insert.executeUpdate();
                        insert.close();
                        getLogger().info(String.format(languageManager.getMessage("RegistUID"), playerId, newUid));
                    }
                    rs.close();
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this); //异步
    }

    // 获取下一个可用的 UID
    private long getNextAvailableUid() {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT MAX(uid) AS max_uid FROM " + TABLE_NAME
            );
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long maxUid = rs.getLong("max_uid");
                rs.close();
                ps.close();
                return (maxUid >= startingUid) ? maxUid + 1 : startingUid + 1;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return startingUid + 1;
    }

    // 处理命令
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!enablePlugin) {
            // 未启用插件, 返回提示信息
            sender.sendMessage(languageManager.getMessage("PluginDisabled"));
            return true;
        }

        if (command.getName().equalsIgnoreCase("uid")) {
            GetUID commandGetUID = new GetUID(this, sender, languageManager, connection);
            if (args.length >= 2 && args[0].equalsIgnoreCase("find")) {
                if (args[1].equalsIgnoreCase("id") && args.length == 3) {
                    String playerId = args[2];
                    commandGetUID.findUidById(sender, playerId);
                } else if (args[1].equalsIgnoreCase("uid") && args.length == 3) {
                    try {
                        long uid = Long.parseLong(args[2]);
                        commandGetUID.findIdByUid(sender, uid);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(languageManager.getMessage("InvalidUid"));
                    }
                } else {
                    sender.sendMessage(languageManager.getMessage("usage") + " /uid find <id/uid> <PlayerID / PlayerUID>");
                }
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                loadConfig();
                // 清空缓存
                cache.clear();
                // 重新初始化 LanguageManager
                languageManager = new LanguageManager(getConfig());
                // 重新连接数据库并初始化 GetUID
                disconnectDatabase();
                connectDatabase();
                sender.sendMessage(languageManager.getMessage("reloaded"));
                return true;
            }
        }
        return false;
    }

    // 提供 TAB 补全
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!enablePlugin) return null; // 如果插件未启用，禁用补全

        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("find");
            completions.add("reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("find")) {
            completions.add("id");
            completions.add("uid");
        }
        return completions;
    }
}
