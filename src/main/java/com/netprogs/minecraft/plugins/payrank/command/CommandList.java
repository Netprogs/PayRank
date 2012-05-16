package com.netprogs.minecraft.plugins.payrank.command;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import com.netprogs.minecraft.plugins.payrank.PayRankPlugin;
import com.netprogs.minecraft.plugins.payrank.command.PayRankPermissions.PayRankPermission;
import com.netprogs.minecraft.plugins.payrank.command.data.Help;
import com.netprogs.minecraft.plugins.payrank.command.exception.ArgumentsMissingException;
import com.netprogs.minecraft.plugins.payrank.command.exception.InvalidPermissionsException;
import com.netprogs.minecraft.plugins.payrank.config.PayRanksConfig;
import com.netprogs.minecraft.plugins.payrank.config.PluginConfig;
import com.netprogs.minecraft.plugins.payrank.config.ResourcesConfig;
import com.netprogs.minecraft.plugins.payrank.config.SettingsConfig;
import com.netprogs.minecraft.plugins.payrank.config.data.PayRank;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

public class CommandList extends PayRankCommand {

    private final Logger logger = Logger.getLogger("Minecraft");

    public CommandList() {
        super("list", PayRankPermission.list);
    }

    public void run(PayRankPlugin plugin, CommandSender sender, List<String> arguments,
            HashSet<PayRankPermission> permissions) throws ArgumentsMissingException, InvalidPermissionsException {

        // no arguments

        // check permissions
        if (!permissions.contains(getPermission())) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Player does not have the payrank.list permission");
            }

            throw new InvalidPermissionsException();
        }

        // for each of the ranks, display them and their cost
        List<PayRank> ranks = PluginConfig.getInstance().getConfig(PayRanksConfig.class).getPayRanks();

        String listHeader = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                .getResource("payrank.list.header");

        listHeader = listHeader.replaceAll("<plugin>", plugin.getPluginName());

        // send the header
        sender.sendMessage(ChatColor.AQUA + listHeader);

        // If this is a player asking for the list, color-code what they have and what they can get
        PayRank currentPlayerRank = null;
        if ((sender instanceof Player)) {

            for (PayRank rank : ranks) {

                Player player = (Player) sender;
                if (getPlayerInfoUtil().playerInGroup(plugin, player.getLocation().getWorld(), sender.getName(),
                        rank.getGroup())) {

                    currentPlayerRank = rank;
                }
            }
        }

        // produce the list of available ranks and their prices
        for (PayRank rank : ranks) {

            // color code as needed
            ChatColor color = ChatColor.WHITE;
            if (currentPlayerRank == rank) {
                color = ChatColor.GREEN;
            } else {
                color = ChatColor.GRAY;
            }

            // send the rank
            sender.sendMessage(color + rank.getName() + ": " + rank.getPrice());
        }
    }

    public Help help() {

        Help help = new Help();
        help.setCommand(getCommandName());
        help.setArguments("");

        String giveHelp = PluginConfig.getInstance().getConfig(ResourcesConfig.class).getResource("payrank.list.help");
        help.setDescription(giveHelp);

        return help;
    }
}
