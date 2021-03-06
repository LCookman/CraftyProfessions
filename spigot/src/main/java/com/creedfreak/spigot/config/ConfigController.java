package com.creedfreak.spigot.config;

import com.google.common.base.Charsets;
import com.creedfreak.common.AbsConfigController;
import com.creedfreak.common.professions.TableName;
import com.creedfreak.common.exceptions.ConfigNotFoundException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * This class is in charge of the main configuration files
 * and whether or not they have been created and parsed yet.
 * Most if not all config setup and parsing for the main
 * config file will happen through this class.
 */
public class ConfigController extends AbsConfigController
{
    private JavaPlugin mPlugin = null;

    private File mConfigFile;
    private YamlConfiguration mConfig;

    private Map<String, File> mWageFiles;
    private Map<String, YamlConfiguration> mWageConfigs;

    /**
     * The default constructor for the Configuration Controller.
     *
     * @param plugin - The CraftyProfessions Plugin to receive all the information from
     *                  in order to build and manage config files.
     */
    public ConfigController (JavaPlugin plugin)
    {
        mPlugin = plugin;
        mWageFiles = new HashMap<> ();
        mWageConfigs = new HashMap<> ();
    }

    /**
     * This method will register all of the default config files based on the names
     * within the WAGE_NAMES final variable. To be honest it seems quite crude to do
     * it this way, but it should be fine until I get further into the production of
     * the plugin. This means that all of the wage table files that are necessary
     * for running the plugin are created and registered here.
     *
     * TODO:  Create the ability to have custom files created and default files revoked [Future]
     */
    public void registerConfigFiles ()
    {
        for (TableName resource : TableName.values ())
        {
            loadFile (resource.getFileName ());
        }
    }

    /**
     *  This method will create the default configuration file of the plugin CraftyProfessions.
     *  The name of the file should always be config.yml
     */
    public void createDefaultConfig ()
    {
        try
        {
            if (!mPlugin.getDataFolder ().exists ())
            {
                mPlugin.getDataFolder ().mkdirs ();
                mPlugin.getLogger().info ("CraftyProfessions Data Folder has been created");
            }

            mConfigFile = new File (mPlugin.getDataFolder (), "config.yml");

            if (!mConfigFile.exists ())
            {
                mPlugin.getLogger ().info ("config.yml Not Found, Creating File and Loading Defaults!");
                mPlugin.saveDefaultConfig ();
            }
            else
            {
                mPlugin.getLogger ().info ("config.yml Found, Loading File!");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
    }

    /**
     * This method will allow the saving of a single config file based on the file name
     *
     * @param resource The file to find and save
     */
    public void saveConfig (String resource) throws IOException, ConfigNotFoundException
    {
        YamlConfiguration configuration = getSpecialConfig (resource);
        File file = mWageFiles.get (resource);

        if (file == null)
        {
            throw new ConfigNotFoundException ("Could not find config labeled " + resource);
        }

        configuration.save (file);
    }

    /**
     * This method will save the config parameter to the specified
     * resource file.
     *
     * @param config The configuration to save to the resource file
     * @param resource The file name to save the configuration to.
     */
    public void saveConfig (YamlConfiguration config, String resource)
    {
        File file = mWageFiles.get (resource);

        try
        {
            if (file == null)
            {
                throw new ConfigNotFoundException ("Could not find config labeled " + resource);
            }

            config.save (file);
        }
        catch (ConfigNotFoundException e)
        {
            mPlugin.getLogger ().log (Level.WARNING, "Config not found, Cannot Save the Config File to " + resource);
        }
        catch (IOException e)
        {
            mPlugin.getLogger ().log (Level.WARNING, e.getMessage ());
        }
    }

    /**
     * This method will save all of the config files.
     */
    public void saveConfigs ()
    {
        try
        {
            mPlugin.saveConfig ();

            for (TableName resource : TableName.values ())
            {
                saveConfig (resource.getFileName ());
            }
        }
        catch (ConfigNotFoundException e)
        {
            mPlugin.getLogger ().log (Level.WARNING, "Could not save config: ", e.getMessage ());
        }
        catch (IOException e)
        {
            mPlugin.getLogger ().log (Level.WARNING, e.getMessage ());
        }

    }

    /**
     * This method will check to see if the file name passed in as the parameter
     * is already created or not, and if not it will save the file resource inside
     * of the plugin directory, otherwise it will return the file that was created
     * without putting it within the plugin dir.
     *
     * @param resource The name of the Configuration File to generate
     */
    private void loadFile (String resource)
    {
        File resourceFile = new File (mPlugin.getDataFolder (), resource);

        if (!resourceFile.exists ())
        {
            mPlugin.saveResource ("wage_data/" + resourceFile.getName (), false);
        }

        mWageFiles.put (resource, resourceFile);
    }

    /**
     * This method essentially imitates the getConfig method within the JavaPlugin class
     * but is used to create or grab special config files pertaining to the Wage Tables of
     * the plugin
     *
     * @param resource The file or resource to grab the Configuration for.
     *
     * @return The configuration of the resource.
     */
    public YamlConfiguration getSpecialConfig (String resource)
    {
        YamlConfiguration config = mWageConfigs.get (resource);

        if (config == null)
        {
            InputStream configStream = mPlugin.getResource (resource);
            config = YamlConfiguration.loadConfiguration (mWageFiles.get (resource));

            if (configStream != null)
            {
                config.setDefaults (YamlConfiguration.loadConfiguration (new InputStreamReader (configStream, Charsets.UTF_8)));
            }

            mWageConfigs.put (resource, config);
        }

        return config;
    }

    /**
     * <p>Returns if the config option for Debug mode is true</p>
     *
     * @return true  - If Debug is on
     *         false - If Debug is off
     */
    public boolean getDebug ()
    {
        return getBoolean ("Debug");
    }

    /***************************************************************************
     *                |--------------------------------|
     *                |--- Yaml Configuration Files ---|
     *                |--------------------------------|
     *
     * The following methods are defined as the Spigot implementation for
     * obtaining values for the configuration files.
     *
     *
     **************************************************************************/
    public Integer getInt (String path)
    {
        return mPlugin.getConfig ().getInt (path);
    }

    public Double getDouble (String path)
    {
        return mPlugin.getConfig ().getDouble (path);
    }

    public String getString (String path)
    {
        return mPlugin.getConfig().getString (path);
    }

    public boolean getBoolean (String path)
    {
        return mPlugin.getConfig ().getBoolean (path);
    }
}
