package com.triangl.processing.repository

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.helper.SQLQueryBuilder

class RepositoryExecutor (
    private var repositoryConnector: RepositoryConnector,
    private var sqlQueryBuilder: SQLQueryBuilder
) {
    fun <T: RepositoryEntity>run (operation: OutputOperationDto<*>, table: String, outputClass: Class<T>) {

        operation.data.forEach{item ->
            val castedItem = item as T
            if (
                operation.type == OutputOperationTypeDto.APPLY ||
                operation.type == OutputOperationTypeDto.APPLY_AND_CLEAR
            ) {
                updateOrCreate(castedItem, table, outputClass)
            } else if (operation.type == OutputOperationTypeDto.DELETE) {
                delete(castedItem, table, outputClass)
            }
        }
        if (operation.type == OutputOperationTypeDto.APPLY_AND_CLEAR) {
            val queries = sqlQueryBuilder.deleteNotIn(operation.data, table)
            if (queries.isNotEmpty()) {
                queries.forEach{query ->
                    System.out.printf("$query\n")
                    repositoryConnector.modify(query, outputClass)
                }
            }
        }
    }

    private fun <T: RepositoryEntity>updateOrCreate (data: T, table: String, outputClass: Class<T>) {
        val query = sqlQueryBuilder.insertOrUpdate(data.toHashMap(), table)
        System.out.printf("$query\n")
        repositoryConnector.modify(query, outputClass)
    }

    private fun <T: RepositoryEntity>delete (data: T, table: String, outputClass: Class<T>) {
        val query = sqlQueryBuilder.delete(data.id, table)
        System.out.printf("$query\n")
        repositoryConnector.modify(query, outputClass)
    }
}