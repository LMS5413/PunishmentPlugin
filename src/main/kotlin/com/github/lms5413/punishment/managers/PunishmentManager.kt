package com.github.lms5413.punishment.managers

import com.github.lms5413.punishment.database.tables.PunishmentsTable
import com.github.lms5413.punishment.models.PunishmentModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PunishmentManager {

    private val punishments = mutableListOf<PunishmentModel>()

    init {
        loadPunishments()
    }

    fun getPunishment(uuid: String, force: Boolean = false): PunishmentModel? {
        return punishments.find { it.uuid == uuid }
            ?: if (force) loadPunishmentFromDatabase(uuid, false)
            else null
    }

    fun getPunishments(): List<PunishmentModel> {
        return punishments
    }

    fun getActivePunishment(uuid: String, force: Boolean = false): PunishmentModel? {
        return punishments.find { it.uuid == uuid && it.isActive }
            ?: if (force) loadPunishmentFromDatabase(uuid, true)
            else null
    }

    fun getActivePunishmentByIp(ip: String): PunishmentModel? {
        return punishments.firstOrNull { it.ip == ip && it.isActive }
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
                it[name] = punishment.name
            }

            punishment.id = result[PunishmentsTable.id]
            punishments[punishment.id] = punishment
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

            punishments[punishments.indexOfFirst { it.uuid == punishment.uuid }] = punishment
        }
    }

    fun loadPunishments() {
        transaction {
            val users = PunishmentsTable.selectAll()
            for (user in users) {
                val punishment = mapRowToPunishment(user)

                punishments.add(punishment)
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
            timeout = row[PunishmentsTable.timeout],
            name = row[PunishmentsTable.name] ?: "Unknown"
        )
    }

    private fun loadPunishmentFromDatabase(uuid: String, onlyActive: Boolean = false): PunishmentModel? {
        return transaction {
            val query = PunishmentsTable.selectAll().where {
                (PunishmentsTable.uuid eq uuid) andIfNotNull (if (onlyActive) (PunishmentsTable.isActive eq true) else null)
            }.firstOrNull()

            query?.let {
                val model = mapRowToPunishment(it)
                punishments.add(model)
                model
            }
        }
    }
}