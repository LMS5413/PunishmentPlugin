package com.github.lms5413.punishment.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.github.lms5413.punishment.Punishment
import com.github.lms5413.punishment.enums.PunishmentTypes
import com.github.lms5413.punishment.models.PunishmentModel
import com.github.lms5413.punishment.utils.buildMessage
import com.github.lms5413.punishment.utils.convertStringToTime
import com.github.lms5413.punishment.utils.kickPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("tempmute")
@CommandPermission("punishment.tempmute")
class TempmuteCommand: BaseCommand() {

    @Default
    @Syntax("<player> - The player to mute <time> - Time for mute (ex 7d) <reason> - The reason for the mute")
    fun onTempbanCommand(player: CommandSender, @Single target: String, @Single time: String, @Optional reason: String? = "No reason provided") {
        val target = Bukkit.getOfflinePlayer(target)
        val reason = reason ?: "No reason provided"

        val punishmentHistory = Punishment.getInstance().getPunishmentManager().getActivePunishment(target.uniqueId.toString(), true)
        if (punishmentHistory != null) {
            player.sendMessage("§cThis player have punishment active.")
            return
        }

        val time = convertStringToTime(time)
        if (time == null) {
            player.sendMessage("§cInvalid time format. Use 7d for 7 days, 2h for 2 hours, etc.")
            return
        }

        if (target.isOnline) {
            val targetPlayer = Bukkit.getPlayer(target.uniqueId)!!
            targetPlayer.sendMessage(buildMessage("messages.temp-mute", player, reason, time))
        }

        Punishment.getInstance().getPunishmentManager().addPunishment(
            PunishmentModel(
                id = 0,
                uuid = target.uniqueId.toString(),
                type = PunishmentTypes.MUTE,
                reason = reason,
                author = player.name,
                ip = null,
                timeout = System.currentTimeMillis() + time,
            )
        )

        player.sendMessage("§aYou have banned ${target.name} for $reason")

    }
}