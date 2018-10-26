package com.triangl.processing.repository

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.helper.SQLQueryBuilder

class RepositoryExecutor (
    private var repositoryConnector: RepositoryConnector,
    private var sqlQueryBuilder: SQLQueryBuilder?
) {
    constructor(repositoryConnector: RepositoryConnector) :
        this(repositoryConnector, null)

    fun <T: RepositoryEntity>run (operation: OutputOperationDto<*>, table: String, outputClass: Class<T>) {
        if (sqlQueryBuilder == null) {
            sqlQueryBuilder = SQLQueryBuilder(table)
        }
        operation.data.forEach{item ->
            val castedItem = item as T
            if (
                operation.type == OutputOperationTypeDto.APPLY ||
                operation.type == OutputOperationTypeDto.APPLY_AND_CLEAR
            ) {
                apply(castedItem, outputClass)
            } else if (operation.type == OutputOperationTypeDto.DELETE) {
                delete(castedItem, outputClass)
            }
        }
        if (operation.type == OutputOperationTypeDto.APPLY_AND_CLEAR) {
            val query = sqlQueryBuilder!!.deleteNotIn(operation.data)
            if (query != null) {
                repositoryConnector.modify(query, outputClass)
            }
        }
    }

    fun <T: RepositoryEntity>apply (data: T, outputClass: Class<T>) {
        if (isExisting(data, outputClass)) {
            update(data, outputClass)
        } else {
            create(data, outputClass)
        }
    }
    
    private fun <T: RepositoryEntity>isExisting (data: T, outputClass: Class<T>): Boolean {
        val query = sqlQueryBuilder!!.select(data.id)
        System.out.printf("$query\n")
        val result = repositoryConnector.get(query, outputClass)
        return result.isNotEmpty()
    }

    private fun <T: RepositoryEntity>create (data: T, outputClass: Class<T>) {
        val query = sqlQueryBuilder!!.insert(data.toHashMap())
        System.out.printf("$query\n")
        repositoryConnector.modify(query, outputClass)
    }

    private fun <T: RepositoryEntity>update (data: T, outputClass: Class<T>) {
        val query = sqlQueryBuilder!!.update(data.toHashMap())
        System.out.printf("$query\n")
        repositoryConnector.modify(query, outputClass)
    }

    private fun <T: RepositoryEntity>delete (data: T, outputClass: Class<T>) {
        val query = sqlQueryBuilder!!.delete(data.id)
        repositoryConnector.modify(query, outputClass)
        System.out.printf("$query\n")
    }
}