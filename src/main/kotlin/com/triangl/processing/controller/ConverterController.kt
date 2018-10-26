package com.triangl.processing.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.triangl.processing.converter.CustomerConverter
import com.triangl.processing.converter.TrackedDeviceConverter
import com.triangl.processing.dto.InputOperationTypeDto
import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.PubSubAdditional
import com.triangl.processing.inputEntity.CustomerInput
import com.triangl.processing.inputEntity.TrackingPointInput
import java.io.IOException

class ConverterController {

    private var customerConverter: CustomerConverter = CustomerConverter()

    private var trackedDeviceController: TrackedDeviceConverter = TrackedDeviceConverter()

    fun constructOutputOperations(inputOperationType: InputOperationTypeDto, jsonPayload: String, jsonAdditional: String?): OutputOperationDto<*> {

        val payloadObject = parseJsonPayload(inputOperationType, jsonPayload)
        val additional = parseJsonAdditional(jsonAdditional)

        return when (inputOperationType) {
            InputOperationTypeDto.APPLY_CUSTOMER -> customerConverter.apply(payloadObject as List<CustomerInput>)
            InputOperationTypeDto.APPLY_TRACKING_POINT -> trackedDeviceController.apply(payloadObject as List<TrackingPointInput>, additional!!.mapId!!)
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

    private fun parseJsonAdditional(jsonAdditional: String?): PubSubAdditional? {
        return if (jsonAdditional != null) {
            val mapper = ObjectMapper()
            try {
                return mapper.readValue(jsonAdditional, object : TypeReference<PubSubAdditional>() {})
            } catch (e: IOException) {
                throw e
            }
        } else {
            null
        }
    }
}