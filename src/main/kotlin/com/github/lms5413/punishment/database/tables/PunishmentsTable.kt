package com.github.lms5413.punishment.database.tables

import com.github.lms5413.punishment.enums.PunishmentTypes
import org.jetbrains.exposed.sql.Table


object PunishmentsTable : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val uuid = varchar("uuid", 36)
    val ip = varchar("ip", 15).nullable()
    val type = enumeration<PunishmentTypes>("type")
    val timeout = long("timeout").nullable().default(0)
    val reason = text("reason").nullable()
    val isActive = bool("is_active").default(true)
    val author = varchar("author", 16)


    override val primaryKey = PrimaryKey(id)
}