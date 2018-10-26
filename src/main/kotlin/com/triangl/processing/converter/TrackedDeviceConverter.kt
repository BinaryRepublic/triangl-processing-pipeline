package com.triangl.processing.converter

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.inputEntity.TrackingPointInput
import com.triangl.processing.outputEntity.TrackedDeviceOutput

class TrackedDeviceConverter {

    private val trackingPointConverter = TrackingPointConverter()

    fun convert (trackingPointInputs: TrackingPointInput, mapId: String): TrackedDeviceOutput {
        return TrackedDeviceOutput().apply {
            id = trackingPointInputs.deviceId!!
            this.mapId = mapId
            createdAt = trackingPointInputs.createdAt
            lastUpdatedAt = trackingPointInputs.lastUpdatedAt
        }
    }

    fun apply (trackingPointInputs: List<TrackingPointInput>, mapId: String): OutputOperationDto<*> {

        return OutputOperationDto(
            type = OutputOperationTypeDto.APPLY,
            entity = OutputOperationEntityDto.TRACKED_DEVICE,
            data = trackingPointInputs.map { convert(it, mapId) },
            children = trackingPointInputs.map { trackingPointInput ->
                trackingPointConverter.apply(trackingPointInputs, trackingPointInput.deviceId!!)
            }
        )
    }
}