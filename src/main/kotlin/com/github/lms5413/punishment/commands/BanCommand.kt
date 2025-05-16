package com.github.lms5413.punishment.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.github.lms5413.punishment.Punishment
import com.github.lms5413.punishment.enums.PunishmentTypes
import com.github.lms5413.punishment.models.PunishmentModel
import com.github.lms5413.punishment.utils.kickPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("ban")
@CommandPermission("punishment.ban")
class BanCommand: BaseCommand() {

    @Default
    @Syntax("<player> - The player to ban <reason> - The reason for the ban")
    fun onBanCommand(player: CommandSender, @Single target: String, @Optional reason: String? = "No reason provided") {
        val targetPlayer = Bukkit.getOfflinePlayer(target)
        val reason = reason ?: "No reason provided"

        val punishmentHistory = Punishment.getInstance().getPunishmentManager().getActivePunishment(targetPlayer.uniqueId.toString(), true)
        if (punishmentHistory != null) {
            player.sendMessage("§cThis player have punishment active.")
            return
        }

        if (targetPlayer.isOnline) {
            val targetPlayer = Bukkit.getPlayer(targetPlayer.uniqueId)!!
            kickPlayer(targetPlayer, "messages.permanent-banned", player, reason)
        }

        Punishment.getInstance().getPunishmentManager().addPunishment(
            PunishmentModel(
                id = 0,
                uuid = targetPlayer.uniqueId.toString(),
                type = PunishmentTypes.BAN,
                reason = reason,
                author = player.name,
                ip = null,
                name = target
            )
        )

        player.sendMessage("§aYou have banned ${targetPlayer.name} for $reason")

    }
}