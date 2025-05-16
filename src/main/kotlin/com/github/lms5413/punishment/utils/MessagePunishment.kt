package com.github.lms5413.punishment.utils

import com.github.lms5413.punishment.Punishment
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun kickPlayer(player: Player, path: String, author: CommandSender, reason: String? = "No reason provided", time: Long? = null) {

    val message = buildMessage(path, author, reason, time)

    if (player.isOnline) {
        player.kick(message)
    }
}

fun buildMessage(path: String, author: CommandSender, reason: String? = "No reason provided", time: Long? = null): Component {
    val rawMessages = Punishment.getInstance().config.getComponentStringList(path)

    val components = rawMessages.map {
        it.replaceText { text ->
            text.matchLiteral("{author}").replacement(author.name)
        }.replaceText { text ->
            text.matchLiteral("{reason}").replacement(reason ?: "No reason provided")
        }.let { component ->
            if (time != null) {
                val timeString = convertTimeToString(time)
                component.replaceText {
                    it.matchLiteral("{time}").replacement(timeString)
                }
            } else {
                component
            }
        }
    }

    return Component.join(JoinConfiguration.newlines(), components)
}