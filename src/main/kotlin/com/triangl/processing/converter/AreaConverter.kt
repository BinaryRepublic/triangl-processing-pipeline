package com.triangl.processing.converter

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.inputEntity.AreaInput
import com.triangl.processing.inputEntity.CoordinateInput
import com.triangl.processing.inputEntity.MapInput
import com.triangl.processing.outputEntity.AreaOutput
import com.triangl.processing.outputEntity.CoordinateOutput

class AreaConverter {

    private val coordinateConverter = CoordinateConverter()

    fun convert (areaInput: AreaInput, mapId: String): AreaOutput {
        return AreaOutput().apply {
            id = areaInput.id!!
            this.mapId = mapId
            createdAt = areaInput.createdAt
            lastUpdatedAt = areaInput.lastUpdatedAt
        }
    }

    fun applyAndClear (areaInputs: List<AreaInput>, mapId: String) =
        apply(OutputOperationTypeDto.APPLY_AND_CLEAR, areaInputs, mapId)

    fun apply (operation: OutputOperationTypeDto, areaInputs: List<AreaInput>, mapId: String): OutputOperationDto<*> {

        return OutputOperationDto(
            type = operation,
            entity = OutputOperationEntityDto.AREA,
            data = areaInputs.map { convert(it, mapId) },
            children = areaInputs.filter { it.vertices != null && it.vertices!!.isNotEmpty() }.map { areaInput ->
                coordinateConverter.apply(areaInput.vertices!!, areaInput.id)
            }
        )
    }
}