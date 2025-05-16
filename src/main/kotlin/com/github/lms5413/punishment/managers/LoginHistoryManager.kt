package com.github.lms5413.punishment.managers

import com.github.lms5413.punishment.database.tables.LoginHistoryTable
import com.github.lms5413.punishment.models.LoginHistoryModel
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class LoginHistoryManager {

    private val loginHistory = mutableMapOf<String, LoginHistoryModel>()

    init {
        loadLoginHistory()
    }

    fun getLoginHistory(uuid: String): LoginHistoryModel? {
        if (loginHistory.containsKey(uuid)) {
            return loginHistory[uuid]
        }

        transaction {
            val history = LoginHistoryTable.selectAll().where { LoginHistoryTable.uuid eq uuid }.firstOrNull()
            if (history != null) {
                val loginHistoryModel = mapRowToLoginHistory(history)
                loginHistory[uuid] = loginHistoryModel
            }
        }

        return loginHistory[uuid]
    }

    fun addLoginHistory(loginHistoryModel: LoginHistoryModel) {
        transaction {
            val result = LoginHistoryTable.insert {
                it[uuid] = loginHistoryModel.uuid
                it[ip] = loginHistoryModel.ip
                it[lastLogin] = loginHistoryModel.lastLogin
            }

            loginHistoryModel.id = result[LoginHistoryTable.id]
            loginHistory[loginHistoryModel.uuid] = loginHistoryModel
        }
    }

    fun updateLoginHistory(loginHistoryModel: LoginHistoryModel) {
        transaction {
            LoginHistoryTable.update({ LoginHistoryTable.uuid eq loginHistoryModel.uuid }) {
                it[LoginHistoryTable.ip] = ip
                it[LoginHistoryTable.lastLogin] = lastLogin
            }

            if (!loginHistory.containsKey(loginHistoryModel.uuid)) {
                loginHistory[loginHistoryModel.uuid] = loginHistoryModel
                return@transaction
            }

            loginHistory[loginHistoryModel.uuid]!!.lastLogin = loginHistoryModel.lastLogin
            loginHistory[loginHistoryModel.uuid]!!.ip = loginHistoryModel.ip
        }
    }

    fun loadLoginHistory() {
        transaction {
            val users = LoginHistoryTable.selectAll()
            for (user in users) {
                val punishment = mapRowToLoginHistory(user)

                loginHistory[punishment.uuid] = punishment
            }
        }
    }

    private fun mapRowToLoginHistory(row: ResultRow): LoginHistoryModel {
        return LoginHistoryModel(
            id = row[LoginHistoryTable.id],
            uuid = row[LoginHistoryTable.uuid],
            ip = row[LoginHistoryTable.ip],
            lastLogin = row[LoginHistoryTable.lastLogin]
        )
    }


}