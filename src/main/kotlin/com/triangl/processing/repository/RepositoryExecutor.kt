package com.triangl.processing.repository

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.helper.SQLQueryBuilder

class RepositoryExecutor (
    private var repositoryConnector: RepositoryConnector,
    private var sqlQueryBuilder: SQLQueryBuilder
) {
    fun <T: RepositoryEntity>run (operation: OutputOperationDto<*>, table: String) {

        operation.data.forEach{item ->
            @Suppress("UNCHECKED_CAST")
            val castedItem = item as T
            if (
                operation.type == OutputOperationTypeDto.APPLY ||
                operation.type == OutputOperationTypeDto.APPLY_AND_CLEAR
            ) {
                updateOrCreate(castedItem, table)
            } else if (operation.type == OutputOperationTypeDto.DELETE) {
                delete(castedItem, table)
            }
        }
        if (operation.type == OutputOperationTypeDto.APPLY_AND_CLEAR) {
            val query = sqlQueryBuilder.deleteNotIn(operation.data, table)
            System.out.printf("$query\n")
            repositoryConnector.execute(query)
        }
    }

    private fun <T: RepositoryEntity>updateOrCreate (data: T, table: String) {
        val query = sqlQueryBuilder.insertOrUpdate(data.toHashMap(), table)
        System.out.printf("$query\n")
        repositoryConnector.execute(query)
    }

    private fun <T: RepositoryEntity>delete (data: T, table: String) {
        val query = sqlQueryBuilder.delete(data.id, table)
        System.out.printf("$query\n")
        repositoryConnector.execute(query)
    }
}