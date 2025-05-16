package com.github.lms5413.punishment.database

import com.github.lms5413.punishment.Punishment
import com.github.lms5413.punishment.database.tables.LoginHistoryTable
import com.github.lms5413.punishment.database.tables.PunishmentsTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnector {

    private lateinit var connection: Database

    fun connectDatabase() {

        val databaseConfig = Punishment.getInstance().config.getConfigurationSection("database")

        val type = databaseConfig?.getString("type")?.lowercase() ?: "sqlite"
        val host = databaseConfig?.getString("host") ?: "localhost"
        val port = databaseConfig?.getInt("port") ?: 3306
        val database = databaseConfig?.getString("database") ?: "dbname"
        val username = databaseConfig?.getString("username") ?: "username"
        val password = databaseConfig?.getString("password") ?: "password"
        val connectionPool = databaseConfig?.getInt("pools") ?: 6

        if (type == "sqlite") connectToSqlite()
        else {

            Class.forName("org.mariadb.jdbc.Driver")

            val databaseConfigHikari = HikariConfig().apply {
                jdbcUrl = "jdbc:mariadb://$host:$port/$database"
                driverClassName = "org.mariadb.jdbc.Driver"
                username
                password
                maximumPoolSize = connectionPool
                transactionIsolation = "TRANSACTION_SERIALIZABLE"
            }

            try {
                connection = Database.connect(HikariDataSource(databaseConfigHikari))
            } catch (e: Exception) {
                Punishment.getInstance().logger.severe("Failed to connect to the database. Using SQLITE")
                e.printStackTrace()
                connectToSqlite()
            }
        }

        try {
            transaction {
                SchemaUtils.create(PunishmentsTable)
                SchemaUtils.create(LoginHistoryTable)
            }
        } catch (e: Exception) {
            Punishment.getInstance().logger.severe("Failed to create the table. Please check your database.")
            e.printStackTrace()
        }
    }

    fun getConnection(): Database {
        return connection
    }

    fun close() {
        if (::connection.isInitialized) {
            connection.connector().close()
        }
    }

    private fun connectToSqlite() {

        val databaseConfigHikari = HikariConfig()

        databaseConfigHikari.jdbcUrl = "jdbc:sqlite:${Punishment.getInstance().dataFolder}/punishment.db"
        databaseConfigHikari.driverClassName = "org.sqlite.JDBC"
        databaseConfigHikari.maximumPoolSize = 1

        connection = Database.connect(HikariDataSource(databaseConfigHikari))
    }
}