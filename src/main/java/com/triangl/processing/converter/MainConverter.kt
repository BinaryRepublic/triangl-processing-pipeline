package com.triangl.processing.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.triangl.processing.dto.InputOperationTypeDto
import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.inputEntity.CustomerInput
import com.triangl.processing.inputEntity.TrackingPointInput
import java.io.IOException

class MainConverter {

    private var customerConverter: CustomerConverter = CustomerConverter()

    fun constructOutputOperations(inputOperationType: InputOperationTypeDto, jsonPayload: String): OutputOperationDto<*> {

        val payloadObject = parseJsonPayload(inputOperationType, jsonPayload)

        return when (inputOperationType) {
            InputOperationTypeDto.APPLY_CUSTOMER -> customerConverter.apply(payloadObject as List<CustomerInput>)
            InputOperationTypeDto.APPLY_TRACKING_POINT -> customerConverter.apply(payloadObject as List<CustomerInput>)
        }
    }

    private fun parseJsonPayload(inputOperationType: InputOperationTypeDto, jsonPayload: String): Any {
        val mapper = ObjectMapper()
        try {
            return when (inputOperationType) {
                InputOperationTypeDto.APPLY_CUSTOMER -> mapper.readValue(jsonPayload, object : TypeReference<List<CustomerInput>>() {})
                InputOperationTypeDto.APPLY_TRACKING_POINT -> mapper.readValue(jsonPayload, object: TypeReference<List<TrackingPointInput>>() {})
            }
        } catch (e: IOException) {
            throw e
        }
    }
}