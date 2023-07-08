package com.sparkinex.simpleforgechat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Toolkit {

    public static void broadcastMessage(MinecraftServer server, Component message) {
        if (server == null) {
            return;
        }

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            p.sendSystemMessage(message);
        }
    }

    public static String translateColorCodes(String text) {
        Pattern hexPattern = Pattern.compile("&#[a-fA-F0-9]{6}");

        Matcher hexMatcher = hexPattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (hexMatcher.find()) {
            String hexCode = hexMatcher.group();
            int rgb = Integer.parseInt(hexCode.substring(2), 16);
            String forgeCode = TextColor.fromRgb(rgb).toString();
            hexMatcher.appendReplacement(sb, forgeCode);
        }
        hexMatcher.appendTail(sb);

        return sb.toString().replace("&", "\u00A7");
    }

    public static @Nullable String createFormattedMessage(String format, Map<String, String> messageParts) {
        String newMessage = format;
        for (Map.Entry<String, String> entry : messageParts.entrySet()) {
            String placeholder = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.length() == 0) {
                newMessage = newMessage
                        .replace(" " + placeholder, "")
                        .replace(placeholder + " ", "")
                        .replace(placeholder, "");
            } else {
                newMessage = newMessage.replace(placeholder, value);
            }
        }

        return newMessage;
    }
}
