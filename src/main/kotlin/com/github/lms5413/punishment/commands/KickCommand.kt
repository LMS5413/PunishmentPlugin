package com.github.lms5413.punishment.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.github.lms5413.punishment.utils.kickPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("kick")
@CommandPermission("punishment.kick")
class KickCommand: BaseCommand() {

    @Default
    @Syntax("<player> - The player to kick <reason> - The reason for the kick")
    fun onKickCommand(player: CommandSender, @Single target: String, @Optional reason: String? = "No reason provided") {
        val target = Bukkit.getPlayer(target)
        val reason = reason ?: "No reason provided"

        if (target == null) {
            player.sendMessage("§cPlayer not found.")
            return
        }

        kickPlayer(target, "messages.kick", player, reason)
        player.sendMessage("§aYou have kicked ${target.name} for $reason")

    }
}