/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Doctor Squawk <Squawkers13@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.pekkit.projectrassilon.nms;


import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Reflection is Evil: Part 1 (for specific old version)
 */
public class HelperV1_8_R1 extends INMSHelper {

    public HelperV1_8_R1() {
        super("v1_8_R1");
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            Method serializeChat = getNMSClass("ChatSerializer").getDeclaredMethod("a", String.class); //TODO This line changes?

            Object titleJSON = serializeChat.invoke(null, buildJSON(title)); //IChatBaseComponent
            Object subtitleJSON = serializeChat.invoke(null, buildJSON(subtitle)); //IChatBaseComponent

            Class enumClass = getNMSClass("EnumTitleAction"); //TODO This line changes?
            Constructor<?> packetConstructorLong = getNMSClass("PacketPlayOutTitle").
                    getConstructor(enumClass, getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Constructor<?> packetConstructor = getNMSClass("PacketPlayOutTitle").
                    getConstructor(enumClass, getNMSClass("IChatBaseComponent"));

            Object titlePacket = packetConstructorLong.newInstance(Enum.valueOf(enumClass, "TITLE"), titleJSON, fadeIn, stay, fadeOut); //PacketPlayOutTitle
            Object subtitlePacket = packetConstructor.newInstance(Enum.valueOf(enumClass, "SUBTITLE"), subtitleJSON); //PacketPlayOutTitle

            Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
            sendPacket.invoke(getConnection(player), titlePacket);
            sendPacket.invoke(getConnection(player), subtitlePacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendActionBar(Player player, String msg) {
        try {
            Method serializeChat = getNMSClass("ChatSerializer").getDeclaredMethod("a", String.class); //TODO This line changes?

            Object messageJSON = serializeChat.invoke(null, buildJSON(msg)); //IChatBaseComponent

            Constructor<?> packetConstructor = getNMSClass("PacketPlayOutChat").
                    getConstructor(getNMSClass("IChatBaseComponent"), byte.class);

            Object chatPacket = packetConstructor.newInstance(messageJSON, (byte) 2);

            Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
            sendPacket.invoke(getConnection(player), chatPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

