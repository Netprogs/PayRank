package com.netprogs.minecraft.plugins.payrank.player;

import java.util.List;
import java.util.logging.Logger;

import com.netprogs.minecraft.plugins.payrank.PayRankPlugin;
import com.netprogs.minecraft.plugins.payrank.config.PayRanksConfig;
import com.netprogs.minecraft.plugins.payrank.config.PluginConfig;
import com.netprogs.minecraft.plugins.payrank.config.ResourcesConfig;
import com.netprogs.minecraft.plugins.payrank.config.SettingsConfig;
import com.netprogs.minecraft.plugins.payrank.config.data.PayRank;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

/**
 * This class provides basic player utility methods.
 */
public class PlayerInfoUtil {

    private final Logger logger = Logger.getLogger("Minecraft");

    public boolean playerInGroup(PayRankPlugin plugin, World world, String playerName, String groupName) {

        // TODO: Passing world as NULL. I may need to revisit this later.
        // We want to pass in NULL here because we don't care about what world they're in, we just want to know
        // if they've been assigned the group at all.
        final String nullString = null;

        String[] groupList = plugin.getPermission().getPlayerGroups(nullString, playerName);
        for (String group : groupList) {
            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("playerGroup: " + group);
            }
            if (group.equalsIgnoreCase(groupName)) {
                if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                    logger.info("Matched playerGroup: " + group);
                }
                return true;
            }
        }

        // return plugin.getPermission().playerInGroup(nullString, playerName, groupName);
        return false;
    }

    public PlayerInfo getPlayerInfo(JavaPlugin plugin, CommandSender sender, String searchPlayer) {

        PlayerInfo playerInfo = null;

        // cast the plug-in to ours since we know that's what it is so we can use it here
        PayRankPlugin payRankPlugin = (PayRankPlugin) plugin;

        // Attempt to find the player and determine if they are online.
        // If they either don't exist or are off-line, this call will return the player as NULL.
        Player player = getMinecraftPlayer(plugin, sender, searchPlayer);
        if (player != null) {

            // we have a valid, online player
            playerInfo = new PlayerInfo();
            playerInfo.setPlayer(player);

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Have valid player instance. Checking for available ranks...");
            }

            // Okay, now that we have their world and name, lets get their PayRank details
            PayRank currentRank = null;
            PayRank previousRank = null;
            PayRank nextRank = null;

            List<PayRank> ranks = PluginConfig.getInstance().getConfig(PayRanksConfig.class).getPayRanks();

            for (PayRank rank : ranks) {

                // if the current user isn't null, that means this next rank is the one we want to use
                if (currentRank != null) {

                    // so let's assign that now, and we're done with the loop
                    nextRank = rank;

                    if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                        logger.info("Found next rank: " + nextRank.getName());
                    }

                    break;
                }

                // Try to find the rank in the players group list
                if (playerInGroup(payRankPlugin, playerInfo.getPlayer().getWorld(), playerInfo.getPlayer().getName(),
                        rank.getGroup())) {

                    // the user is assigned this rank, so lets see if there is another one after this
                    currentRank = rank;

                    if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                        logger.info("Found current rank: " + currentRank.getName());
                    }
                }

                // set the previous rank here so when we find a match to the current one, we have the previous
                if (currentRank == null) {
                    previousRank = rank;
                }
            }

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {

                if (previousRank != null) {
                    logger.info("Found previous rank: " + previousRank.getName());
                } else {
                    logger.info("Did not find previous rank.");
                }
            }

            // let's put everything into the PayRankPlayerInfo and return it
            playerInfo.setPreviousRank(previousRank);
            playerInfo.setCurrentRank(currentRank);
            playerInfo.setNextRank(nextRank);
        }

        return playerInfo;
    }

    private Player getMinecraftPlayer(JavaPlugin plugin, CommandSender sender, String searchPlayer) {

        // get the base player information
        Player player = plugin.getServer().getPlayer(searchPlayer);
        if (player != null) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Found player: " + player.getName());
            }

            return player;

        } else {

            // check to see if they are off-line
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(searchPlayer);
            if (offlinePlayer != null) {

                if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                    logger.info("Player " + offlinePlayer.getName() + " appears to be offline.");
                }

                String playerOfflineSender = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                        .getResource("payrank.error.offlinePlayer.sender");

                playerOfflineSender = playerOfflineSender.replaceAll("<player>",
                        ChatColor.BLUE + offlinePlayer.getName() + ChatColor.RED);

                sender.sendMessage(ChatColor.RED + playerOfflineSender);

            } else {

                // they can't be found there either, so tell the sender they don't exist
                if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                    logger.info("Could not find find player: " + searchPlayer);
                }

                String cannotFindPlayerSender = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                        .getResource("payrank.error.cannotFindPlayer.sender");

                cannotFindPlayerSender = cannotFindPlayerSender.replaceAll("<player>", ChatColor.BLUE + searchPlayer
                        + ChatColor.RED);

                sender.sendMessage(ChatColor.RED + cannotFindPlayerSender);
            }
        }

        return null;
    }
}
