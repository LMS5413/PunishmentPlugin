package com.github.lms5413.punishment.managers

import com.github.lms5413.punishment.database.tables.PunishmentsTable
import com.github.lms5413.punishment.models.PunishmentModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PunishmentManager {

    private val punishments = mutableMapOf<String, PunishmentModel>()

    init {
        loadPunishments()
    }

    fun getPunishment(uuid: String, force: Boolean = false): PunishmentModel? {
        return punishments[uuid]
            ?: if (force) loadPunishmentFromDatabase(uuid, false)
            else null
    }

    fun getActivePunishment(uuid: String, force: Boolean = false): PunishmentModel? {
        return punishments[uuid]?.takeIf { it.isActive }
            ?: if (force) loadPunishmentFromDatabase(uuid, true)
            else null
    }

    fun getActivePunishmentByIp(ip: String): PunishmentModel? {
        return punishments.values.firstOrNull { it.ip == ip && it.isActive }
    }

    fun addPunishment(punishment: PunishmentModel) {
        transaction {
            val result = PunishmentsTable.insert {
                it[uuid] = punishment.uuid
                it[ip] = punishment.ip
                it[type] = punishment.type
                it[reason] = punishment.reason
                it[isActive] = punishment.isActive
                it[author] = punishment.author
                it[timeout] = punishment.timeout
            }

            punishment.id = result[PunishmentsTable.id]
            punishments[punishment.uuid] = punishment
        }
    }

    fun updatePunishment(punishment: PunishmentModel) {
        transaction {
            PunishmentsTable.update({ PunishmentsTable.uuid eq punishment.uuid }) {
                it[ip] = punishment.ip
                it[type] = punishment.type
                it[reason] = punishment.reason
                it[isActive] = punishment.isActive
                it[timeout] = punishment.timeout
            }

            punishments[punishment.uuid] = punishment
        }
    }

    fun loadPunishments() {
        transaction {
            val users = PunishmentsTable.selectAll()
            for (user in users) {
                val punishment = mapRowToPunishment(user)

                punishments[punishment.uuid] = punishment
            }
        }
    }

    private fun mapRowToPunishment(row: ResultRow): PunishmentModel {
        return PunishmentModel(
            id = row[PunishmentsTable.id],
            uuid = row[PunishmentsTable.uuid],
            type = row[PunishmentsTable.type],
            reason = row[PunishmentsTable.reason],
            ip = row[PunishmentsTable.ip],
            isActive = row[PunishmentsTable.isActive],
            author = row[PunishmentsTable.author],
            timeout = row[PunishmentsTable.timeout]
        )
    }

    private fun loadPunishmentFromDatabase(uuid: String, onlyActive: Boolean = false): PunishmentModel? {
        return transaction {
            val query = PunishmentsTable.selectAll().where {
                (PunishmentsTable.uuid eq uuid) andIfNotNull (if (onlyActive) (PunishmentsTable.isActive eq true) else null)
            }.firstOrNull()

            query?.let {
                val model = mapRowToPunishment(it)
                punishments[uuid] = model
                model
            }
        }
    }
}