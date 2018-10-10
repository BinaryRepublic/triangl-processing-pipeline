package com.triangl.processing.controller

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.repository.*

class RepositoryController (
    private val outputOperation: OutputOperationDto<*>
) {

    fun applyOutputOperations () {

        when (outputOperation.type) {
            OutputOperationTypeDto.APPLY -> {
                runParents()
                runSelf()
                runChildren()
            }
            OutputOperationTypeDto.APPLY_AND_CLEAR -> {
                runParents()
                runSelf()
                runChildren()
            }
            OutputOperationTypeDto.DELETE -> {
                runChildren()
                runSelf()
                runParents()
            }
        }
    }

    private fun runParents () {
        if (outputOperation.parents.isNotEmpty()) {
            outputOperation.parents.forEach {
                RepositoryController(it).applyOutputOperations()
            }
        }
    }

    private fun runChildren () {
        if (outputOperation.children.isNotEmpty()) {
            outputOperation.children.forEach {
                RepositoryController(it).applyOutputOperations()
            }
        }
    }

    private fun runSelf () {
        runRepositoryOperation(outputOperation)
    }

    private fun runRepositoryOperation (outputOperation: OutputOperationDto<*>) {
        val repository = when (outputOperation.entity) {
            OutputOperationEntityDto.CUSTOMER -> RepositoryHandler(CustomerRepository())
            OutputOperationEntityDto.MAP -> RepositoryHandler(MapRepository())
            OutputOperationEntityDto.ROUTER -> RepositoryHandler(RouterRepository())
            OutputOperationEntityDto.COORDINATE -> RepositoryHandler(CoordinateRepository())
            OutputOperationEntityDto.TRACKED_DEVICE -> RepositoryHandler(TrackedDeviceRepository())
            OutputOperationEntityDto.TRACKING_POINT -> RepositoryHandler(TrackingPointRepository())
        }

        repository.run(outputOperation)
    }
}