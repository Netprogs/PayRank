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

public class CommandPurchase extends PayRankCommand {

    private final Logger logger = Logger.getLogger("Minecraft");

    public CommandPurchase() {
        super("purchase", PayRankPermission.purchase);
    }

    public void run(PayRankPlugin plugin, CommandSender sender, List<String> arguments,
            HashSet<PayRankPermission> permissions) throws ArgumentsMissingException, InvalidPermissionsException {

        // no additional arguments needed

        if (permissions.contains(getPermission())) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Executing purchase...");
            }

            Player player = (Player) sender;
            PlayerCommandUtil.promote(plugin, getPlayerInfoUtil(), sender, player.getName(), true);

        } else {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Player does not have the payrank.purchase permission");
            }

            throw new InvalidPermissionsException();
        }
    }

    public Help help() {

        Help help = new Help();
        help.setCommand(getCommandName());
        help.setArguments("");

        String purchaseHelp = PluginConfig.getInstance().getConfig(ResourcesConfig.class)
                .getResource("payrank.purchase.help");

        help.setDescription(purchaseHelp);

        return help;
    }
}
