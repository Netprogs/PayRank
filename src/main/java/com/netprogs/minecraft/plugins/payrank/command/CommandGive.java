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
import com.netprogs.minecraft.plugins.payrank.config.PayRanksConfig;
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

public class CommandGive extends PayRankCommand {

    private final Logger logger = Logger.getLogger("Minecraft");

    public CommandGive() {
        super("give", PayRankPermission.give);
    }

    public void run(PayRankPlugin plugin, CommandSender sender, List<String> arguments,
            HashSet<PayRankPermission> permissions) throws ArgumentsMissingException, InvalidPermissionsException {

        // check arguments
        if (arguments.size() != 2) {
            throw new ArgumentsMissingException();
        }

        // check permissions
        if (!permissions.contains(getPermission())) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Player does not have the payrank.give permission");
            }

            throw new InvalidPermissionsException();
        }

        // get the rank
        String searchPlayer = arguments.get(0);
        String searchRank = arguments.get(1);

        if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {

            logger.info("Search for player: " + searchPlayer);
            logger.info("Search for rank: " + searchRank);
        }

        // get the players information
        PlayerInfo playerInfo = getPlayerInfoUtil().getPlayerInfo(plugin, sender, searchPlayer);
        if (playerInfo != null) {

            // get the rank we want to assign to them
            PayRank giveRank = PluginConfig.getInstance().getConfig(PayRanksConfig.class)
                    .getPayRankMatching(searchRank);

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {

                logger.info("Player: " + playerInfo.getPlayer());
                logger.info("World: " + playerInfo.getPlayer().getWorld());
                logger.info("Name: " + playerInfo.getPlayer().getName());
                logger.info("Previous Rank: " + playerInfo.getPreviousRank());
                logger.info("Current Rank: " + playerInfo.getCurrentRank());
                logger.info("Next Rank: " + playerInfo.getNextRank());
            }

            // check to make sure it's not the same rank
            if (giveRank == playerInfo.getCurrentRank()) {

                String alreadyHasRankSender = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                        .getResource("payrank.give.alreadyHasRank.sender");

                alreadyHasRankSender = alreadyHasRankSender.replaceAll("<rank>", ChatColor.BLUE + giveRank.getName()
                        + ChatColor.RED);

                // give the error back to the caller
                sender.sendMessage(ChatColor.RED + alreadyHasRankSender);
                return;

            } else if (giveRank != null) {

                // change their rank
                PlayerCommandUtil.changeRank(plugin, playerInfo.getPlayer().getWorld(), playerInfo.getPlayer()
                        .getName(), playerInfo.getCurrentRank(), giveRank);

                // tell the user the player has been promoted
                String completedSender = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                        .getResource("payrank.give.completed.sender");

                completedSender = completedSender.replaceAll("<rank>", ChatColor.BLUE + giveRank.getName()
                        + ChatColor.GREEN);

                completedSender = completedSender.replaceAll("<player>", ChatColor.AQUA
                        + playerInfo.getPlayer().getName() + ChatColor.GREEN);

                sender.sendMessage(ChatColor.GREEN + completedSender);

                // tell the player they've been changed if they're online
                if (playerInfo.getPlayer() != null) {

                    String completedPlayer = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                            .getResource("payrank.give.completed.player");

                    completedPlayer = completedPlayer.replaceAll("<rank>", ChatColor.BLUE + giveRank.getName()
                            + ChatColor.GREEN);

                    String message = ChatColor.GREEN + completedPlayer;

                    playerInfo.getPlayer().sendMessage(message);
                }

            } else {

                // give the error back to the caller
                String cannotFindRankSender = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                        .getResource("payrank.give.alreadyHasRank.sender");

                cannotFindRankSender = cannotFindRankSender.replaceAll("<rank>", ChatColor.BLUE + searchRank
                        + ChatColor.RED);

                sender.sendMessage(ChatColor.RED + cannotFindRankSender);
            }
        }
    }

    public Help help() {

        Help help = new Help();
        help.setCommand(getCommandName());
        help.setArguments("<player> <rank>");

        String giveHelp = PluginConfig.getInstance().getConfig(ResourcesConfig.class).getResource("payrank.give.help");
        help.setDescription(giveHelp);

        return help;
    }
}
