package com.sparkinex.simpleforgechat;

import com.mojang.logging.LogUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(SimpleForgeChat.MODID)
public class SimpleForgeChat {
    public static final String MODID = "simpleforgechat";
    private static final Logger LOGGER = LogUtils.getLogger();

    private LuckPerms lp = null;

    public SimpleForgeChat() {
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent e) {
        lp = LuckPermsProvider.get();
        if (lp == null) {
            throw new NullPointerException("LuckPerms must be present!");
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent e) {
        if (lp == null
                || e == null
                || e.getPlayer() == null
                || e.getMessage() == null
                || e.getMessage().getString().length() == 0
        ) return;

        ServerPlayer player = e.getPlayer();

        CachedMetaData meta = getLuckPermsUserMetaData(player.getUUID());
        if (meta == null) return;
        String metaPrefix = meta.getPrefix();
        String metaSuffix = meta.getSuffix();

        Map<String, String> messageParts = new HashMap<>();
        messageParts.put("{prefix}", metaPrefix == null ? null : metaPrefix.trim());
        messageParts.put("{username}", player.getDisplayName().getString());
        messageParts.put("{suffix}", metaSuffix == null ? null : metaSuffix.trim());
        messageParts.put("{message}", e.getMessage().getString());

        String newMessage = Toolkit.createFormattedMessage(Config.chatFormat, messageParts);

        Component fullMessage = Component.literal(Toolkit.translateColorCodes(newMessage));
        e.setCanceled(true);

        player.server.execute(() -> {
            Toolkit.broadcastMessage(player.level.getServer(), fullMessage);
        });
    }

    private @Nullable CachedMetaData getLuckPermsUserMetaData(UUID playerId) {
        try {
            User usr = lp.getUserManager().getUser(playerId);
            return usr != null ? usr.getCachedData().getMetaData() : null;
        } catch(IllegalStateException e) {
            return null;
        }
    }
}
