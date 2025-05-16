package com.github.lms5413.punishment.database.tables

import org.jetbrains.exposed.sql.Table

object LoginHistoryTable : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val uuid = varchar("uuid", 36)
    val ip = varchar("ip", 15).nullable()
    val lastLogin = long("last_login")

    override val primaryKey = PrimaryKey(id)
}