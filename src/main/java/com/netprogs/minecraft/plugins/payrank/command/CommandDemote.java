package com.netprogs.minecraft.plugins.payrank.command;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import com.netprogs.minecraft.plugins.payrank.PayRankPlugin;
import com.netprogs.minecraft.plugins.payrank.command.PayRankPermissions.PayRankPermission;
import com.netprogs.minecraft.plugins.payrank.command.data.Help;
import com.netprogs.minecraft.plugins.payrank.command.exception.ArgumentsMissingException;
import com.netprogs.minecraft.plugins.payrank.command.exception.InvalidPermissionsException;
import com.netprogs.minecraft.plugins.payrank.command.util.PlayerCommandUtil;
import com.netprogs.minecraft.plugins.payrank.config.PluginConfig;
import com.netprogs.minecraft.plugins.payrank.config.ResourcesConfig;
import com.netprogs.minecraft.plugins.payrank.config.SettingsConfig;
import com.netprogs.minecraft.plugins.payrank.config.data.PayRank;
import com.netprogs.minecraft.plugins.payrank.player.PlayerInfo;

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

public class CommandDemote extends PayRankCommand {

    private final Logger logger = Logger.getLogger("Minecraft");

    public CommandDemote() {
        super("demote", PayRankPermission.demote);
    }

    public void run(PayRankPlugin plugin, CommandSender sender, List<String> arguments,
            HashSet<PayRankPermission> permissions) throws ArgumentsMissingException, InvalidPermissionsException {

        // check arguments
        if (arguments.size() != 1) {
            throw new ArgumentsMissingException();
        }

        // check permissions
        if (!permissions.contains(getPermission())) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Player does not have the payrank.demote permission");
            }

            throw new InvalidPermissionsException();
        }

        if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
            logger.info("Executing demote...");
        }

        // assign the expected arguments to variables
        String playerName = arguments.get(0);

        // determine which rank to push the user down to, then do it
        PayRank currentRank = null;
        PayRank previousRank = null;

        // get the players information
        PlayerInfo playerInfo = getPlayerInfoUtil().getPlayerInfo(plugin, sender, playerName);
        if (playerInfo != null) {

            // get their rank positions
            currentRank = playerInfo.getCurrentRank();
            previousRank = playerInfo.getPreviousRank();

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {

                logger.info("Player: " + playerInfo.getPlayer().getName());
                logger.info("World: " + playerInfo.getPlayer().getWorld());

                if (currentRank != null) {
                    logger.info("Current Rank: " + currentRank.getName());
                } else {
                    logger.info("Current Rank: null");
                }

                if (previousRank != null) {
                    logger.info("Demoted Rank: " + previousRank.getName());
                } else {
                    logger.info("Demoted Rank: null");
                }
            }

            if (previousRank != null && currentRank != null) {

                // change their rank
                PlayerCommandUtil.changeRank(plugin, playerInfo.getPlayer().getWorld(), playerInfo.getPlayer()
                        .getName(), currentRank, previousRank);

                // tell the player they've been demoted
                String demoteCompletedPlayer =
                        PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                                .getResource("payrank.demote.completed.player");

                demoteCompletedPlayer =
                        demoteCompletedPlayer.replaceAll("<rank>", ChatColor.BLUE + previousRank.getName()
                                + ChatColor.GREEN);

                playerInfo.getPlayer().sendMessage(ChatColor.GREEN + demoteCompletedPlayer);

                // tell the sender that they demoted the player
                String demoteCompletedSender =
                        PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                                .getResource("payrank.demote.completed.sender");

                demoteCompletedSender =
                        demoteCompletedSender.replaceAll("<rank>", ChatColor.BLUE + previousRank.getName()
                                + ChatColor.GREEN);

                demoteCompletedSender =
                        demoteCompletedSender.replaceAll("<player>", ChatColor.AQUA + playerInfo.getPlayer().getName()
                                + ChatColor.GREEN);

                sender.sendMessage(ChatColor.GREEN + demoteCompletedSender);

            } else {

                // tell the sender that they can't be demoted any further
                String demoteMinimumSender =
                        PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                                .getResource("payrank.demote.lowestRankReached.sender");

                demoteMinimumSender =
                        demoteMinimumSender.replaceAll("<player>", ChatColor.AQUA + playerInfo.getPlayer().getName()
                                + ChatColor.GREEN);

                sender.sendMessage(ChatColor.GREEN + demoteMinimumSender);
            }
        }
    }

    public Help help() {

        Help help = new Help();
        help.setCommand(getCommandName());
        help.setArguments("<player>");

        String demoteHelp =
                PluginConfig.getInstance().getConfig(ResourcesConfig.class).getResource("payrank.demote.help");

        help.setDescription(demoteHelp);

        return help;
    }
}
