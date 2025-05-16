package com.github.lms5413.punishment

import co.aikar.commands.PaperCommandManager
import com.github.lms5413.punishment.commands.*
import com.github.lms5413.punishment.database.DatabaseConnector
import com.github.lms5413.punishment.events.PlayerEvent
import com.github.lms5413.punishment.managers.LoginHistoryManager
import com.github.lms5413.punishment.managers.PunishmentManager
import com.github.lms5413.punishment.modules.ConfigModule
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Punishment : JavaPlugin() {

    companion object {

        private lateinit var instance: Punishment

        @JvmStatic
        fun getInstance(): Punishment {
            return instance
        }
    }

    private lateinit var config: ConfigModule
    private lateinit var database: DatabaseConnector
    private lateinit var punishmentManager: PunishmentManager
    private lateinit var loginHistoryManager: LoginHistoryManager

    override fun onEnable() {
        instance = this
        registerManagers()
        registerCommands()
        registerEvents()

        Bukkit.getConsoleSender().sendMessage("§aThe plugin has been enabled.")
    }

    override fun onDisable() {
        Bukkit.getConsoleSender().sendMessage ("§cThe plugin has been disabled.")
    }

    override fun getConfig(): ConfigModule {
        return config
    }

    fun getPunishmentManager(): PunishmentManager {
        return punishmentManager
    }

    fun getLoginHistoryManager(): LoginHistoryManager {
        return loginHistoryManager
    }

    private fun registerManagers() {
        config = ConfigModule("config.yml", this)

        database = DatabaseConnector()
        database.connectDatabase()

        punishmentManager = PunishmentManager()
        loginHistoryManager = LoginHistoryManager()
    }

    private fun registerEvents() {
        Bukkit.getPluginManager().registerEvents(PlayerEvent(), this)
    }

    private fun registerCommands() {
        val commandContext = PaperCommandManager(this)

        commandContext.registerCommand(BanCommand())
        commandContext.registerCommand(IpbanCommand())
        commandContext.registerCommand(KickCommand())
        commandContext.registerCommand(MuteCommand())
        commandContext.registerCommand(TempbanCommand())
        commandContext.registerCommand(UnbanCommand())
        commandContext.registerCommand(UnmuteCommand())
    }
}
