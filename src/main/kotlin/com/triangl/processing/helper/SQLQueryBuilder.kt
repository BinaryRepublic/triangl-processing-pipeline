package com.triangl.processing.helper

import com.triangl.processing.repository.RepositoryEntity
import java.text.SimpleDateFormat
import java.util.*


class SQLQueryBuilder {

    fun insert(data: HashMap<String, Any?>, table: String): String {
        val filteredData = data.filter { it.value != null }
        val keys = filteredData.map { it.key }.joinToString(", ")
        val values = filteredData.map { formatValueForQuery(it.value) }.joinToString(", ")
        return "INSERT INTO $table ($keys) VALUES ($values)"
    }

    fun insertOrUpdate(data: HashMap<String, Any?>, table: String): String {
        val insertQuery = insert(data, table)
        val updateQuery = "UPDATE ${data.filter { it.key != "id" }.map { "${it.key}=VALUES(${it.key})" }.joinToString(", ")}"
        return "$insertQuery ON DUPLICATE KEY $updateQuery"
    }

    fun delete(id: String, table: String) =
        "DELETE FROM $table WHERE id=\"$id\""

    fun <T: RepositoryEntity>deleteNotIn(data: List<T>, table: String): String {
        val foreignKeyClearClause = data[0].getForeignKeyClearClause()
        if (foreignKeyClearClause == null) {
            ExceptionHandler.throwClearNotSupported(table)
        }
        val notInIdClause = constructINClause(data.map { it.id })
        return "DELETE FROM $table WHERE id NOT IN ($notInIdClause) AND ${data[0].getForeignKeyClearClause()!!}"
    }

    fun formatValueForQuery(value: Any?): Any? {
        return when (value) {
            is String -> "\"$value\""
            is Date -> {
                val timestamp = java.sql.Timestamp(value.time)
                val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                return "\"${sdf.format(timestamp)}\""
            }
            else -> value
        }
    }

    fun constructINClause(values: List<String>) =
        values.map { "\"$it\"" }.joinToString(", ")
}