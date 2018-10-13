package com.triangl.processing.repository

import com.triangl.processing.helper.ResultSetMapper
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class RepositoryConnector {

    var connection: Connection

    init {
        val instanceConnectionName = "triangl-215714:europe-west3:test"

        val databaseName = "serving-db"

        val username = "root"
        val password = ""

        //[START doc-example]
        val jdbcUrl = String.format(
            "jdbc:mysql://google/%s?cloudSqlInstance=%s" + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
            databaseName,
            instanceConnectionName
        )

        connection = DriverManager.getConnection(jdbcUrl, username, password)
    }

    inline fun <reified T>get(query: String) =
            run<T>(false, query)

    inline fun <reified T>write(query: String) =
            run<T>(true, query)

    @Throws(IOException::class, SQLException::class)
    inline fun <reified T>run(isWrite: Boolean, query: String): List<T> {
        connection.createStatement().use { statement ->
             return if (isWrite) {
                statement.executeUpdate(query)
                emptyList()
            } else {
                val resultSet = statement.executeQuery(query)
                val mapper = ResultSetMapper<T>()
                return mapper.mapResultSetToObject(resultSet, T::class.java).toList()
            }
        }
    }
}