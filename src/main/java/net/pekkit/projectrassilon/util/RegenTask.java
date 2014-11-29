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

/**
 * Tasks used during regeneration.
 * @author Squawkers13
 */
public enum RegenTask {
  
    /**
     *
     */
    PRE_REGEN_EFFECTS("pre_regen_effects"),

    /**
     *
     */
    REGEN_EFFECTS("regen_effects"),

    /**
     *
     */
    POST_REGEN_EFFECTS("post_regen_effects"),

    /**
     *
     */
    REGEN_END("regen_end"), 
    
    REGEN_DELAY("regen_delay"),
    
    POST_REGEN_DELAY("post_regen_delay");
    
    private final String columnName;
    
    RegenTask(String par1) {
        columnName = par1;
    }
    
    /**
     *
     * @return
     */
    public String getColumnName() {
        return columnName;
    }
    
}
