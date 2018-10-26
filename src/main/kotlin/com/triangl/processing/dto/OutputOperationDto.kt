package com.triangl.processing.dto

import com.triangl.processing.repository.RepositoryEntity
import java.io.Serializable

data class OutputOperationDto<T: RepositoryEntity>(

    var type: OutputOperationTypeDto,

    var entity: OutputOperationEntityDto,

    var data: List<T>,

    var children: List<OutputOperationDto<*>> = emptyList(),

    var parents: List<OutputOperationDto<*>> = emptyList()

) : Serializable


enum class OutputOperationTypeDto {

    APPLY,

    APPLY_AND_CLEAR,

    DELETE
}

enum class OutputOperationEntityDto {

    CUSTOMER,

    MAP,

    ROUTER,

    COORDINATE,

    TRACKED_DEVICE,

    TRACKING_POINT
}