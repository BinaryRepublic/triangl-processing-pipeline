package com.triangl.processing.converter

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto
import com.triangl.processing.inputEntity.CustomerInput
import com.triangl.processing.outputEntity.CustomerOutput

class CustomerConverter {

    private val mapConverter = MapConverter()

    fun convert (customerInput: CustomerInput): CustomerOutput {
        return CustomerOutput().apply {
            id = customerInput.id!!
            name = customerInput.name
            createdAt = customerInput.createdAt
            lastUpdatedAt = customerInput.lastUpdatedAt
        }
    }

    fun apply (customerInputs: List<CustomerInput>) =
        apply(OutputOperationTypeDto.APPLY, customerInputs)

    fun applyAndClear (customerInputs: List<CustomerInput>) =
        apply(OutputOperationTypeDto.APPLY_AND_CLEAR, customerInputs)

    fun apply (operation: OutputOperationTypeDto, customerInputs: List<CustomerInput>): OutputOperationDto<*> {

        val customerOutputData = customerInputs.map { convert(it) }

        return OutputOperationDto(
            type = operation,
            entity = OutputOperationEntityDto.CUSTOMER,
            data = customerOutputData,
            children = customerInputs.filter { it.maps != null && it.maps!!.isNotEmpty() }.map { customerInput ->
                mapConverter.applyAndClear(customerInput.maps!!, customerInput.id!!)
            }
        )
    }
}