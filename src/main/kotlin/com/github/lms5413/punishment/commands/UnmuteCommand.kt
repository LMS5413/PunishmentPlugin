package com.github.lms5413.punishment.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.github.lms5413.punishment.Punishment
import com.github.lms5413.punishment.enums.PunishmentTypes
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("unmute")
@CommandPermission("punishment.unmute")
class UnmuteCommand: BaseCommand() {

    @Default
    @Syntax("<player> - The player to unmute")
    fun onUnmuteCommand(player: CommandSender, @Single target: String) {
        val target = Bukkit.getOfflinePlayer(target)

        val punishmentHistory = Punishment.getInstance().getPunishmentManager().getActivePunishment(target.uniqueId.toString(), true)
        if (punishmentHistory == null || punishmentHistory.type != PunishmentTypes.MUTE) {
            player.sendMessage("§cThis player is not silenced.")
            return
        }

        punishmentHistory.isActive = false
        Punishment.getInstance().getPunishmentManager().updatePunishment(punishmentHistory)

        player.sendMessage("§aYou have unsilenced ${target.name}")
    }
}