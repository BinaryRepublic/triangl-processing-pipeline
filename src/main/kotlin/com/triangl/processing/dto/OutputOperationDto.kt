package com.triangl.processing.dto

import com.triangl.processing.repository.RepositoryEntity
import org.codehaus.jackson.map.ObjectMapper
import java.io.Serializable

class OutputOperationDto<T: RepositoryEntity>(

    var type: OutputOperationTypeDto,

    var entity: OutputOperationEntityDto,

    var data: List<T>,

    var children: List<OutputOperationDto<*>> = emptyList(),

    var parents: List<OutputOperationDto<*>> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean {
        val jsonMapper = ObjectMapper()
        return jsonMapper.writeValueAsString(other!!) == jsonMapper.writeValueAsString(this)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + entity.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + children.hashCode()
        result = 31 * result + parents.hashCode()
        return result
    }
}

enum class OutputOperationTypeDto {

    APPLY,

    APPLY_AND_CLEAR,

    DELETE
}

enum class OutputOperationEntityDto {

    CUSTOMER,

    MAP,

    ROUTER,

    AREA,

    COORDINATE,

    TRACKED_DEVICE,

    TRACKING_POINT
}