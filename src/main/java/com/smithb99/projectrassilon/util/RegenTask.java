package com.smithb99.projectrassilon.util;

public enum RegenTask {
    PRE_REGEN_EFFECTS("pre_regen_effects"),
    REGEN_EFFECTS("regen_effects"),
    POST_REGEN_EFFECTS("post_regen_effects"),
    REGEN_END("regen_end"),
    REGEN_DELAY("regen_delay"),
    POST_REGEN_DELAY("post_regen_delay");

    private final String columnName;

    RegenTask(String par1) {
        columnName = par1;
    }

    public String getColumnName() {
        return columnName;
    }
}
