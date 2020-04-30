package it.multicoredev.forge.twitch;

import it.multicoredev.forge.util.Util;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class MessageController {
    private static Map<String, KeyBinding> commands = new HashMap<>();
    public static long pressDuration = 400;
    public static boolean twitchPlays;

    static {
        GameSettings settings = Minecraft.getInstance().gameSettings;
        commands.put("W", settings.keyBindForward);
        commands.put("A", settings.keyBindLeft);
        commands.put("S", settings.keyBindBack);
        commands.put("D", settings.keyBindRight);
        commands.put("J", settings.keyBindJump);
        commands.put("L", settings.keyBindAttack);
        commands.put("R", settings.keyBindUseItem);
    }

    public static void process(String user, String msg) {
        boolean isCommand = twitchPlays && msg.length() == 1 && commands.containsKey(msg);

        if (isCommand) new Thread(() -> {
            InputMappings.Input key = commands.get(msg).getKey();
            KeyBinding.setKeyBindState(key, true);

            try {
                if (msg.equalsIgnoreCase("l") || msg.equalsIgnoreCase("r")) Thread.sleep(200);
                else Thread.sleep(pressDuration);
            } catch (InterruptedException ignored) {
            }
            KeyBinding.setKeyBindState(key, false);
        }).start();

        Minecraft.getInstance().player.sendMessage(Util.translateString(String.format(isCommand ? "<&e%s&r> &6&l%s&r" : "<&5%s&r> %s&r", user, msg)));
    }





    /*public static Map<String, Long> flags = new ConcurrentHashMap<>();
    public static long timestamp = System.currentTimeMillis();
    public static long pressDuration = 400L;
    public static boolean twitchPlays;

    static {
        flags.put("A", 0L);
        flags.put("D", 0L);
        flags.put("W", 0L);
        flags.put("S", 0L);
        flags.put("J", 0L);
    }

    public static void process(String user, String msg) {
        boolean isCommand = twitchPlays && msg.length() == 1 && flags.containsKey(msg);

        if (isCommand) flags.replace(msg, timestamp);
        if (isCommand) KeyBinding.setKeyBindState(Minecraft.getInstance().gameSettings);

        try {
            Minecraft.getInstance().player.sendMessage(Util.translateString(String.format(isCommand ? "<&e%s&r> &6&l%s&r" : "<&5%s&r> %s&r", user, msg)));
        } catch (NullPointerException ignored) {
        }
    }



    public static boolean shouldPress(String s) {
        return timestamp - flags.get(s) < pressDuration;
    }


    public static void update() {
        timestamp = System.currentTimeMillis();
    }*/
}
