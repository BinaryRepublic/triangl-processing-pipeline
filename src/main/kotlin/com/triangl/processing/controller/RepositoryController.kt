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
            OutputOperationEntityDto.CUSTOMER -> repositoryExecutor.run(op, "Customer", CustomerOutput::class.java)
            OutputOperationEntityDto.MAP -> repositoryExecutor.run(op, "Map", MapOutput::class.java)
            OutputOperationEntityDto.ROUTER -> repositoryExecutor.run(op, "Router", RouterOutput::class.java)
            OutputOperationEntityDto.AREA -> repositoryExecutor.run(op, "Area", AreaOutput::class.java)
            OutputOperationEntityDto.COORDINATE -> repositoryExecutor.run(op, "Coordinate", CoordinateOutput::class.java)
            OutputOperationEntityDto.TRACKED_DEVICE -> repositoryExecutor.run(op, "TrackedDevice", TrackedDeviceOutput::class.java)
            OutputOperationEntityDto.TRACKING_POINT -> repositoryExecutor.run(op, "TrackingPoint", TrackingPointOutput::class.java)
        }
    }
}
