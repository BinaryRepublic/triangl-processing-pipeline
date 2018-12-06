package com.triangl.processing.converter

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.inputEntity.CoordinateInput
import com.triangl.processing.outputEntity.CoordinateOutput

class CoordinateConverter {

    fun convert (coordinateInput: CoordinateInput, routerId: String? = null, areaId: String? = null, trackingPointId: String? = null): CoordinateOutput {
        return CoordinateOutput().apply {
            id = coordinateInput.id!!
            this.routerId = routerId
            this.areaId = areaId
            this.trackingPointId = trackingPointId
            x = coordinateInput.x
            y = coordinateInput.y
            createdAt = coordinateInput.createdAt
            lastUpdatedAt = coordinateInput.lastUpdatedAt
        }
    }

    fun apply (coordinateInputs: List<CoordinateInput>, routerId: String? = null, areaId: String? = null, trackingPointId: String? = null): OutputOperationDto<CoordinateOutput> {

        return OutputOperationDto(
            type = OutputOperationTypeDto.APPLY,
            entity = OutputOperationEntityDto.COORDINATE,
            data = coordinateInputs.map { convert(it, routerId, areaId, trackingPointId) }
        )
    }
}