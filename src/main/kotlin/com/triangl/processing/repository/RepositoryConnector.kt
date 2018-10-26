package com.triangl.processing.repository

import com.triangl.processing.helper.ResultSetMapper
import java.io.IOException
import java.sql.Connection
import java.sql.SQLException

class RepositoryConnector (
    private var connection: Connection
) {
    fun <T>get(query: String, outputClass: Class<T>) =
        run(false, query, outputClass)

    fun <T>modify(query: String, outputClass: Class<T>) =
        run(true, query, outputClass)

    @Throws(IOException::class, SQLException::class)
    fun <T>run(isWrite: Boolean, query: String, outputClass: Class<T>): List<T> {
        connection.createStatement().use { statement ->
             return if (isWrite) {
                statement.executeUpdate(query)
                emptyList()
            } else {
                val resultSet = statement.executeQuery(query)
                val mapper = ResultSetMapper<T>()
                return mapper.mapResultSetToObject(resultSet, outputClass).toList()
            }
        }
    }
}