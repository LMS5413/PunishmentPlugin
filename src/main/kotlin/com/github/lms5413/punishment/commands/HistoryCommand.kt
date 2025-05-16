package com.github.lms5413.punishment.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.github.lms5413.punishment.gui.PunishmentHistoryMainGui
import org.bukkit.entity.Player

@CommandAlias("history")
@CommandPermission("punishment.history")
class HistoryCommand: BaseCommand() {

    @Default
    fun onHistoryCommand(player: Player) {
        val historyGui = PunishmentHistoryMainGui()
        historyGui.openInventory(player)
    }
}