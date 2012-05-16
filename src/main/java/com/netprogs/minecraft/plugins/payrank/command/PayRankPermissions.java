package com.netprogs.minecraft.plugins.payrank.command;

import java.util.HashSet;
import java.util.logging.Logger;

import com.netprogs.minecraft.plugins.payrank.PayRankPlugin;
import com.netprogs.minecraft.plugins.payrank.config.PluginConfig;
import com.netprogs.minecraft.plugins.payrank.config.SettingsConfig;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
public class PayRankPermissions {

    private static final Logger logger = Logger.getLogger("Minecraft");

    public static enum PayRankPermission {
        currentOthers {
            public String toString() {
                return "current.others";
            }
        },
        reload, purchase, promote, demote, give, remove, current, list
    }

    public static HashSet<PayRankPermission> getPermissions(PayRankPlugin plugin, CommandSender sender) {

        // first thing we want to do is check for who's sending this request
        HashSet<PayRankPermission> permissions = new HashSet<PayRankPermission>();

        // if the sender is the console, then only certain commands will be allowed
        if ((sender instanceof ConsoleCommandSender)) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Setting console permissions...");
            }

            permissions.add(PayRankPermission.promote);
            permissions.add(PayRankPermission.demote);
            permissions.add(PayRankPermission.give);
            permissions.add(PayRankPermission.remove);
            permissions.add(PayRankPermission.current);
            permissions.add(PayRankPermission.currentOthers);
            permissions.add(PayRankPermission.reload);
            permissions.add(PayRankPermission.list);

        } else if ((sender instanceof Player)) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Setting player permissions...");
            }

            Player player = (Player) sender;

            // If the sender is a player, then only certain commands will be allowed
            // So now we'll check to see what they are allowed to do.
            for (PayRankPermission permission : PayRankPermission.values()) {

                if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                    logger.info("Checking: payrank." + permission.toString());
                }

                // we're going to use Vault to verify the permissions
                if (plugin.getPermission().has(player, "payrank." + permission.toString())) {
                    if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                        logger.info("Matched: payrank." + permission.toString());
                    }
                    permissions.add(permission);
                }
            }
        }

        return permissions;
    }
}
