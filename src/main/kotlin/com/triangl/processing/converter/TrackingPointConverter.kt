package com.triangl.processing.converter

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.inputEntity.TrackingPointInput
import com.triangl.processing.outputEntity.TrackingPointOutput

class TrackingPointConverter {

    private val coordinateConverter = CoordinateConverter()

    fun convert (trackingPointInput: TrackingPointInput, trackedDeviceId: String): TrackingPointOutput {
        return TrackingPointOutput().apply {
            id = trackingPointInput.id!!
            this.trackedDeviceId = trackedDeviceId
            timestamp = trackingPointInput.timestamp
            createdAt = trackingPointInput.createdAt
            lastUpdatedAt = trackingPointInput.lastUpdatedAt
        }
    }

    fun apply (trackingPointInputs: List<TrackingPointInput>, trackedDeviceId: String): OutputOperationDto<TrackingPointOutput> {

        return OutputOperationDto(
            type = OutputOperationTypeDto.APPLY,
            entity = OutputOperationEntityDto.TRACKING_POINT,
            data = trackingPointInputs.map { convert(it, trackedDeviceId) },
            children = trackingPointInputs.filter { it.location != null }.map { trackingPointInput ->
                coordinateConverter.apply(listOf(trackingPointInput.location!!), trackingPointId = trackingPointInput.id!!)
            }
        )
    }
}