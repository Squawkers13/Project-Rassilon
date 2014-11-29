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
package net.pekkit.projectrassilon.api;

import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.RegenManager;
import net.pekkit.projectrassilon.data.RDataHandler;
import net.pekkit.projectrassilon.locale.MessageSender;

/**
 * This is the API other plugins can use to hook into Project Rassilon.
 *
 * @author Squawkers13
 */
public class RassilonAPI {

    private final ProjectRassilon plugin;
    private final RDataHandler rdh;
    private final RegenManager rm;

    private final TimelordDataManager tdm;
    private final Regenerator tr;

    /**
     * Creates an instance of the API.
     *
     * @param par1 Instance of the main plugin class.
     * @param par2 Instance of the plugin's data handler.
     */
    public RassilonAPI(ProjectRassilon par1, RDataHandler par2, RegenManager rm) {
        plugin = par1;
        rdh = par2;
        this.rm = rm;

        tdm = new TimelordDataManager(rdh);
        tr = new Regenerator(plugin, rdh, rm);
    }

    /**
     *
     * @return
     */
    public TimelordDataManager getTimelordDataManager() {
        return tdm;
    }

    /**
     *
     * @return
     */
    public Regenerator getRegenerator() {
        return tr;
    }

}
