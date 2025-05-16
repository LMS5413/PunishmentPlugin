package com.github.lms5413.punishment.tasks

import com.github.lms5413.punishment.Punishment

class PunishmentTempTask: Runnable {
    override fun run() {
        val punishments = Punishment.getInstance().getPunishmentManager().getPunishments().filter {
            it.isActive && it.timeout != null && it.timeout!! > 0
        }

        for (punishment in punishments) {
            if (System.currentTimeMillis() >= punishment.timeout!!) {
                punishment.isActive = false
                Punishment.getInstance().getPunishmentManager().updatePunishment(punishment)
            }
        }
    }
}