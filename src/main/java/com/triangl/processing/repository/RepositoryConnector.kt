package com.triangl.processing.repository

import com.triangl.processing.helper.ResultSetMapper
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class RepositoryConnector {

    var connection: Connection

    init {
        val env = System.getenv()
        connection = DriverManager.getConnection(env["JDBC_URL"], env["DB_USER"], env["DB_PASSWORD"])
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