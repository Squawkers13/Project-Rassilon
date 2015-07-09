package com.smithb99.projectrassilon.locale;

import javafx.scene.control.Pagination;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandSource;
//import static org.spongepowered.api.

public class MessageSender {
    public static void sendMsg(CommandSource source, String message, boolean pagination) {
        String msg = translateAlternateColorCodes('&', message);

        if (msg.length() > ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH && pagination) {
            String[] multiline = ChatPaginator.wordWrap(msg, ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH);
            source.sendMessage(Texts.of(multiline));
        } else {
            source.sendMessage(Texts.of(msg));
        }
    }

    public static void sendMsg(CommandSource source, String message) {
        sendMsg(source, message, true);
    }

    public static void sendPluginMsg(CommandSource source, String message, boolean pagination) {
        sendMsg(source, "[ProjectRassilon] " + message, pagination);
    }

    public static void log(String message) {
        sendPluginMsg(Bukkit.getServer().getConsoleSender(), message, false);
    }

    public static void logStackTrace(Exception e) {
        log("An error occurred:  " + e.getLocalizedMessage());
        log(e.getClass().getName());

        for (StackTraceElement element : e.getStackTrace()) {
            log("    at " + element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber());
        }

        log("Please report this error to the plugin author.");
    }
}
