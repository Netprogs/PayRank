package com.netprogs.minecraft.plugins.payrank.command;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import com.netprogs.minecraft.plugins.payrank.PayRankPlugin;
import com.netprogs.minecraft.plugins.payrank.command.PayRankPermissions.PayRankPermission;
import com.netprogs.minecraft.plugins.payrank.command.data.Help;
import com.netprogs.minecraft.plugins.payrank.command.exception.ArgumentsMissingException;
import com.netprogs.minecraft.plugins.payrank.command.exception.InvalidPermissionsException;
import com.netprogs.minecraft.plugins.payrank.config.PluginConfig;
import com.netprogs.minecraft.plugins.payrank.config.ResourcesConfig;
import com.netprogs.minecraft.plugins.payrank.config.SettingsConfig;
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

public class CommandCurrent extends PayRankCommand {

    private final Logger logger = Logger.getLogger("Minecraft");

    public CommandCurrent() {
        super("current", PayRankPermission.current, PayRankPermission.currentOthers);
    }

    public void run(PayRankPlugin plugin, CommandSender sender, List<String> arguments,
            HashSet<PayRankPermission> permissions) throws ArgumentsMissingException, InvalidPermissionsException {

        // check arguments
        if (arguments.size() > 1) {
            throw new ArgumentsMissingException();
        }

        String searchPlayer = null;

        // if no arguments, we'll check the main permission
        if (arguments.size() == 0) {

            // check permissions
            if (!permissions.contains(getPermission())) {

                if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                    logger.info("Player does not have the payrank.current permission");
                }

                throw new InvalidPermissionsException();
            }

            // set the search player to the sender
            searchPlayer = sender.getName();

        } else if (arguments.size() == 1) {

            // if one argument, we'll check the permission.others
            if (!permissions.contains(getPermissionOthers())) {

                if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                    logger.info("Player does not have the payrank.current.others permission");
                }

                throw new InvalidPermissionsException();
            }

            // set the search player to the parameter
            searchPlayer = arguments.get(0);
        }

        if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {

            logger.info("Attempting to find current rank...");
            logger.info("Search for player: " + searchPlayer);
        }

        // get the players information
        PlayerInfo playerInfo = getPlayerInfoUtil().getPlayerInfo(plugin, sender, searchPlayer);
        if (playerInfo != null) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {

                logger.info("Player: " + playerInfo.getPlayer());
                logger.info("World: " + playerInfo.getPlayer().getWorld());
                logger.info("Name: " + playerInfo.getPlayer().getName());
                logger.info("Current Rank: " + playerInfo.getCurrentRank());
            }

            // check to make sure they have a current rank
            if (playerInfo.getCurrentRank() != null) {

                // if the sender isn't the player, send the sender a different message
                if (sender != playerInfo.getPlayer()) {

                    // tell the sender we're done
                    String completedSender = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                            .getResource("payrank.current.completed.sender");

                    completedSender = completedSender.replaceAll("<rank>", ChatColor.BLUE
                            + playerInfo.getCurrentRank().getName() + ChatColor.GREEN);

                    completedSender = completedSender.replaceAll("<player>", ChatColor.AQUA
                            + playerInfo.getPlayer().getName() + ChatColor.GREEN);

                    // give the error back to the caller
                    sender.sendMessage(ChatColor.GREEN + completedSender);

                } else {

                    // tell the player we're done
                    String completedPlayer = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                            .getResource("payrank.current.completed.player");

                    completedPlayer = completedPlayer.replaceAll("<rank>", ChatColor.BLUE
                            + playerInfo.getCurrentRank().getName() + ChatColor.GREEN);

                    // give the error back to the caller
                    sender.sendMessage(ChatColor.GREEN + completedPlayer);
                }

                return;

            } else {

                // give the error back to the caller
                String cannotFindRankSender = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                        .getResource("payrank.current.cannotFindRank.sender");

                sender.sendMessage(ChatColor.RED + cannotFindRankSender);
            }
        }
    }

    public Help help() {

        Help help = new Help();
        help.setCommand(getCommandName());
        help.setArguments("[player]");

        String giveHelp = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                .getResource("payrank.current.help");

        help.setDescription(giveHelp);

        return help;
    }
}
