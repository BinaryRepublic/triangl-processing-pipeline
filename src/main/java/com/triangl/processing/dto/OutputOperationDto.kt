package com.triangl.processing.dto

import java.io.Serializable

class OutputOperationDto<T: Serializable>(

    var type: OutputOperationTypeDto,

    var entity: OutputOperationEntityDto,

    var data: T

) : Serializable


enum class OutputOperationTypeDto {

    CREATE,

    UPDATE,

    UPDATE_AND_DELETE,

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