package com.netprogs.minecraft.plugins.payrank.command.util;

import java.util.HashSet;
import java.util.Map;

import com.netprogs.minecraft.plugins.payrank.PayRankPlugin;
import com.netprogs.minecraft.plugins.payrank.command.IPayRankCommand;
import com.netprogs.minecraft.plugins.payrank.command.PayRankPermissions.PayRankPermission;
import com.netprogs.minecraft.plugins.payrank.command.data.Help;
import com.netprogs.minecraft.plugins.payrank.config.PluginConfig;
import com.netprogs.minecraft.plugins.payrank.config.ResourcesConfig;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/*
 * Copyright 2012 Scott Milne. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

public class MessageUtil {

    public static void sendInvalidPermissionsMessage(CommandSender sender) {

        String invalidPermissions = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                .getResource("payrank.error.invalidPermissions");

        sender.sendMessage(ChatColor.RED + invalidPermissions);
    }

    public static void sendHelpMessage(CommandSender sender, IPayRankCommand command) {

        // send the message back to the caller
        sender.sendMessage(formatHelp(command.help()));
    }

    public static void sendAllHelpMessages(PayRankPlugin plugin, CommandSender sender,
            Map<String, IPayRankCommand> commands, HashSet<PayRankPermission> permissions) {

        String listHeader = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                .getResource("payrank.commands.header");

        listHeader = listHeader.replaceAll("<plugin>", plugin.getPluginName());

        sender.sendMessage(ChatColor.GRAY + listHeader);

        boolean hasCommand = false;

        for (IPayRankCommand payRankCommand : commands.values()) {

            // check to see if the users permissions contain the one for this command
            if (permissions.contains(payRankCommand.getPermission())) {

                // it does, so send the help for this command
                sendHelpMessage(sender, payRankCommand);

                // set the flag saying that at least one command was available to the user
                hasCommand = true;
            }
        }

        if (!hasCommand) {

            String noneAvailable = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                    .getResource("payrank.commands.noneAvailable");

            sender.sendMessage(noneAvailable);
        }
    }

    private static String formatHelp(Help help) {

        String message = ChatColor.GOLD + "payRank " + help.getCommand() + " " + help.getArguments() + ChatColor.WHITE
                + ": " + help.getDescription();

        return message;
    }
}
