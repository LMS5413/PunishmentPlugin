package com.github.lms5413.punishment.models

import com.github.lms5413.punishment.enums.PunishmentTypes

class PunishmentModel (
    var id: Int,
    var uuid: String,
    var name: String,
    var ip: String?,
    var type: PunishmentTypes,
    var reason: String?,
    var isActive: Boolean = true,
    var author: String,
    var timeout: Long? = 0L
)