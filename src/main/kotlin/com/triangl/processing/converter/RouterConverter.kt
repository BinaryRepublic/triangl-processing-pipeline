package com.triangl.processing.converter

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.inputEntity.RouterInput
import com.triangl.processing.outputEntity.RouterOutput

class RouterConverter {

    private val coordinateConverter = CoordinateConverter()

    fun convert (routerInput: RouterInput, mapId: String): RouterOutput {
        return RouterOutput().apply {
            id = routerInput.id!!
            this.mapId = mapId
            createdAt = routerInput.createdAt
            lastUpdatedAt = routerInput.lastUpdatedAt
        }
    }

    fun applyAndClear (routerInputs: List<RouterInput>, mapId: String) =
        apply(OutputOperationTypeDto.APPLY_AND_CLEAR, routerInputs, mapId)

    fun apply (routerInputs: List<RouterInput>, mapId: String) =
        apply(OutputOperationTypeDto.APPLY, routerInputs, mapId)

    fun apply (operation: OutputOperationTypeDto, routerInputs: List<RouterInput>, mapId: String): OutputOperationDto<RouterOutput> {

        return OutputOperationDto(
            type = operation,
            entity = OutputOperationEntityDto.ROUTER,
            data = routerInputs.map { convert(it, mapId) },
            children = routerInputs.filter { it.location != null }.map { routerInput ->
                coordinateConverter.apply(listOf(routerInput.location!!), routerId = routerInput.id!!)
            }
        )
    }
}