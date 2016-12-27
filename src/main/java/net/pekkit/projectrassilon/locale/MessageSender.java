/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Doctor Squawk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.pekkit.projectrassilon.locale;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 *
 * @author Squawkers13
 */
public class MessageSender {

    /**
     * Plugin method to easily send messages to players (and console too). This
     * is the core of all the sender methods included here.
     *
     * @param sender The CommandSender to send the message to.
     * @param msg The message to be sent.
     * @param pagination Whether to paginate the message or not.
     *
     * @since 1.2.2
     */
    public static void sendMsg(CommandSender sender, String msg, boolean pagination) {
        String message = translateAlternateColorCodes('&', msg);
        if (message.length() > ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH && pagination) {
            String[] multiline = ChatPaginator.wordWrap(message, ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH);
            sender.sendMessage(multiline);
        } else {
            sender.sendMessage(message);
        }
    }

    /**
     * Plugin method to easily send messages to players (and console too). This
     * version assumes you want to paginate the message.
     *
     * @param sender The CommandSender to send the message to.
     * @param msg The message to be sent.
     *
     * @since 1.2.2
     */
    public static void sendMsg(CommandSender sender, String msg) {
        sendMsg(sender, msg, true);
    }

    /**
     * Plugin method to easily send messages prefixed with a short prefix.
     * Prefixes the message with [PR] and calls sendMsg.
     * This is recommended than sendPluginMsg for in-game-chat!
     *
     * @param sender The CommandSender to send the message to.
     * @param msg The message to be sent.
     *
     * @see #sendMsg(CommandSender, String)
     *
     * @since 2.0
     */
    public static void sendPrefixMsg(CommandSender sender, String msg) {
        sendMsg(sender, "&c[&6PR&c] " + msg, true);
    }

    /**
     * Plugin method to easily send messages prefixed with the plugin name.
     * Prefixes the message with the plugin name and calls sendMsg.
     *
     * @param sender The CommandSender to send the message to.
     * @param msg The message to be sent.
     * @param pagination Whether to paginate the message or not.
     *
     * @see #sendMsg(CommandSender, String)
     *
     * @since 1.2
     */
    public static void sendPluginMsg(CommandSender sender, String msg, boolean pagination) {
        sendMsg(sender, "&c[&6ProjectRassilon&c] &r" + msg, pagination);
    }

    /**
     * Plugin method to easily log messages to the console. Actually just calls
     * sendPluginMsg and specifies the sender as the server's
     * ConsoleCommandSender.
     *
     * @param msg The message to be sent to the console.
     *
     * @see #sendPluginMsg(CommandSender, String, boolean)
     *
     * @since 1.2
     */
    public static void log(String msg) {
        sendPluginMsg(Bukkit.getServer().getConsoleSender(), msg, true);
    }

    /**
     * Plugin method to easily log caught exceptions to the console. Actually
     * uses a for loop to log for each line of the pieced-together stacktrace.
     *
     * @param ex The Exception to log a stacktrace for.
     *
     * @see #log(String)
     *
     * @since 1.2
     */
    public static void logStackTrace(Exception ex) {
        log("&4An error occurred: &f" + ex.getLocalizedMessage());
        log(ex.getClass().getName());
        for (StackTraceElement ee : ex.getStackTrace()) {
            log("   at " + ee.getClassName() + ":" + ee.getMethodName() + " (line " + ee.getLineNumber() + ")");
        }
        log("&cPlease report this to the author so he can try to fix the problem!");
    }

}
