package com.netprogs.minecraft.plugins.payrank.listener;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

import com.netprogs.minecraft.plugins.payrank.PayRankPlugin;
import com.netprogs.minecraft.plugins.payrank.config.PayRanksConfig;
import com.netprogs.minecraft.plugins.payrank.config.PluginConfig;
import com.netprogs.minecraft.plugins.payrank.config.SettingsConfig;
import com.netprogs.minecraft.plugins.payrank.config.data.PayRank;
import com.netprogs.minecraft.plugins.payrank.config.data.PayRankTemplate;
import com.netprogs.minecraft.plugins.payrank.config.data.PayRankWorld;
import com.netprogs.minecraft.plugins.payrank.player.PlayerInfo;
import com.netprogs.minecraft.plugins.payrank.player.PlayerInfoUtil;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final Logger logger = Logger.getLogger("Minecraft");

    private PlayerInfoUtil playerInfoUtil;
    private PayRankPlugin plugin;

    public PlayerChatListener(PayRankPlugin plugin) {

        this.playerInfoUtil = new PlayerInfoUtil();
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {

        Player player = event.getPlayer();
        World world = event.getPlayer().getWorld();

        // get the rank for the player
        PayRank currentUserRank = null;

        if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
            logger.info("Searching for player current PayRank: " + player.getName());
        }

        // get the players information
        PlayerInfo playerInfo = playerInfoUtil.getPlayerInfo(plugin, player, player.getName());
        if (playerInfo != null) {

            currentUserRank = playerInfo.getCurrentRank();
        }

        // if we have a rank, apply the chat settings to their text
        if (currentUserRank != null) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Found PayRank: " + currentUserRank.getName() + ", applying chat settings.");
            }

            // get the list of templates
            Map<String, PayRankTemplate> templates = PluginConfig.getInstance().getConfig(PayRanksConfig.class)
                    .getTemplates();

            // get the desired template for the users current rank
            PayRankTemplate template = templates.get(currentUserRank.getTemplate());

            if (template != null) {

                Map<String, PayRankWorld> worldMap = template.getWorldMap();

                // get the original message from the event and escape the % signs
                String message = event.getMessage().replaceAll("%", "%%");

                // the format can have any of the following options to parse:
                // [time] [world] [prefix] [suffix] [player] and & for colours
                String format = template.getFormat();

                // set the [time] if provided
                String timeFormat = template.getTime();
                if ((timeFormat != null) && (!timeFormat.equalsIgnoreCase(""))) {

                    DateFormat dateFormat = new SimpleDateFormat(timeFormat);
                    Calendar calendar = Calendar.getInstance();
                    format = format.replaceAll("\\[time\\]", String.valueOf(dateFormat.format(calendar.getTime())));

                } else {

                    // no time format was given, so we'll strip it out entirely
                    String space = "";
                    if (format.contains("[time] ")) {
                        space = " ";
                    }
                    format = format.replaceAll("\\[time\\]" + space, "");
                }

                // set the [world] if provided
                PayRankWorld payRankWorld = worldMap.get(world.getName());
                if (payRankWorld != null) {
                    format = format.replaceAll("\\[world\\]", payRankWorld.getDisplayName());
                }

                // do [prefix] and [suffix] and [player]
                format = format.replaceAll("\\[prefix\\]", currentUserRank.getPrefix());
                format = format.replaceAll("\\[suffix\\]", currentUserRank.getSuffix());
                format = format.replaceAll("\\[player\\]", player.getName());

                // finally, replace all the & signs with \u00A7 ($2 is the letter after &)
                format = format.replaceAll("(&([A-Fa-f0-9L-Ol-o]))", "\u00A7$2");

                // now tell the even to use our formatted name instead
                event.setFormat(format + message);
            }

        } else {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Could not find a current PayRank: " + player.getName() + ". Using default chat settings.");
            }
        }
    }
}
