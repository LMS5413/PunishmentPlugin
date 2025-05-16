package com.github.lms5413.punishment.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.github.lms5413.punishment.Punishment
import com.github.lms5413.punishment.enums.PunishmentTypes
import com.github.lms5413.punishment.models.PunishmentModel
import com.github.lms5413.punishment.utils.kickPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("ipban")
@CommandPermission("punishment.ipban")
class IpbanCommand: BaseCommand() {

    @Default
    @Syntax("<player> - The player to ban <reason> - The reason for the ban")
    fun onIpbanCommand(player: CommandSender, @Single target: String, @Optional reason: String? = "No reason provided") {
        val target = Bukkit.getOfflinePlayer(target)
        val reason = reason ?: "No reason provided"

        val punishmentHistory = Punishment.getInstance().getPunishmentManager().getActivePunishment(target.uniqueId.toString(), true)
        if (punishmentHistory != null) {
            player.sendMessage("§cThis player have punishment active.")
            return
        }

        if (target.isOnline) {
            val targetPlayer = Bukkit.getPlayer(target.uniqueId)!!
            kickPlayer(targetPlayer, "messages.permanent-banned", player, reason)
        }

        val loginHistory = Punishment.getInstance().getLoginHistoryManager().getLoginHistory(target.uniqueId.toString())
        if (loginHistory == null) {
            player.sendMessage("§cNo login history found for this player.")
            return
        }

        Punishment.getInstance().getPunishmentManager().addPunishment(
            PunishmentModel(
                id = 0,
                uuid = target.uniqueId.toString(),
                type = PunishmentTypes.BAN,
                reason = reason,
                author = player.name,
                ip = loginHistory.ip,
            )
        )

        player.sendMessage("§aYou have banned ${target.name} for $reason")

    }
}