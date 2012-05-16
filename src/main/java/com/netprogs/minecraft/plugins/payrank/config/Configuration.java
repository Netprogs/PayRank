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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;

/**
 * <pre>
 * Abstract class for managing loading/saving of configuration data.
 *  
 * This class handles the following:
 *  - Determines if this is the first time being called and extracts the configuration file from the jar.
 *  - Checks to see if a default data object was provided and uses that instead of jar extraction.
 *  
 *  This class cannot be used directly and should only be used to create file type specific variations.
 * </pre>
 * @param <T> Generic bean for the configuration to place loaded data into.
 */
public abstract class Configuration<T> {

    private boolean firstRun;
    private boolean copyDefaults;
    private File configFile;
    private Class<T> classObject;

    private T dataObject;
    private T defaultDataObject;

    @SuppressWarnings("unchecked")
    private void createClassObject() {

        // get the runtime class from the sub-class instance for the generic type being used
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        classObject = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    /**
     * Constructor for creating a configuration.
     * @param configFileName The location of the configuration file to be placed when extracted/saved.
     */
    protected Configuration(String configFileName) {

        createClassObject();

        this.defaultDataObject = null;
        this.configFile = new File(configFileName);

        this.firstRun = false;

        if (!configFile.exists()) {

            // create the directories
            configFile.getParentFile().mkdirs();

            this.firstRun = true;
        }
    }

    /**
     * <pre>
     * Loads the configuration data into the data object.
     * The loadConfig() method calls preLoad(), load() and postLoad(). 
     * These methods should be overridden as needed by your sub-class.
     * </pre>
     */
    public final void loadConfig() {

        // extract the configuration data into a file if needed
        extractConfiguration();

        // create the config file from the jar
        preLoad();

        load();

        // called to provide
        postLoad();
    }

    /**
     * <pre>
     * Saves the configuration data object to the file type determined by the sub-class.
     * The saveConfig() method calls preSave(), save() and postSave(). 
     * These methods should be overridden as needed by your sub-class.
     * </pre>
     */
    public final void saveConfig() {

        preSave();

        save();

        postSave();
    }

    /**
     * <pre>
     * Does pre-loading work before the actual load is requested.
     * </pre>
     */
    private void extractConfiguration() {

        // if the configuration file doesn't exist, we need to create or extract it
        if (isFirstRun() && getCopyDefaults()) {

            // if we should be using the default, then create the file, set the object and save it
            try {

                configFile.createNewFile();
                setDataObject(defaultDataObject);
                saveConfig();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else if (isFirstRun()) {

            // we just want to pull it from out jar instead
            extractFromJar();
        }
    }

    /**
     * Override to provide any pre-loading work that needs to be done.
     */
    protected void preLoad() {

    }

    /**
     * Override this method to provide the file type specific requirements for loading the configuration data.
     */
    protected abstract void load();

    /**
     * Override to provide any post-loading work that needs to be done.
     */
    protected void postLoad() {

        this.firstRun = false;
    }

    /**
     * Override to provide any pre-saving work.
     */
    protected void preSave() {

    }

    /**
     * Override this method to provide the file type specific requirements for saving the configuration data.
     */
    protected abstract void save();

    /**
     * Override to provide any post-saving work.
     */
    protected void postSave() {

    }

    /**
     * Reloads the configuration data by calling {@link #saveConfig} then {@link #loadConfig}.
     */
    public final void reloadConfig() {
        try {

            // saveConfig();

            setDataObject(null);
            loadConfig();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setDataObject(T dataObject) {
        this.dataObject = dataObject;
    }

    protected T getDataObject() {
        return dataObject;
    }

    private void extractFromJar() {

        try {

            // grab the file from our jar
            InputStream inputStream = classObject.getClassLoader().getResourceAsStream(configFile.getName());

            // open the config file and place the contents of our initial configuration into it
            FileWriter outputStream = new FileWriter(configFile);

            int c;
            while ((c = inputStream.read()) != -1) {
                outputStream.write(c);
            }

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Class<T> getClassObject() {
        return classObject;
    }

    protected File getConfigFile() {
        return configFile;
    }

    public boolean isFirstRun() {
        return firstRun;
    }

    public void setDefaultDataObject(T defaultDataObject) {
        this.defaultDataObject = defaultDataObject;
    }

    public boolean getCopyDefaults() {
        return copyDefaults;
    }

    public void setCopyDefaults(boolean copyDefaults) {
        this.copyDefaults = copyDefaults;
    }
}
