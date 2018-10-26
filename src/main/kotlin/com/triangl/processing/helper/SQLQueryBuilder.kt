package com.triangl.processing.helper

import com.triangl.processing.outputEntity.MapOutput
import com.triangl.processing.outputEntity.RouterOutput
import com.triangl.processing.repository.RepositoryEntity
import java.text.SimpleDateFormat
import java.util.*


class SQLQueryBuilder {

    var table: String? = null

    fun select(id: String) =
        "SELECT * FROM $table WHERE id=\"$id\""

    fun insert(data: HashMap<String, Any?>): String {
        val filteredData = data.filter { it.value != null }
        val keys = filteredData.map { it.key }.joinToString(", ")
        val values = filteredData.map { formatValueForQuery(it.value) }.joinToString(", ")
        return "INSERT INTO $table ($keys) VALUES ($values)"
    }

    fun update(data: HashMap<String, Any?>) =
        "UPDATE $table SET ${data.filter { it.key != "id" }.map { "${it.key}=${formatValueForQuery(it.value)}" }.joinToString(", ")}"

    fun delete(id: String) =
        "DELETE FROM $table WHERE id=\"$id\""

    fun <T: RepositoryEntity>deleteNotIn(data: List<T>): String? {
        val notInIdClause = constructINClause(data.map { it.id })
        var preQuery: String? = null

        val foreignKeyClause: String = when (table) {
            "Map" -> {
                data as List<MapOutput>
                "customerId=\"${data[0].customerId}\""
            }
            "Router" -> {
                data as List<RouterOutput>
                val foreignKeyClause = "mapId=\"${data[0].mapId}\""
                val deleteCoordinateIds = "SELECT coordinateId FROM $table WHERE id NOT IN ($notInIdClause) AND $foreignKeyClause"
                preQuery = "DELETE FROM Coordinate WHERE id IN ($deleteCoordinateIds))"
                foreignKeyClause
            }
            else -> {
                // TODO raise exception
                return null
            }
        }

        val mainQuery = "DELETE FROM $table WHERE id NOT IN ($notInIdClause) AND $foreignKeyClause"
        return if (preQuery != null) {
            "$preQuery; $mainQuery;"
        } else {
            mainQuery
        }
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