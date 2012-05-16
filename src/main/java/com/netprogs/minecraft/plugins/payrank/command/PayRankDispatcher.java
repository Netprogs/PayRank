package com.netprogs.minecraft.plugins.payrank.command;

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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.netprogs.minecraft.plugins.payrank.PayRankPlugin;
import com.netprogs.minecraft.plugins.payrank.command.PayRankPermissions.PayRankPermission;
import com.netprogs.minecraft.plugins.payrank.command.exception.ArgumentsMissingException;
import com.netprogs.minecraft.plugins.payrank.command.exception.InvalidPermissionsException;
import com.netprogs.minecraft.plugins.payrank.command.util.MessageUtil;
import com.netprogs.minecraft.plugins.payrank.config.PluginConfig;
import com.netprogs.minecraft.plugins.payrank.config.SettingsConfig;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Dispatches all incoming commands off to their related {@link IPayRankCommand} instance to handle.
 * @author Scott Milne
 */
public class PayRankDispatcher implements CommandExecutor {

    private final Logger logger = Logger.getLogger("Minecraft");

    private Map<String, IPayRankCommand> commands;
    private PayRankPlugin plugin;

    public PayRankDispatcher(PayRankPlugin plugin) {

        this.plugin = plugin;

        createCommandMap();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {

        // first thing we want to do is check for who's sending this request
        HashSet<PayRankPermission> permissions = PayRankPermissions.getPermissions(plugin, sender);

        if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
            StringWriter argumentList = new StringWriter();
            for (String argument : arguments) {
                argumentList.append(argument);
                argumentList.append(" ");
            }
            logger.info("Incoming command: " + argumentList.toString());
        }

        try {

            // if nothing given, don't continue
            if (arguments.length == 0) {
                throw new ArgumentsMissingException();
            }

            // Grab the first argument, this should be our command.
            String requestedCommand = arguments[0];

            // put the rest into a list
            List<String> commandArguments = new ArrayList<String>();
            for (int i = 1; i < arguments.length; i++) {
                commandArguments.add(arguments[i]);
            }

            if (commands.containsKey(requestedCommand)) {

                IPayRankCommand payRankCommand = commands.get(requestedCommand);

                // try to run the command
                try {

                    payRankCommand.run(plugin, sender, commandArguments, permissions);

                } catch (ArgumentsMissingException exception) {

                    // If we're here, the command wasn't given enough information. So we'll send the help instead.
                    MessageUtil.sendHelpMessage(sender, payRankCommand);

                } catch (InvalidPermissionsException exception) {

                    // If we're here, the sender requesting the command did not have permission to do so
                    MessageUtil.sendInvalidPermissionsMessage(sender);
                }

                // we've handled this command in one form or another
                return true;
            }

            // Send all help messages if none matched (/payRank help would do this also)
            MessageUtil.sendAllHelpMessages(plugin, sender, commands, permissions);

        } catch (ArgumentsMissingException exception) {

            // if any arguments are missing, produce the help page for all of them
            MessageUtil.sendAllHelpMessages(plugin, sender, commands, permissions);
        }

        return true;
    }

    private void createCommandMap() {

        commands = new HashMap<String, IPayRankCommand>();

        CommandList list = new CommandList();
        commands.put(list.getCommandName(), list);

        CommandPurchase purchase = new CommandPurchase();
        commands.put(purchase.getCommandName(), purchase);

        CommandPromote promote = new CommandPromote();
        commands.put(promote.getCommandName(), promote);

        CommandDemote demote = new CommandDemote();
        commands.put(demote.getCommandName(), demote);

        CommandGive give = new CommandGive();
        commands.put(give.getCommandName(), give);

        CommandRemove remove = new CommandRemove();
        commands.put(remove.getCommandName(), remove);

        CommandCurrent current = new CommandCurrent();
        commands.put(current.getCommandName(), current);

        CommandReload reload = new CommandReload();
        commands.put(reload.getCommandName(), reload);
    }
}
