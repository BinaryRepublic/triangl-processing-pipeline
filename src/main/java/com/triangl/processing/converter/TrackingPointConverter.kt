package com.triangl.processing.converter

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.inputEntity.TrackingPointInput
import com.triangl.processing.outputEntity.TrackingPointOutput

class TrackingPointConverter {

    private val coordinateConverter = CoordinateConverter()

    fun convert (trackingPointInput: TrackingPointInput, trackedDeviceId: String, coordinateId: String): TrackingPointOutput {
        return TrackingPointOutput().apply {
            id = trackingPointInput.id!!
            this.trackedDeviceId = trackedDeviceId
            this.coordinateId = coordinateId
            createdAt = trackingPointInput.createdAt
            lastUpdatedAt = trackingPointInput.lastUpdatedAt
        }
    }

    fun apply (trackingPointInputs: List<TrackingPointInput>, trackedDeviceId: String): OutputOperationDto<*> {

        return OutputOperationDto(
            type = OutputOperationTypeDto.APPLY,
            entity = OutputOperationEntityDto.COORDINATE,
            data = trackingPointInputs.map { convert(it, trackedDeviceId, it.location!!.id!!) },
            parents = trackingPointInputs.filter { it.location != null }.map { routerInput ->
                coordinateConverter.apply(listOf(routerInput.location!!))
            }
        )
    }
}