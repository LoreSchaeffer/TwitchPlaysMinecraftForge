package it.multicoredev.forge.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.StringTextComponent;

/**
 * Copyright © 2020 by Lorenzo Magni
 * This file is part of TwitchPlaysMinecraftForge.
 * TwitchPlaysMinecraftForge is under "The 3-Clause BSD License", you can find a copy <a href="https://opensource.org/licenses/BSD-3-Clause">here</a>.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
public class Util {
    public static StringTextComponent translateString(String str) {
        StringTextComponent stc = new StringTextComponent("");

        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {

            if ((chars[i] == '&') && ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(chars[(i + 1)]) > -1)) {
                stc.appendText(getChatFormatting(chars[i + 1]) + "");
                i++;
                continue;
            }

            stc.appendText(chars[i] + "");
        }

        return stc;
    }

    private static ChatFormatting getChatFormatting(char code) {
        code = Character.toLowerCase(code);

        for (ChatFormatting cf : ChatFormatting.values()) {
            if (cf.func_225041_a() == code) return cf;
        }

        return null;
    }
}
