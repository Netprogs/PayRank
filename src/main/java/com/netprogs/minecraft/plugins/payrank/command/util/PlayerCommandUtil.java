package com.netprogs.minecraft.plugins.payrank.command.util;

import java.util.List;
import java.util.logging.Logger;

import com.netprogs.minecraft.plugins.payrank.PayRankPlugin;
import com.netprogs.minecraft.plugins.payrank.config.PayRanksConfig;
import com.netprogs.minecraft.plugins.payrank.config.PluginConfig;
import com.netprogs.minecraft.plugins.payrank.config.ResourcesConfig;
import com.netprogs.minecraft.plugins.payrank.config.SettingsConfig;
import com.netprogs.minecraft.plugins.payrank.config.data.PayRank;
import com.netprogs.minecraft.plugins.payrank.player.PlayerInfo;
import com.netprogs.minecraft.plugins.payrank.player.PlayerInfoUtil;

import net.milkbowl.vault.permission.plugins.Permission_PermissionsBukkit;

import org.bukkit.ChatColor;
import org.bukkit.World;
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

public abstract class PlayerCommandUtil {

    private static final Logger logger = Logger.getLogger("Minecraft");

    public static void promote(PayRankPlugin plugin, PlayerInfoUtil playerInfoUtil, CommandSender sender,
            String playerName, boolean doPurchase) {

        // take all the ranks and determine which of them apply to the user and their current world
        List<PayRank> ranks = PluginConfig.getInstance().getConfig(PayRanksConfig.class).getPayRanks();

        PayRank currentUserRank = null;
        PayRank nextRank = null;

        // get the players information
        PlayerInfo playerInfo = playerInfoUtil.getPlayerInfo(plugin, sender, playerName);
        if (playerInfo != null) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {

                logger.info("Sender: " + sender.getName());
                logger.info("Player: " + playerInfo.getPlayer().getName());
                logger.info("World: " + playerInfo.getPlayer().getWorld());
            }

            // get their rank positions
            currentUserRank = playerInfo.getCurrentRank();
            nextRank = playerInfo.getNextRank();

            // if nextRank is null AND currentUserRank are null, then we'll assume this player hasn't been added before
            // and will attempt to add them to a group now
            if (nextRank == null && currentUserRank == null) {

                // grab the first one in the list
                nextRank = ranks.get(0);
            }

            // okay, now, check to see if there was another rank available, if not, tell them
            if (nextRank == null) {

                // if it's a purchase, that means the player requested promote
                if (doPurchase) {

                    String highestRankPlayer =
                            PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                                    .getResource("payrank.promote.highestRankReached.player");

                    playerInfo.getPlayer().sendMessage(ChatColor.GOLD + highestRankPlayer);

                } else {

                    // otherwise, we'll assume admin sent it
                    String highestRankSender =
                            PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                                    .getResource("payrank.promote.highestRankReached.sender");

                    highestRankSender = highestRankSender.replaceAll("<player>", playerInfo.getPlayer().getName());

                    sender.sendMessage(ChatColor.GOLD + highestRankSender);
                }

                return;
            }

            // check again, just in case they don't have ranks defined
            if (nextRank != null) {

                // otherwise, lets promote them
                boolean rankPaidFor = true;

                // Check to see if we need to do the purchase
                if (doPurchase) {

                    if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                        logger.info("Attempting to make purchase: " + nextRank.getName() + ", " + nextRank.getPrice()
                                + ", " + nextRank.getExperience());
                    }

                    // If a price was given, then attempt to obtain it
                    if (nextRank.getExperience() != 0) {

                        // get their current xp
                        int currentXp = playerInfo.getPlayer().getTotalExperience();

                        if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                            logger.info("currentXp: " + currentXp);
                        }

                        // see if they have enough to cover the cost
                        if (currentXp >= nextRank.getExperience()) {

                            // update their current xp
                            playerInfo.getPlayer().setTotalExperience(currentXp - nextRank.getExperience());

                            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                                logger.info("updatedXp: " + playerInfo.getPlayer().getTotalExperience());
                            }

                        } else {

                            // tell the player that they need more xp to purchase the next rank
                            if (playerInfo.getPlayer() != null) {

                                String notEnoughFundsPlayer =
                                        PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                                                .getResource("payrank.promote.notEnoughFunds.player");

                                notEnoughFundsPlayer =
                                        notEnoughFundsPlayer.replaceAll("<price>",
                                                ChatColor.BLUE + Double.toString(nextRank.getExperience()) + "xp"
                                                        + ChatColor.RED);

                                notEnoughFundsPlayer =
                                        notEnoughFundsPlayer.replaceAll("<rank>", ChatColor.BLUE + nextRank.getName()
                                                + ChatColor.RED);

                                String message = ChatColor.RED + notEnoughFundsPlayer;

                                playerInfo.getPlayer().sendMessage(message);

                                // they didn't have enough, so set to false
                                rankPaidFor = false;
                            }
                        }
                    }

                    // If a price was given, then attempt to obtain it
                    if (nextRank.getPrice() != 0) {

                        // First, let's check to see if they can afford the promotion
                        if (plugin.getEconomy().has(playerInfo.getPlayer().getName(), nextRank.getPrice())) {

                            // okay, they can afford it, lets take their money
                            plugin.getEconomy().withdrawPlayer(playerInfo.getPlayer().getName(), nextRank.getPrice());

                        } else {

                            // tell the player that they need more money to purchase the next rank
                            if (playerInfo.getPlayer() != null) {

                                String notEnoughFundsPlayer =
                                        PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                                                .getResource("payrank.promote.notEnoughFunds.player");

                                notEnoughFundsPlayer =
                                        notEnoughFundsPlayer.replaceAll("<price>",
                                                ChatColor.BLUE + Double.toString(nextRank.getPrice()) + ChatColor.RED);

                                notEnoughFundsPlayer =
                                        notEnoughFundsPlayer.replaceAll("<rank>", ChatColor.BLUE + nextRank.getName()
                                                + ChatColor.RED);

                                String message = ChatColor.RED + notEnoughFundsPlayer;

                                playerInfo.getPlayer().sendMessage(message);

                                // they didn't have enough, so set to false
                                rankPaidFor = false;
                            }
                        }
                    }
                }

                // If it's been paid for (or by-passed), do the promotion now
                if (rankPaidFor) {

                    // change their rank
                    changeRank(plugin, playerInfo.getPlayer().getWorld(), playerInfo.getPlayer().getName(),
                            currentUserRank, nextRank);

                    // tell the player they've been promoted
                    if (playerInfo.getPlayer() != null) {

                        String completedPlayer =
                                PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                                        .getResource("payrank.promote.completed.player");

                        completedPlayer =
                                completedPlayer.replaceAll("<rank>", ChatColor.BLUE + nextRank.getName()
                                        + ChatColor.GREEN);

                        String message = ChatColor.GREEN + completedPlayer;

                        playerInfo.getPlayer().sendMessage(message);
                    }

                    // tell the sender that they've been promoted (if not the player)
                    if (sender != playerInfo.getPlayer()) {

                        String completedSender =
                                PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                                        .getResource("payrank.promote.completed.sender");

                        completedSender =
                                completedSender.replaceAll("<player>", ChatColor.AQUA
                                        + playerInfo.getPlayer().getName() + ChatColor.GREEN);

                        completedSender =
                                completedSender.replaceAll("<rank>", ChatColor.BLUE + nextRank.getName()
                                        + ChatColor.GREEN);

                        sender.sendMessage(ChatColor.GREEN + completedSender);
                    }
                }
            }
        }
    }

    public static void removeRank(PayRankPlugin plugin, World world, String playerName, PayRank currentRank) {

        if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
            logger.info("Removing rank: " + currentRank.getName());
        }

        // Now that we have the previous and the current, lets change their rank.
        //
        // The Vault::Permission_PermissionsBukkit version of this checks for world to BE NULL.
        //
        // So, for now, pass NULL for world for that Permission implementation. I'll come back to this later.
        if ((plugin.getPermission() instanceof Permission_PermissionsBukkit)) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Permission_PermissionsBukkit adjustment called.");
            }

            // we add the new rank first, then remove the old one
            final String nullString = null;
            plugin.getPermission().playerRemoveGroup(nullString, playerName, currentRank.getGroup());

        } else {

            // hoping that everyone else plays nicely with that method
            plugin.getPermission().playerRemoveGroup(world, playerName, currentRank.getGroup());
        }
    }

    public static void changeRank(PayRankPlugin plugin, World world, String playerName, PayRank currentRank,
            PayRank newRank) {

        if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {

            if (currentRank != null) {
                logger.info("Changing rank from: " + currentRank.getName() + " to " + newRank.getName());
            } else {
                logger.info("Assigning new rank: " + newRank.getName());
            }
        }

        //
        // Now that we have the previous and the current, lets change their rank.
        //
        // The Vault::Permission_PermissionsBukkit version of this checks for world to BE NULL.
        //
        // So, for now, pass NULL for world for that Permission implementation. I'll come back to this later.
        if ((plugin.getPermission() instanceof Permission_PermissionsBukkit)) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Permission_PermissionsBukkit adjustment called.");
            }

            // we add the new rank first, then remove the old one
            final String nullString = null;

            plugin.getPermission().playerAddGroup(nullString, playerName, newRank.getGroup());

            if (currentRank != null) {
                plugin.getPermission().playerRemoveGroup(nullString, playerName, currentRank.getGroup());
            }

        } else {

            // hoping that everyone else plays nicely with that method
            // we add the new rank first, then remove the old one
            plugin.getPermission().playerAddGroup(world, playerName, newRank.getGroup());

            if (currentRank != null) {
                plugin.getPermission().playerRemoveGroup(world, playerName, currentRank.getGroup());
            }
        }
    }
}
