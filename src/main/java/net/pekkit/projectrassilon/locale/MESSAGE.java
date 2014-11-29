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

package net.pekkit.projectrassilon.locale;

/** Messages.
 * More will be added in 1.3.
 *
 * @author Squawkers13
 */
public enum MESSAGE {

    /**
     *
     */
    CORRECT_SYNTAX("&4The correct syntax is:"),

    /**
     *
     */
    INVALID_ARGS("&4Invalid arguments!"),

    /**
     *
     */
    INVALID_SUB_CMD("&cThat is not a valid sub-command!"),

    /**
     *
     */
    MUST_BE_PLAYER("&cYou must be a player to run this command!"),

    /**
     *
     */
    NO_PERMS("&4You do not have permission to do that!"),

    /**
     *
     */
    PLAYER_NEVER_JOINED("&4That player has never joined this server!"),

    /**
     *
     */
    PLAYER_NOT_ONLINE("&4That player is not online!"),

    /**
     *
     */
    TOGGLE_WARNING("&4If no value is specified, the command will toggle the current value."),

    /**
     *
     */
    TOO_FEW_ARGS("&cToo few command arguments!");

    private String msg;

    private MESSAGE(String m) {
        msg = m;
    }

    @Override
    public String toString() {
        return msg;
    }

    /**
     *
     * @param m
     */
    protected void setMessage(String m) {
        msg = m;
    }
}
