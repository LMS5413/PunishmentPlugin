package com.github.lms5413.punishment.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.github.lms5413.punishment.Punishment
import com.github.lms5413.punishment.enums.PunishmentTypes
import com.github.lms5413.punishment.models.PunishmentModel
import com.github.lms5413.punishment.utils.convertStringToTime
import com.github.lms5413.punishment.utils.kickPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("tempban")
@CommandPermission("punishment.tempban")
class TempbanCommand: BaseCommand() {

    @Default
    @Syntax("<player> - The player to ban <time> - Time for ban (ex 7d) <reason> - The reason for the ban")
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
            kickPlayer(targetPlayer, "messages.temp-banned", player, reason, time)
        }

        Punishment.getInstance().getPunishmentManager().addPunishment(
            PunishmentModel(
                id = 0,
                uuid = target.uniqueId.toString(),
                type = PunishmentTypes.BAN,
                reason = reason,
                author = player.name,
                ip = null,
                timeout = System.currentTimeMillis() + time,
            )
        )

        player.sendMessage("§aYou have banned ${target.name} for $reason")

    }
}