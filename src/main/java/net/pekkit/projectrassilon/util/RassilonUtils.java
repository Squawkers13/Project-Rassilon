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
package net.pekkit.projectrassilon.util;

import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.locale.MessageSender;
import net.pekkit.projectrassilon.nms.BountifulHelper;
import net.pekkit.projectrassilon.nms.HelperV1_8_R1;
import net.pekkit.projectrassilon.nms.InterfaceNMSHelper;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Squawkers13
 */
public class RassilonUtils {

    private static InterfaceNMSHelper nms;

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
            v = "1.7.2"; //This catches Cauldron servers - thanks eccentric_nz
        }
        return new Version(v);
    }

    public enum SimplifiedVersion {

        PRE_UUID(new Version("1.7.2"), 0),
        MINIMUM(new Version("1.7.9"), 1),
        BOUNTIFUL(new Version("1.8"), 2);

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


    public static InterfaceNMSHelper getNMSHelper() {
        if (nms == null) {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];

            MessageSender.log("Instantiating NMS helper for server version " + version);

            if(version == "v1_8_R1") {
                nms = new HelperV1_8_R1();
            } else {
                nms = new BountifulHelper(version); //The code stays stable for now
            }
        }
        return nms;
    }

    public enum ConfigurationFile {
        CORE("core.yml"),
        REGEN("regen.yml");

        private String fileName;

        ConfigurationFile(String par1) {
            fileName = par1;
        }

        public String getFileName() {
            return fileName;
        }
    }

    /**
     * Used to copy configuration files to the plugin folder.
     * @since 2.0
     */
    public static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
