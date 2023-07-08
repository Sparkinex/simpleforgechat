package com.sparkinex.simpleforgechat;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = SimpleForgeChat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<String> CHAT_FORMAT = BUILDER
            .comment("The chat format you would like (Supported placeholders: {prefix}, {username}, {suffix}, {message})")
            .define("chatFormat", "{prefix} {username} {suffix}&f: {message}");

    protected static final ForgeConfigSpec SPEC = BUILDER.build();

    public static String chatFormat;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        chatFormat = CHAT_FORMAT.get();
    }
}
