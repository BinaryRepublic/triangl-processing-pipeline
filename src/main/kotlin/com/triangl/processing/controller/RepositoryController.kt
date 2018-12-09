package com.triangl.processing.controller

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.outputEntity.*
import com.triangl.processing.repository.RepositoryExecutor

class RepositoryController(
    private var outputOperation: OutputOperationDto<*>,
    private var repositoryExecutor: RepositoryExecutor
) {

    fun applyOutputOperations () {
        if (outputOperation.type == OutputOperationTypeDto.APPLY ||
            outputOperation.type == OutputOperationTypeDto.APPLY_AND_CLEAR) {
            runParents()
            runSelf()
            runChildren()
        } else if (outputOperation.type == OutputOperationTypeDto.DELETE) {
            runChildren()
            runSelf()
            runParents()
        }
    }

    private fun runParents () {
        if (outputOperation.parents.isNotEmpty()) {
            outputOperation.parents.forEach {
                RepositoryController(it, repositoryExecutor).applyOutputOperations()
            }
        }
    }

    private fun runChildren () {
        if (outputOperation.children.isNotEmpty()) {
            outputOperation.children.forEach {
                RepositoryController(it, repositoryExecutor).applyOutputOperations()
            }
        }
    }

    private fun runSelf () {
        runRepositoryOperation(outputOperation)
    }

    private fun runRepositoryOperation (op: OutputOperationDto<*>) {
        when (outputOperation.entity) {
            OutputOperationEntityDto.CUSTOMER -> repositoryExecutor.run<CustomerOutput>(op, "Customer")
            OutputOperationEntityDto.MAP -> repositoryExecutor.run<MapOutput>(op, "Map")
            OutputOperationEntityDto.ROUTER -> repositoryExecutor.run<RouterOutput>(op, "Router")
            OutputOperationEntityDto.AREA -> repositoryExecutor.run<AreaOutput>(op, "Area")
            OutputOperationEntityDto.COORDINATE -> repositoryExecutor.run<CoordinateOutput>(op, "Coordinate")
            OutputOperationEntityDto.TRACKED_DEVICE -> repositoryExecutor.run<TrackedDeviceOutput>(op, "TrackedDevice")
            OutputOperationEntityDto.TRACKING_POINT -> repositoryExecutor.run<TrackingPointOutput>(op, "TrackingPoint")
        }
    }
}
