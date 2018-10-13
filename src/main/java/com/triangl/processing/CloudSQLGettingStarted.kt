package com.triangl.processing

import java.io.IOException
import java.sql.DriverManager
import java.sql.SQLException

/**
 * A sample app that connects to a Cloud SQL instance and lists all available tables in a database.
 */
object ListTables {
    @Throws(IOException::class, SQLException::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val instanceConnectionName = "triangl-215714:europe-west3:master"

        val databaseName = "servingDB"

        val username = "root"
        val password = "root"

        //[START doc-example]
        val jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s" + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName)

        val connection = DriverManager.getConnection(jdbcUrl, username, password)
        //[END doc-example]

        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery("SHOW TABLES")
            while (resultSet.next()) {
                println(resultSet.getString(1))
            }
        }
    }
}