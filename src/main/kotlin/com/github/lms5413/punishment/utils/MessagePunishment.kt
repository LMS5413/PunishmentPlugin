package com.github.lms5413.punishment.utils

import com.github.lms5413.punishment.Punishment
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun kickPlayer(player: Player, path: String, author: CommandSender, reason: String? = "No reason provided", time: Long? = null) {

    val message = buildMessage(path, author, reason, time)

    if (player.isOnline) {
        player.kick(message)
    }
}

fun buildMessage(path: String, author: CommandSender, reason: String? = "No reason provided", time: Long? = null): Component {

    var message = Punishment.getInstance().config.getComponentStringList(path)
        .replaceText {
            it.matchLiteral("{author}").replacement(author.name)
        }
        .replaceText {
            it.matchLiteral("{reason}").replacement(reason ?: "No reason provided")
        }

    if (time != null) {
        val timeString = convertTimeToString(time)
        message = message.replaceText {
            it.matchLiteral("{time}").replacement(timeString)
        }
    }

    return message
}