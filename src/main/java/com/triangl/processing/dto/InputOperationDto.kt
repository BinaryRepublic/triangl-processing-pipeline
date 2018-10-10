package com.triangl.processing.dto

import java.io.Serializable

class InputOperationDto<T>(

    var type: InputOperationTypeDto,

    var data: T

) : Serializable


enum class InputOperationTypeDto {

    APPLY_CUSTOMER,

    APPLY_TRACKING_POINT
}