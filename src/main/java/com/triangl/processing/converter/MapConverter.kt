package com.triangl.processing.converter

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.inputEntity.MapInput
import com.triangl.processing.outputEntity.MapOutput

class MapConverter {

    private val routerConverter = RouterConverter()

    fun convert (mapInput: MapInput, customerId: String): MapOutput {
        return MapOutput().apply {
            id = mapInput.id!!
            this.customerId = customerId
            name = mapInput.name
            svgPath = mapInput.svgPath
            width = mapInput.size?.x?.toString()
            height = mapInput.size?.y?.toString()
            createdAt = mapInput.createdAt
            lastUpdatedAt = mapInput.lastUpdatedAt
        }
    }

    fun apply (mapInputs: List<MapInput>, customerId: String) =
        apply(OutputOperationTypeDto.APPLY, mapInputs, customerId)

    fun applyAndClear (mapInputs: List<MapInput>, customerId: String) =
        apply(OutputOperationTypeDto.APPLY_AND_CLEAR, mapInputs, customerId)

    fun apply (operation: OutputOperationTypeDto, mapInputs: List<MapInput>, customerId: String): OutputOperationDto<*> {

        return OutputOperationDto(
            type = operation,
            entity = OutputOperationEntityDto.MAP,
            data = mapInputs.map { convert(it, customerId) },
            children = mapInputs.filter { it.router != null && it.router!!.isNotEmpty() }.map { mapInput ->
                routerConverter.applyAndClear(mapInput.router!!, mapInput.id!!)
            }
        )
    }
}