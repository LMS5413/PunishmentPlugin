package com.github.lms5413.punishment.models

class LoginHistoryModel (
    var id: Int,
    var uuid: String,
    var ip: String? = null,
    var lastLogin: Long = 0L
)