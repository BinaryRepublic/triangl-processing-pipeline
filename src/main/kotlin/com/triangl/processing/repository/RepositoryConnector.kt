package com.triangl.processing.repository

import java.io.IOException
import java.sql.Connection
import java.sql.SQLException

class RepositoryConnector (
    private var connection: Connection
) {

    @Throws(IOException::class, SQLException::class)
    fun execute(query: String) {
        connection.createStatement().use { statement ->
            statement.executeUpdate(query)
        }
    }
}