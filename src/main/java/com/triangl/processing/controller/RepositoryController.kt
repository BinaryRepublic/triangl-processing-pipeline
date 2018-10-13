package com.triangl.processing.controller

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.outputEntity.*
import com.triangl.processing.repository.RepositoryConnector
import com.triangl.processing.repository.RepositoryExecutor

class RepositoryController (
    private val outputOperation: OutputOperationDto<*>,
    private val dbConnector: RepositoryConnector
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
                RepositoryController(it, dbConnector).applyOutputOperations()
            }
        }
    }

    private fun runChildren () {
        if (outputOperation.children.isNotEmpty()) {
            outputOperation.children.forEach {
                RepositoryController(it, dbConnector).applyOutputOperations()
            }
        }
    }

    private fun runSelf () {
        runRepositoryOperation(outputOperation)
    }

    private fun runRepositoryOperation (op: OutputOperationDto<*>) {

        val repoExecutor = RepositoryExecutor(dbConnector)

        when (outputOperation.entity) {
            OutputOperationEntityDto.CUSTOMER -> repoExecutor.run<CustomerOutput>(op, "Customer")
            OutputOperationEntityDto.MAP -> repoExecutor.run<MapOutput>(op, "Map")
            OutputOperationEntityDto.ROUTER -> repoExecutor.run<RouterOutput>(op, "Router")
            OutputOperationEntityDto.COORDINATE -> repoExecutor.run<CoordinateOutput>(op, "Coordinate")
            OutputOperationEntityDto.TRACKED_DEVICE -> repoExecutor.run<TrackedDeviceOutput>(op, "TrackedDevice")
            OutputOperationEntityDto.TRACKING_POINT -> repoExecutor.run<TrackingPointOutput>(op, "TrackingPoint")
        }
    }
}