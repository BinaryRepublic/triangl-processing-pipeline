package com.triangl.processing.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.triangl.processing.dto.InputOperationTypeDto
import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.inputEntity.CustomerInput
import java.io.IOException

class MainConverter {

    fun constructOutputOperations(inputOperationType: InputOperationTypeDto, jsonPayload: String): OutputOperationDto<*> {

        val mapper = ObjectMapper()
        try {
            val obj: CustomerInput = mapper.readValue(jsonPayload, CustomerInput::class.java)
            return OutputOperationDto(OutputOperationTypeDto.CREATE, OutputOperationEntityDto.CUSTOMER, obj)
        } catch (e: IOException) {
            throw e
        }
    }
}