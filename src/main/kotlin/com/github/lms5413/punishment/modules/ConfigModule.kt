package com.github.lms5413.punishment.modules

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.util.logging.Level

/**
 * Create configuration files with custom file names
 */
class ConfigModule(name: String, private val plugin: Plugin): YamlConfiguration() {
    private val configFile: File = File(plugin.dataFolder, if (name.endsWith(".yml")) name else "$name.yml")

    init {
        loadConfig()
    }

    fun save() {
        try {
            save(configFile)
            reload()
        } catch (e: IOException) {
            plugin.logger
                .log(Level.SEVERE, "There was an error while saving the " + configFile.getName() + " configuration!", e)
        }
    }

    fun reload() {
        loadConfig()
    }

    @Throws(IOException::class, InvalidConfigurationException::class)
    private fun loadConfig() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir()

        if (!configFile.exists()) {
            try {
                plugin.saveResource(configFile.getName(), false)
            } catch (e: IllegalArgumentException) {
                configFile.createNewFile()
            }
        }

        load(configFile)
    }

    /**
     * Gets the requested String by path, returning null if not found.
     *
     * @param path            Path of the String to get.
     * @param translateColors Translate colors from & color codes
     * @return Requested String.
     */

    /**
     * Gets the requested String by path, returning the default value if not found.
     *
     * @param path            Path of the String to get.
     * @param defaultValue    The default value to return if the path is not found or is not a String.
     * @param translateColors Translate colors from & color codes
     * @return Requested String.
     */
    fun getComponentString(path: String, defaultValue: String? = null): Component {
        val value = getRawString(path)
        return MiniMessage.miniMessage().deserialize((value ?: defaultValue) ?: "")
    }

    fun getComponentStringList(path: String): Component {
        val value = getStringList(path)
        return MiniMessage.miniMessage().deserialize(value.joinToString("\n"))
    }

    fun getRawString(path: String): String? {
        return getRawString(path, "")
    }

    fun getRawString(path: String, defaultValue: String, translateColors: Boolean = false): String? {
        return getString(path, defaultValue)?.replace("&", if (translateColors) "§" else "&")
    }
}
