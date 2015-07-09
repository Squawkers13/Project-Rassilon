package com.smithb99.projectrassilon.api;

import com.smithb99.projectrassilon.ProjectRassilon;
import com.smithb99.projectrassilon.RegenManager;
import com.smithb99.projectrassilon.data.RDataHandler;

public class RassilonAPI {
    private final ProjectRassilon plugin;
    private final RDataHandler rdh;
    private final RegenManager rm;

    private final TimelordDataManager tdm;
    private final Regenerator tr;

    public RassilonAPI(ProjectRassilon par1, RDataHandler par2, RegenManager rm) {
        plugin = par1;
        rdh = par2;
        this.rm = rm;

        tdm = new TimelordDataManager(rdh);
        tr = new Regenerator(plugin, rdh, rm);
    }

    public TimelordDataManager getTimelordDataManager() {
        return tdm;
    }

    public Regenerator getRegenerator() {
        return tr;
    }
}
