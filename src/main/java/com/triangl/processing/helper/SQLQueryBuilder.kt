package com.triangl.processing.helper

import java.text.SimpleDateFormat
import java.util.*


class SQLQueryBuilder (
    private val table: String
) {

    fun select(id: String) =
        "SELECT * FROM $table WHERE id='$id'"

    fun insert(data: HashMap<String, Any?>): String {
        val filteredData = data.filter { it.value != null }
        val keys = filteredData.map { it.key }.joinToString(", ")
        val values = filteredData.map { formatValueForQuery(it.value) }.joinToString(", ")
        return "INSERT INTO $table ($keys) VALUES ($values)"
    }

    fun update(data: HashMap<String, Any?>) =
        "UPDATE $table SET ${data.filter { it.key != "id" }.map { "${it.key}=${formatValueForQuery(it.value)}" }.joinToString(", ")}"

    fun delete(id: String) =
        "DELETE FROM $table WHERE id='$id'"

    private fun formatValueForQuery(value: Any?): Any? {
        return when (value) {
            is String -> "\"$value\""
            is Date -> {
                val timestamp = java.sql.Timestamp(value.time)
                val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                return "\"${sdf.format(timestamp)}\""
            }
            else -> value
        }
    }
}