package com.rayzr522.staffnecessities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;
import com.rayzr522.creativelynamedlib.config.Messages;
import com.rayzr522.staffnecessities.command.CommandStaffNecessities;
import com.rayzr522.staffnecessities.data.SoundManager;

/**
 * @author Rayzr
 */
public class StaffNecessities extends JavaPlugin {
    private static StaffNecessities instance;

    private Messages lang = new Messages();
    private SoundManager sounds = new SoundManager(this);

    @Override
    public void onEnable() {
        instance = this;

        getCommand("staffnecessities").setExecutor(new CommandStaffNecessities(this));

        reload();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    /**
     * (Re)loads all configs from the disk
     */
    public void reload() {
        saveDefaultConfig();
        reloadConfig();

        lang.load(getConfig("messages.yml"));
        sounds.load(getConfig().getConfigurationSection("sound"));

        if (!getFile("sounds.txt").exists()) {
            try {
                Files.write("Available sounds:\n\n" + Arrays.stream(Sound.values())
                        .map(sound -> "- " + sound.name())
                        .collect(Collectors.joining("\n")), getFile("sounds.txt"), Charset.defaultCharset());
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Failed to write to sounds.txt!", e);
            }
        }
    }

    /**
     * If the file is not found and there is a default file in the JAR, it saves the default file to the plugin data folder first
     * 
     * @param path The path to the config file (relative to the plugin data folder)
     * @return The {@link YamlConfiguration}
     */
    public YamlConfiguration getConfig(String path) {
        if (!getFile(path).exists() && getResource(path) != null) {
            saveResource(path, true);
        }
        return YamlConfiguration.loadConfiguration(getFile(path));
    }

    /**
     * Attempts to save a {@link YamlConfiguration} to the disk, and any {@link IOException}s are printed to the console
     * 
     * @param config The config to save
     * @param path The path to save the config file to (relative to the plugin data folder)
     */
    public void saveConfig(YamlConfiguration config, String path) {
        try {
            config.save(getFile(path));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save config", e);
        }
    }

    /**
     * @param path The path of the file (relative to the plugin data folder)
     * @return The {@link File}
     */
    public File getFile(String path) {
        return new File(getDataFolder(), path.replace('/', File.pathSeparatorChar));
    }

    /**
     * Returns a message from the language file
     * 
     * @param key The key of the message to translate
     * @param objects The formatting objects to use
     * @return The formatted message
     */
    public String tr(String key, Object... objects) {
        return lang.tr(key, objects);
    }

    /**
     * Returns a message from the language file without adding the prefix
     * 
     * @param key The key of the message to translate
     * @param objects The formatting objects to use
     * @return The formatted message
     */
    public String trRaw(String key, Object... objects) {
        return lang.trRaw(key, objects);
    }

    /**
     * @return The {@link Messages} instance for this plugin
     */
    public Messages getLang() {
        return lang;
    }

    public SoundManager getSounds() {
        return sounds;
    }

    public static StaffNecessities getInstance() {
        return instance;
    }

}
