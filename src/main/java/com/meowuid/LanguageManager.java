package com.meowuid;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LanguageManager {
    private Map<String, String> messages = new HashMap<>();
    private FileConfiguration config;

    public LanguageManager(FileConfiguration config) {
        this.config = config;
        loadLanguage();
    }

    public void loadLanguage() {
        // 有效的语言列表
        Set<String> validLanguages = new HashSet<>(Arrays.asList("zh_hans", "zh_hant", "en_us", "ja_jp"));

        // 读取配置中的语言设置，默认为zh_hans
        String language = config.getString("language", "zh_hans");

        // 如果读取的语言不在有效列表中，则设为默认值
        if (!validLanguages.contains(language.toLowerCase())) {
            language = "zh_hans";
        }
        messages.clear();

        if ("zh_hans".equalsIgnoreCase(language)) {
            // 中文消息
            messages.put("TranslationContributors", "当前语言: 简体中文 (贡献者: Zhang1233)");
            messages.put("CanNotFoundMeowLibs", "未找到 MeowLibs, 请安装前置依赖 MeowLibs!");
            messages.put("startup", "MeowUID 已加载!");
            messages.put("shutdown", "MeowUID 已卸载!");
            messages.put("nowusingversion", "当前使用版本:");
            messages.put("checkingupdate", "正在检查更新...");
            messages.put("checkfailed", "检查更新失败，请检查你的网络状况!");
            messages.put("updateavailable", "发现新版本:");
            messages.put("updatemessage", "更新内容如下:");
            messages.put("updateurl", "新版本下载地址:");
            messages.put("oldversionmaycauseproblem", "旧版本可能会导致问题，请尽快更新!");
            messages.put("nowusinglatestversion", "您正在使用最新版本!");
            messages.put("reloaded", "配置文件已重载!");
            messages.put("nopermission", "你没有权限执行此命令!");
            messages.put("usage", "用法:");
            messages.put("CanNotConnectDatabase", "无法连接到数据库: %s");
            messages.put("PluginDisabled", "插件目前未启用, 请检查你的配置文件!");
            messages.put("InvalidUid", "无效的 UID 格式!");
            messages.put("FoudedUIDforPlayer", "玩家 %s 的 UID 是 %d");
            messages.put("CanNotFoundPlayerUidById", "找不到以 %s 为名的玩家的 UID");
            messages.put("playeridFouded", "对应的玩家 ID 是:");
            messages.put("CanNotFoundPlayerIdByUID", "找不到 UID %d 对应的玩家");
            messages.put("CanNotFoundPAPI", "未找到 PlaceholderAPI, 无法使用变量查询玩家 UID !");
            messages.put("RegistUID", "已为玩家 %s 注册UID %d");
            messages.put("finding", "正在查询...");
            messages.put("query_time", "(耗时: %dms)");
            messages.put("InvalidUUIDFormat", "无效的 UUID 格式: %s");
            messages.put("FoudedUIDforUUID", "UUID 为 %s 的玩家的 UID 是 %d");
            messages.put("CanNotFoundPlayerUidByUUID", "找不到此 UUID 的玩家对应的 UID: %s");
            messages.put("DatabaseError", "数据库错误！");
            messages.put("QQNotFound", "找不到UID为 %d 的玩家的QQ信息");
            messages.put("QQFound", "UID为 %d 的玩家的QQ是：%s");
            messages.put("QQNotFoundForPlayer", "找不到玩家 %s 的QQ信息");
            messages.put("QQFoundForPlayer", "玩家 %s 的QQ是：%s");
            messages.put("QQNotFoundForUUID", "找不到UUID为 %s 的玩家的QQ信息");
            messages.put("QQFoundForUUID", "UUID为 %s 的玩家的QQ是：%s");
            messages.put("CanNotFoundPlayerIdByUUID", "找不到UUID为 %s 的玩家ID");
            messages.put("FoundIDForUUID", "UUID为 %s 的玩家ID是：%s");
        } else if ("zh_hant".equalsIgnoreCase(language)) {
            // 繁体中文消息
            messages.put("TranslationContributors", "當前语言: 繁體中文 (貢獻者: Zhang1233 & TongYi-Lingma LLM)");
            messages.put("CanNotFoundMeowLibs", "未找到 MeowLibs, 請安裝前置依賴 MeowLibs!");
            messages.put("startup", "MeowUID 已加载!");
            messages.put("shutdown", "MeowUID 已卸载!");
            messages.put("nowusingversion", "目前使用版本:");
            messages.put("checkingupdate", "正在檢查更新...");
            messages.put("checkfailed", "檢查更新失敗，請檢查你的網絡狀態!");
            messages.put("updateavailable", "發現新版本:");
            messages.put("updatemessage", "更新內容如下:");
            messages.put("updateurl", "新版本下載網址:");
            messages.put("oldversionmaycauseproblem", "舊版本可能會導致問題，請盡快更新!");
            messages.put("nowusinglatestversion", "您正在使用最新版本!");
            messages.put("reloaded", "配置文件已重载!");
            messages.put("nopermission", "你没有权限执行此命令!");
            messages.put("usage", "用法:");
            messages.put("CanNotConnectDatabase", "無法連接到數據庫: %s");
            messages.put("PluginDisabled", "插件目前未啟用，請檢查你的配置文件!");
            messages.put("InvalidUid", "無效的 UID 格式!");
            messages.put("FoudedUIDforPlayer", "玩家 %s 的 UID 是 %d");
            messages.put("CanNotFoundPlayerUidById", "找不到以 %s 為名的玩家的 UID");
            messages.put("playeridFouded", "對應的玩家 ID 是:");
            messages.put("CanNotFoundPlayerIdByUID", "找不到 UID %d 對應的玩家");
            messages.put("CanNotFoundPAPI", "未找到 PlaceholderAPI, 無法使用變量查詢玩家 UID !");
            messages.put("RegistUID", "已為玩家 %s 註冊UID %d");
            messages.put("finding", "正在查询...");
            messages.put("query_time", "(耗時: %dms)");
            messages.put("InvalidUUIDFormat", "無效的 UUID 格式: %s");
            messages.put("FoudedUIDforUUID", "UUID 為 %s 的玩家的 UID 是 %d");
            messages.put("CanNotFoundPlayerUidByUUID", "找不到此 UUID 的玩家對應的 UID: %s");
            messages.put("DatabaseError", "數據庫錯誤！");
            messages.put("QQNotFound", "找不到UID為 %d 的玩家的QQ信息");
            messages.put("QQFound", "UID為 %d 的玩家的QQ是：%s");
            messages.put("QQNotFoundForPlayer", "找不到玩家 %s 的QQ信息");
            messages.put("QQFoundForPlayer", "玩家 %s 的QQ是：%s");
            messages.put("QQNotFoundForUUID", "找不到UUID為 %s 的玩家的QQ信息");
            messages.put("QQFoundForUUID", "UUID為 %s 的玩家的QQ是：%s");
            messages.put("CanNotFoundPlayerIdByUUID", "找不到UUID為 %s 的玩家ID");
            messages.put("FoundIDForUUID", "UUID為 %s 的玩家ID是：%s");
        } else if ("en_us".equalsIgnoreCase(language)) {
            // English messages
            messages.put("TranslationContributors", "Current language: English (Contributors: Zhang1233)");
            messages.put("CanNotFoundMeowLibs", "MeowLibs not found, please install the preceding dependency MeowLibs!");
            messages.put("startup", "MeowUID has been loaded!");
            messages.put("shutdown", "MeowUID has been unloaded!");
            messages.put("nowusingversion", "Currently using version:");
            messages.put("checkingupdate", "Checking for updates...");
            messages.put("checkfailed", "Update check failed, please check your network status!");
            messages.put("updateavailable", "New version available:");
            messages.put("updatemessage", "Update content:");
            messages.put("updateurl", "Download URL for new version:");
            messages.put("oldversionmaycauseproblem", "Old versions may cause problems, please update as soon as possible!");
            messages.put("nowusinglatestversion", "You are currently using the latest version!");
            messages.put("reloaded", "Config file has been reloaded!");
            messages.put("nopermission", "You do not have permission to execute this command!");
            messages.put("usage", "Usage:");
            messages.put("CanNotConnectDatabase", "Failed to connect to the database: %s");
            messages.put("PluginDisabled", "The plugin is currently disabled, please check your configuration file!");
            messages.put("InvalidUid", "Invalid UID format!");
            messages.put("FoudedUIDforPlayer", "Player %s has been registered with UID %d");
            messages.put("CanNotFoundPlayerUidById", "Unable to find UID for player named %s");
            messages.put("playeridFouded", "The ID of the user is:");
            messages.put("CanNotFoundPlayerIdByUID", "Unable to find player with UID %d");
            messages.put("CanNotFoundPAPI", "Could not find PlaceholderAPI, unable to use variables to query player UID!");
            messages.put("RegistUID", "Registered UID %d for player %s");
            messages.put("finding", "Finding...");
            messages.put("query_time", "(Time taken: %dms)");
            messages.put("InvalidUUIDFormat", "Invalid UUID format: %s");
            messages.put("FoudedUIDforUUID", "The UID of the player with UUID %s is %d");
            messages.put("CanNotFoundPlayerUidByUUID", "Unable to find a UID for the player with this UUID: %s");
            messages.put("DatabaseError", "Database error occurred!");
            messages.put("QQNotFound", "Unable to find QQ for player with UID %d");
            messages.put("QQFound", "QQ for player with UID %d is: %s");
            messages.put("QQNotFoundForPlayer", "Unable to find QQ for player %s");
            messages.put("QQFoundForPlayer", "QQ for player %s is: %s");
            messages.put("QQNotFoundForUUID", "Unable to find QQ for player with UUID %s");
            messages.put("QQFoundForUUID", "QQ for player with UUID %s is: %s");
            messages.put("CanNotFoundPlayerIdByUUID", "Unable to find player ID for UUID %s");
            messages.put("FoundIDForUUID", "Player ID for UUID %s is: %s");
        } else if ("ja_jp".equalsIgnoreCase(language)) {
            // 日本語メッセージ
            messages.put("TranslationContributors", "現在の言語: 日本語 (貢献者: Zhang1233 & TongYi-Lingma LLM)");
            messages.put("CanNotFoundMeowLibs", "MeowLibsが見つかりません。プレフィックス依存をインストールしてください!");
            messages.put("startup", "MeowUIDが読み込まれました!");
            messages.put("shutdown", "MeowUIDがアンロードされました!");
            messages.put("nowusingversion", "現在使用中のバージョン:");
            messages.put("checkingupdate", "アップデートをチェック中...");
            messages.put("checkfailed", "アップデートチェックに失敗しました。ネットワークの状態を確認してください!");
            messages.put("updateavailable", "新しいバージョンが利用可能です:");
            messages.put("updatemessage", "アップデート内容:");
            messages.put("updateurl", "新しいバージョンのダウンロードURL:");
            messages.put("oldversionmaycauseproblem", "古いバージョンは問題を引き起こす可能性があります。できるだけ早くアップデートしてください!");
            messages.put("nowusinglatestversion", "現在最新バージョンを使用しています!");
            messages.put("reloaded", "設定ファイルがリロードされました!");
            messages.put("nopermission", "このコマンドの実行に権限がありません!");
            messages.put("usage", "使用法:");
            messages.put("CanNotConnectDatabase", "データベースに接続できません: %s");
            messages.put("PluginDisabled", "プラグインは現在無効化されています。設定ファイルを確認してください!");
            messages.put("InvalidUid", "無効なUIDフォーマットです!");
            messages.put("FoudedUIDforPlayer", "プレイヤー%sはUID%dで登録されています");
            messages.put("CanNotFoundPlayerUidById", "%s というプレイヤーの UID が見つかりません");
            messages.put("playeridFouded", "ユーザーのIDは:");
            messages.put("CanNotFoundPlayerIdByUID", "UID %d のプレイヤーが見つかりません");
            messages.put("CanNotFoundPAPI", "PlaceholderAPIが見つかりません。プレイヤーUIDをクエリするには使用できません!");
            messages.put("RegistUID", "プレイヤー%sにUID%dを登録しました");
            messages.put("finding", "検索中...");
            messages.put("query_time", "(処理時間: %dms)");
            messages.put("InvalidUUIDFormat", "無効なUUIDフォーマット: %s");
            messages.put("FoudedUIDforUUID", "UUID%sのプレイヤーのUIDは%dです");
            messages.put("CanNotFoundPlayerUidByUUID", "このUUIDのプレイヤーのUIDが見つかりません: %s");
            messages.put("DatabaseError", "データベースエラーが発生しました！");
            messages.put("QQNotFound", "UID %d のプレイヤーのQQ情報が見つかりません");
            messages.put("QQFound", "UID %d のプレイヤーのQQは：%s です");
            messages.put("QQNotFoundForPlayer", "プレイヤー %s のQQ情報が見つかりません");
            messages.put("QQFoundForPlayer", "プレイヤー %s のQQは：%s です");
            messages.put("QQNotFoundForUUID", "UUID %s のプレイヤーのQQ情報が見つかりません");
            messages.put("QQFoundForUUID", "UUID %s のプレイヤーのQQは：%s です");
            messages.put("CanNotFoundPlayerIdByUUID", "UUID %s のプレイヤーIDが見つかりません");
            messages.put("FoundIDForUUID", "UUID %s のプレイヤーIDは：%s です");
        }
    }

    /**
     * 获取语言消息
     * @param key 消息键名
     * @return 对应的语言消息，如果不存在则返回键名
     */
    public String getMessage(String key) {
        return messages.getOrDefault(key, key);
    }
}
