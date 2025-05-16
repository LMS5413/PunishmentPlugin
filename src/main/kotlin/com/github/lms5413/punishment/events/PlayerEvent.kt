package com.github.lms5413.punishment.events

import com.github.lms5413.punishment.Punishment
import com.github.lms5413.punishment.enums.PunishmentTypes
import com.github.lms5413.punishment.models.LoginHistoryModel
import com.github.lms5413.punishment.utils.buildMessage
import com.github.lms5413.punishment.utils.kickPlayer
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent

class PlayerEvent: Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val punishmentInfo = Punishment.getInstance().getPunishmentManager().getActivePunishment(event.player.uniqueId.toString(), true) ?:
            Punishment.getInstance().getPunishmentManager().getActivePunishmentByIp(event.player.address.address.hostAddress.toString())
        if (punishmentInfo != null && punishmentInfo.type == PunishmentTypes.BAN) {
            println(punishmentInfo.timeout)
            kickPlayer(
                event.player,
                if (punishmentInfo.timeout != null && punishmentInfo.timeout!! > 0)
                    "messages.temp-banned" else "messages.permanent-banned",
                event.player,
                punishmentInfo.reason,
                if (punishmentInfo.timeout != null && punishmentInfo.timeout!! > 0)
                    punishmentInfo.timeout!! - System.currentTimeMillis() else null
            )

            return
        }

        val historyLogin = Punishment.getInstance().getLoginHistoryManager().getLoginHistory(event.player.uniqueId.toString())
        val model = LoginHistoryModel(
            id = 0,
            uuid = event.player.uniqueId.toString(),
            ip = event.player.address?.address?.hostAddress,
            lastLogin = System.currentTimeMillis()
        )

        if (historyLogin == null) Punishment.getInstance().getLoginHistoryManager().addLoginHistory(model)
        Punishment.getInstance().getLoginHistoryManager().updateLoginHistory(model)
    }

    @EventHandler
    fun onPlayerSendChat(event: AsyncChatEvent) {
        val punishmentInfo = Punishment.getInstance().getPunishmentManager().getActivePunishment(event.player.uniqueId.toString())
        if (punishmentInfo != null && punishmentInfo.type == PunishmentTypes.MUTE) {
            event.isCancelled = true
            event.player.sendMessage(buildMessage(
                if (punishmentInfo.timeout != null && punishmentInfo.timeout!! > 0)
                    "messages.temp-mute" else "messages.permanent-mute",
                event.player,
                punishmentInfo.reason,
                punishmentInfo.timeout
            ))
            return
        }
    }
}