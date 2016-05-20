/*
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Squawkers13 <Squawkers13@pekkit.net>
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
package net.pekkit.projectrassilon.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_9_R2.PacketPlayOutChat;
import net.minecraft.server.v1_9_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_9_R2.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_9_R2.PlayerConnection;
import net.pekkit.projectrassilon.ProjectRassilon;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Squawkers13
 */
public class RassilonUtils {

    public static final SimplifiedVersion getCurrentVersion(ProjectRassilon plugin) {
        Version server = getServerVersion(plugin.getServer().getVersion());

        if (server.compareTo(SimplifiedVersion.BOUNTIFUL.getImpliedVersion()) > -1) {
            return SimplifiedVersion.BOUNTIFUL;
        } else if (server.compareTo(SimplifiedVersion.MINIMUM.getImpliedVersion()) > -1) {
            return SimplifiedVersion.MINIMUM;
        } else {
            return SimplifiedVersion.PRE_UUID;
        }
    }

    /**
     *
     * @param s
     * @return
     */
    public static final Version getServerVersion(String s) {
        Pattern pat = Pattern.compile("\\((.+?)\\)", Pattern.DOTALL);
        Matcher mat = pat.matcher(s);
        String v;
        if (mat.find()) {
            String[] split = mat.group(1).split(" ");
            v = split[1];
        } else {
            v = "1.7.2"; //Why is this 1.7.2? Probably to catch Cauldron servers
        }
        return new Version(v);
    }

    public enum SimplifiedVersion {

        PRE_UUID(new Version("1.7.2"), 0),
        MINIMUM(new Version("1.7.9"), 1),
        BOUNTIFUL(new Version("1.8.6"), 2);

        private final Version implied;
        private final int index;

        SimplifiedVersion(Version par1, int par2) {
            implied = par1;
            index = par2;
        }

        public Version getImpliedVersion() {
            return implied;
        }

        public int getIndex() {
            return index;
        }
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        IChatBaseComponent titleJSON = ChatSerializer.a(buildJSON(title));
        IChatBaseComponent subtitleJSON = ChatSerializer.a(buildJSON(subtitle));
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
    }

    public static void sendActionBar(Player p, String msg) {
        IChatBaseComponent cbc = ChatSerializer.a(buildJSON(msg));
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
    }

    private static String buildJSON(String msg) {
        return "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', msg) + "\"}";
    }
}
