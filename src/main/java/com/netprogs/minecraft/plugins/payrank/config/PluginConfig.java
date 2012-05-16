package com.netprogs.minecraft.plugins.payrank.config;

import java.util.HashMap;
import java.util.Map;

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
 * Singleton implementation to allow the storing of any {@link Configuration} subclass. This allows you to obtain any of
 * your configurations from any parts of your code without having to pass around the instance itself.
 */
public class PluginConfig {

    private final Map<Class<?>, Configuration<?>> configurations;

    private static final PluginConfig SINGLETON = new PluginConfig();

    public static PluginConfig getInstance() {
        return SINGLETON;
    }

    private PluginConfig() {

        configurations = new HashMap<Class<?>, Configuration<?>>();
    }

    public void reset() {
        configurations.clear();
    }

    /**
     * Add a configuration instance to the manager.
     * @param configuration The configuration instance to add.
     */
    public void register(Configuration<?> configuration) {

        configuration.loadConfig();
        configurations.put(configuration.getClass(), configuration);
    }

    /**
     * Request a particular configuration instance back.
     * @param configClass The class of the configuration you'd like to request.
     * @return The configuration instance. NULL if not found.
     */
    public <T> T getConfig(Class<T> configClass) {
        return configClass.cast(configurations.get(configClass));
    }
}
