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

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class JsonConfiguration<T> extends Configuration<T> {

    private Gson json;

    protected JsonConfiguration(String configFileName) {
        super(configFileName);

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.disableHtmlEscaping();
        builder.serializeNulls();
        json = builder.create();
    }

    /**
     * Implements {@link Configuration#load()} to process a JSON based file load.
     */
    protected synchronized void load() {

        try {

            InputStream inputStream = new FileInputStream(getConfigFile());
            InputStreamReader reader = new InputStreamReader(inputStream);

            setDataObject(json.fromJson(reader, getClassObject()));

            reader.close();
            inputStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Implements {@link Configuration#save()} to process a JSON based file save.
     */
    protected synchronized void save() {

        try {

            String jsonOutput = json.toJson(getDataObject());

            FileWriter fstream = new FileWriter(getConfigFile());
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(jsonOutput);
            out.close();
            fstream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
