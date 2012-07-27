package com.netprogs.minecraft.plugins.payrank;

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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.netprogs.minecraft.plugins.payrank.command.PayRankDispatcher;
import com.netprogs.minecraft.plugins.payrank.config.PayRanksConfig;
import com.netprogs.minecraft.plugins.payrank.config.PluginConfig;
import com.netprogs.minecraft.plugins.payrank.config.ResourcesConfig;
import com.netprogs.minecraft.plugins.payrank.config.SettingsConfig;
import com.netprogs.minecraft.plugins.payrank.listener.PlayerChatListener;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PayRankPlugin extends JavaPlugin {

    private final Logger logger = Logger.getLogger("Minecraft");

    private String pluginName;
    private File pluginFolder;

    private static Economy economy = null;
    private static Permission permission = null;

    // used for sending completely anonymous data to http://mcstats.org for usage tracking
    private Metrics metrics;

    public void onEnable() {

        // report that this plug in is being loaded
        PluginDescriptionFile pdfFile = getDescription();

        pluginName = getDescription().getName();
        pluginFolder = getDataFolder();

        setupEconomy();
        setupPermission();

        // load the rank data from the XML file
        loadConfigurations();

        // attach to the "payRank" command
        getCommand("payRank").setExecutor(new PayRankDispatcher(this));

        // attach the events to our listener if turned on
        if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isChatFormattingEnabled()) {

            getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        }

        // start up the metrics engine
        try {
            metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error while enabling Metrics.");
        }

        logger.info("[" + pdfFile.getName() + "] v" + pdfFile.getVersion() + " has been enabled.");
    }

    public void loadConfigurations() {

        PluginConfig.getInstance().reset();
        PluginConfig.getInstance().register(new SettingsConfig(getDataFolder() + "/config.json"));
        PluginConfig.getInstance().register(new PayRanksConfig(getDataFolder() + "/payranks.json"));
        PluginConfig.getInstance().register(new ResourcesConfig(getDataFolder() + "/resources.json"));
    }

    private void setupEconomy() {

        RegisteredServiceProvider<Economy> economyProvider =
                getServer().getServicesManager().getRegistration(Economy.class);

        if (economyProvider != null) {
            economy = (Economy) economyProvider.getProvider();
        }
    }

    public void setupPermission() {

        RegisteredServiceProvider<Permission> permissionProvider =
                getServer().getServicesManager().getRegistration(Permission.class);

        if (permissionProvider != null) {
            permission = (Permission) permissionProvider.getProvider();
        }
    }

    public void onDisable() {

        PluginDescriptionFile pdfFile = getDescription();
        this.logger.info("[" + pdfFile.getName() + "] has been disabled.");
    }

    public Economy getEconomy() {
        return economy;
    }

    public Permission getPermission() {
        return permission;
    }

    public String getPluginName() {
        return pluginName;
    }

    public File getPluginFolder() {
        return pluginFolder;
    }
}
