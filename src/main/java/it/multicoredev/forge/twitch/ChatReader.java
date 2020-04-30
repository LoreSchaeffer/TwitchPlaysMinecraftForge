package it.multicoredev.forge.twitch;

import it.multicoredev.forge.util.Util;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class ChatReader {
    private static final Pattern USER_PATT = Pattern.compile("(?<=^:)\\w+(?=!)");
    private static final String MSG_PATT = "^:\\w+!\\w+@\\w+\\.tmi\\.twitch\\.tv PRIVMSG #\\w+ :";
    private static final Logger LOGGER = LogManager.getLogger();

    public static String channel;
    public static Thread chatReader;

    private static Socket socket;
    private static BufferedReader in;
    private static DataOutputStream out;

    public static void start(String c) {
        chatReader = null;
        channel = c;
        chatReader = new Thread(ChatReader::connect);
        chatReader.start();
    }

    public static void connect() {
        try {
            Minecraft.getInstance().player.sendMessage(Util.translateString(String.format("[&dTPMC&r]&7 Listening to &6%s's&7 chat...&r", channel)));
            socket = new Socket("irc.chat.twitch.tv", 6667);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new DataOutputStream(socket.getOutputStream());

            out.write("NICK justinfan421\n\r".getBytes(StandardCharsets.UTF_8));
            out.write(String.format("JOIN #%s\n\r", channel).getBytes(StandardCharsets.UTF_8));

            String response;
            while ((response = in.readLine()) != null) {
                process_response(response);
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (channel != null)
                Minecraft.getInstance().player.sendMessage(Util.translateString(String.format("[&dTPMC&r]&7 Disconnected from &6%s&r", channel)));
            stop();
            channel = null;
        }
    }

    public static void stop() {
        try {
            socket.close();
            chatReader = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void process_response(String res) throws IOException {
        if (res.startsWith("PING :tmi.twitch.tv")) {
            out.write("PONG :tmi.twitch.tv\n\r".getBytes(StandardCharsets.UTF_8));
        } else if (!(res.startsWith(":justinfan421") || res.startsWith(":tmi.twitch.tv "))) {
            Matcher m = USER_PATT.matcher(res);
            if (m.find()) {
                String user = m.group(0);
                String msg = res.replaceFirst(MSG_PATT, "");
                LOGGER.info(String.format("<%s> %s", user, msg));
                if (msg.length() == 1) msg = msg.toUpperCase();
                MessageController.process(user, msg);
            } else {
                LOGGER.warn("----ERROR----\n" + res + "\n----");
            }
        }
    }
}
