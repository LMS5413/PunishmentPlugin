package com.github.lms5413.punishment.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.github.lms5413.punishment.Punishment
import com.github.lms5413.punishment.enums.PunishmentTypes
import com.github.lms5413.punishment.models.PunishmentModel
import com.github.lms5413.punishment.utils.buildMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("mute")
@CommandPermission("punishment.mute")
class MuteCommand: BaseCommand() {

    @Default
    @Syntax("<player> - The player to mute <reason> - The reason for the mute")
    fun onMuteCommand(player: CommandSender, @Single target: String, @Optional reason: String? = "No reason provided") {
        val target = Bukkit.getOfflinePlayer(target)
        val reason = reason ?: "No reason provided"

        val punishmentHistory = Punishment.getInstance().getPunishmentManager().getActivePunishment(target.uniqueId.toString(), true)
        if (punishmentHistory != null) {
            player.sendMessage("§cThis player have punishment active.")
            return
        }

        buildMessage("messages.permanent-mute", player, reason)

        Punishment.getInstance().getPunishmentManager().addPunishment(
            PunishmentModel(
                id = 0,
                uuid = target.uniqueId.toString(),
                type = PunishmentTypes.MUTE,
                reason = reason,
                author = player.name,
                ip = null,
            )
        )

        player.sendMessage("§aYou have silenced ${target.name} for $reason")

    }
}