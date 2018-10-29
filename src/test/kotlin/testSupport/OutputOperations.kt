package testSupport

import com.triangl.processing.dto.OutputOperationDto
import com.triangl.processing.dto.OutputOperationEntityDto
import com.triangl.processing.dto.OutputOperationTypeDto

class OutputOperations {

    private val mockData = MockData()

    fun customerOutputOperation(delete: Boolean = false) =
        OutputOperationDto(
            type = if (!delete) OutputOperationTypeDto.APPLY else OutputOperationTypeDto.DELETE,
            entity = OutputOperationEntityDto.CUSTOMER,
            data = listOf(
                    mockData.customerOutput("c1")
            ),
            children = listOf(
                OutputOperationDto(
                    type = if (!delete) OutputOperationTypeDto.APPLY_AND_CLEAR else OutputOperationTypeDto.DELETE,
                    entity = OutputOperationEntityDto.MAP,
                    data = listOf(
                            mockData.mapOutput("m1", "c1")
                    ),
                    children = listOf(
                        OutputOperationDto(
                            type = if (!delete) OutputOperationTypeDto.APPLY_AND_CLEAR else OutputOperationTypeDto.DELETE,
                            entity = OutputOperationEntityDto.ROUTER,
                            data = listOf(
                                    mockData.routerOutput("r1", "c1", "m1")
                            ),
                            parents = listOf(
                                OutputOperationDto(
                                    type = if (!delete) OutputOperationTypeDto.APPLY else OutputOperationTypeDto.DELETE,
                                    entity = OutputOperationEntityDto.COORDINATE,
                                    data = listOf(
                                            mockData.coordinateOutput("c1")
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

    fun trackingPointOutputOperation(delete: Boolean = false) =
        OutputOperationDto(
            type = if (!delete) OutputOperationTypeDto.APPLY else OutputOperationTypeDto.DELETE,
            entity = OutputOperationEntityDto.TRACKED_DEVICE,
            data = listOf(
                mockData.trackedDevice("td1", "m1")
            ),
            children = listOf(
                OutputOperationDto(
                    type = if (!delete) OutputOperationTypeDto.APPLY else OutputOperationTypeDto.DELETE,
                    entity = OutputOperationEntityDto.TRACKING_POINT,
                    data = listOf(
                        mockData.trackingPoint("tp1", "td1", "c1")
                    ),
                    parents = listOf(
                        OutputOperationDto(
                            type = if (!delete) OutputOperationTypeDto.APPLY else OutputOperationTypeDto.DELETE,
                            entity = OutputOperationEntityDto.COORDINATE,
                            data = listOf(
                                mockData.coordinateOutput("c1")
                            )
                        )
                    )
                )
            )
        )
}