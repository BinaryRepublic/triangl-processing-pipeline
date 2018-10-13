package com.triangl.processing.repository

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.helper.SQLQueryBuilder

class RepositoryExecutor (
    val dbConnector: RepositoryConnector
) {
    var qb: SQLQueryBuilder? = null

    inline fun <reified T: RepositoryEntity>run (operation: OutputOperationDto<*>, table: String) {
        qb = SQLQueryBuilder(table)
        operation.data.forEach{item ->
            val castedItem = item as T
            if (
                operation.type == OutputOperationTypeDto.APPLY ||
                operation.type == OutputOperationTypeDto.APPLY_AND_CLEAR
            ) {
                apply(castedItem)
            }
        }
    }

    inline fun <reified T: RepositoryEntity>apply (data: T) {
        if (isExisting(data)) {
            update(data)
        } else {
            create(data)
        }
    }
    
    inline fun <reified T: RepositoryEntity>isExisting (data: T): Boolean {
        val query = qb!!.select(data.id)
        System.out.printf("$query\n")
        val result = dbConnector.get<T>(query)
        return result.isNotEmpty()
    }

    inline fun <reified T: RepositoryEntity>create (data: T) {
        val query = qb!!.insert(data.toHashMap())
        System.out.printf("$query\n")
        dbConnector.write<T>(query)
    }

    inline fun <reified T: RepositoryEntity>update (data: T) {
        val query = qb!!.update(data.toHashMap())
        System.out.printf("$query\n")
        dbConnector.write<T>(query)
    }

    inline fun <reified T: RepositoryEntity>delete (data: T) {
        val query = qb!!.update(data.toHashMap())
        dbConnector.write<T>(query)
        System.out.printf("$query\n")
    }
}