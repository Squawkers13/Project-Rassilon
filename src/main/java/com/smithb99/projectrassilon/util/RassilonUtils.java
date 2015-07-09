package com.smithb99.projectrassilon.util;

import com.smithb99.projectrassilon.ProjectRassilon;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.network.PlayerConnection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RassilonUtils {
    public static final SimplifiedVersion getCurrentVersion(ProjectRassilon plugin) {
        Version server = getServerVersion(plugin.getServer().getVersion());

        if (server.compareTo(SimplifiedVersion.BOUNTIFUL.getImpliedVersion()) > -1) {
            return SimplifiedVersion.BOUNTIFUL;
        }

        if (server.compareTo(SimplifiedVersion.MINIMUM.getImpliedVersion()) > -1) {
            return SimplifiedVersion.MINIMUM;
        }

        return SimplifiedVersion.PRE_UUID;
    }

    public static final Version getServerVersion(String s) {
        Pattern pattern = Pattern.compile("\\((.+?)\\)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        String v;

        if (matcher.find()) {
            String[] split = matcher.group(1).split(" ");
            v = split[1];
        } else {
            v = "1.7.2";
        }

        return new Version(v);
    }

    public enum SimplifiedVersion {
        PRE_UUID(new Version("1.7.2"), 0),
        MINIMUM(new Version("1.7.9"), 0),
        BOUNTIFUL(new Version("1.8.6"), 0);

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
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PlayerConnection connection = craftPlayer.getHandle().playerConnection;
        IChatBaseComponent titleJSON = ChatSerializer.a(buildJSON(title));
        IChatBaseComponent subtitleJSON = ChatSerializer.a(buildJSON(subtitle));

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);

        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
    }

    public static void sendActionBar(Player p, String message) {
        IChatBaseComponent cbc = ChatSerializer.a(buildJSON(message));
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
    }

    private static String buildJSON(String message) {
        return "{text:\"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}";
    }
}
