package com.netprogs.minecraft.plugins.payrank.config;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.netprogs.minecraft.plugins.payrank.config.data.PayRank;
import com.netprogs.minecraft.plugins.payrank.config.data.PayRankTemplate;
import com.netprogs.minecraft.plugins.payrank.config.data.PayRanks;

public class PayRanksConfig extends JsonConfiguration<PayRanks> {

    private final Logger logger = Logger.getLogger("Minecraft");

    private List<PayRank> payRankList = null;
    private Map<String, PayRank> payRankNameMap = null;
    private Map<String, PayRank> payRankGroupMap = null;
    private Map<String, PayRankTemplate> templateMap = null;

    public PayRanksConfig(String configFileName) {
        super(configFileName);
    }

    public List<PayRank> getPayRanks() {

        getPayRankData();
        return payRankList;
    }

    public PayRank getPayRankMatching(String rankName) {

        getPayRankData();

        // check the ap that contains the rank display names first
        PayRank value = payRankNameMap.get(rankName);
        if (value == null) {

            // nothing found there, let's try and see if they passed the actual group name instead
            return payRankGroupMap.get(rankName);
        }

        return value;
    }

    public PayRank getPayRankByGroup(String rankGroup) {

        getPayRankData();
        return payRankGroupMap.get(rankGroup);
    }

    private void getPayRankData() {

        if (payRankList == null || payRankNameMap == null || payRankGroupMap == null) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Initializing PayRank data...");
            }

            // get the list from the data object
            List<PayRank> ranks = getDataObject().getPayRanks();

            payRankList = new ArrayList<PayRank>();
            payRankNameMap = new HashMap<String, PayRank>();
            payRankGroupMap = new HashMap<String, PayRank>();

            // we're going to check the list for nulls in case anything went wrong with loading
            for (PayRank rank : ranks) {

                if (rank != null) {

                    if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                        logger.info("Adding rank: " + rank.getName());
                    }

                    payRankNameMap.put(rank.getName(), rank);
                    payRankGroupMap.put(rank.getGroup(), rank);
                    payRankList.add(rank);
                }
            }
        }
    }

    public Map<String, PayRankTemplate> getTemplates() {

        if (templateMap == null) {

            if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                logger.info("Initializing template data...");
            }

            List<PayRankTemplate> templates = getDataObject().getTemplates();

            templateMap = new HashMap<String, PayRankTemplate>();

            for (PayRankTemplate template : templates) {

                if (PluginConfig.getInstance().getConfig(SettingsConfig.class).isLoggingDebug()) {
                    logger.info("Adding template: " + template.getName());
                }

                templateMap.put(template.getName(), template);
            }
        }

        return templateMap;
    }
}
