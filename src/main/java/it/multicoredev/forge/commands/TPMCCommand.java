package it.multicoredev.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.multicoredev.forge.twitch.ChatReader;
import it.multicoredev.forge.twitch.MessageController;
import it.multicoredev.forge.util.Util;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

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
public class TPMCCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("tpmc")
                        .executes(context -> f0(context.getSource()))
                        .then(Commands.argument("channel", StringArgumentType.string())
                                .executes(context -> f1(context.getSource(), StringArgumentType.getString(context, "channel"))))
                        .then(Commands.literal("play")
                                .executes(context -> f2(context.getSource())))
                        .then(Commands.literal("stop")
                                .executes(context -> f3(context.getSource())))
                        .then(Commands.literal("disconnect")
                                .executes(context -> f4(context.getSource())))
                        .then(Commands.literal("pressDuration")
                                .executes(context -> f5(context.getSource(), -1L))
                                .then(Commands.argument("milliseconds", LongArgumentType.longArg(0))
                                        .executes(context -> f5(context.getSource(), LongArgumentType.getLong(context, "milliseconds"))))));
    }

    /* tpmc */
    private static int f0(CommandSource src) throws CommandSyntaxException {
        src.asPlayer().sendMessage(Util.translateString(
                String.format("[&dTPMC&r] &7channel: %s, play: %s, pressDuration: %d&r",
                        ChatReader.channel,
                        MessageController.twitchPlays ? "true" : "false",
                        MessageController.pressDuration
                )));
        return 1;
    }

    /* tpmc <twitch channel> */
    private static int f1(CommandSource src, String channel) {
        ChatReader.start(channel.toLowerCase());
        return 1;
    }

    /* tpmc play */
    private static int f2(CommandSource src) throws CommandSyntaxException {
        if (ChatReader.channel == null)
            src.asPlayer().sendMessage(Util.translateString("[&dTPMC&r] &cConnect to a channel using &4/tpmc <twitch channel> &r"));
        else {
            src.asPlayer().sendMessage(Util.translateString("[&dTPMC&r] &l&dTwitch Plays MineCraft!&r"));
            MessageController.twitchPlays = true;
        }
        return 1;
    }

    /* tpmc stop */
    private static int f3(CommandSource src) throws CommandSyntaxException {
        if (ChatReader.channel == null)
            src.asPlayer().sendMessage(Util.translateString("[&dTPMC&r] &cConnect to a channel using &4/tpmc <twitch channel> &r"));
        else if (!MessageController.twitchPlays)
            src.asPlayer().sendMessage(Util.translateString("[&dTPMC&r] &cLet the chat move your player using &4/tpmc play&r"));
        else {
            MessageController.twitchPlays = false;
            src.asPlayer().sendMessage(Util.translateString("[&dTPMC&r] &dTwitch Plays MineCraft&7 disabled&r"));
        }
        return 1;
    }

    /* tpmc disconnect */
    private static int f4(CommandSource src) throws CommandSyntaxException {
        if (ChatReader.channel == null) {
            src.asPlayer().sendMessage(Util.translateString("[&dTPMC&r] &cConnect to a channel using &4/tpmc <twitch channel>&r"));
        } else {
            if (MessageController.twitchPlays) {
                MessageController.twitchPlays = false;
                src.asPlayer().sendMessage(Util.translateString("[&dTPMC&r] &dTwitch Plays MineCraft&7 disabled&r"));
            }
            ChatReader.stop();
        }
        return 1;
    }

    /* tpmc pressDuration <ms> */
    private static int f5(CommandSource src, long ms) throws CommandSyntaxException {
        if (ms >= 0) MessageController.pressDuration = ms;
        src.asPlayer().sendMessage(Util.translateString(String.format("[&dTPMC&r]&7 pressDuration = %d&r", MessageController.pressDuration)));
        return 1;
    }
}
