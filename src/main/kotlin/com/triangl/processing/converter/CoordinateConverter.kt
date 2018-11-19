package com.triangl.processing.converter

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.inputEntity.CoordinateInput
import com.triangl.processing.outputEntity.CoordinateOutput

class CoordinateConverter {

    fun convert (coordinateInput: CoordinateInput, areaId: String? = null): CoordinateOutput {
        return CoordinateOutput().apply {
            id = coordinateInput.id!!
            x = coordinateInput.x
            y = coordinateInput.y
            this.areaId = areaId
            createdAt = coordinateInput.createdAt
            lastUpdatedAt = coordinateInput.lastUpdatedAt
        }
    }

    fun apply (coordinateInputs: List<CoordinateInput>, areaId: String? = null): OutputOperationDto<*> {

        return OutputOperationDto(
            type = OutputOperationTypeDto.APPLY,
            entity = OutputOperationEntityDto.COORDINATE,
            data = coordinateInputs.map { convert(it, areaId) }
        )
    }
}