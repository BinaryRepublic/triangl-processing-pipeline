package com.triangl.processing.repository

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationTypeDto

class RepositoryHandler<T> (
    private val repository: RepositoryInterface<T>
) {
    fun run (operation: OutputOperationDto<*>) {

        val mapOutputs = operation.data as List<T>

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

    fun apply (data: T) {
        if (repository.isExisting(data)) {
            repository.update(data)
        } else {
            repository.create(data)
        }
    }
}