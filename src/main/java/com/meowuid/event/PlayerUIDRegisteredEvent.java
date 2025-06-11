package com.meowuid.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

public class PlayerUIDRegisteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final long uid;
    private final boolean isNewRegistration;

    public PlayerUIDRegisteredEvent(Player player, long uid, boolean isNewRegistration) {
        this.player = player;
        this.uid = uid;
        this.isNewRegistration = isNewRegistration;
    }

    /**
     * 获取注册的玩家
     * @return 玩家对象
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 获取玩家的UID
     * @return UID
     */
    public long getUID() {
        return uid;
    }

    /**
     * 是否是新的注册（true表示新玩家，false表示已存在的玩家）
     * @return 是否新注册
     */
    public boolean isNewRegistration() {
        return isNewRegistration;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
} 