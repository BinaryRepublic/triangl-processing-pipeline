package testSupport

import com.triangl.processing.outputEntity.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class MockData {
    private var timeFormatter = DateTimeFormatter.ISO_DATE_TIME
    private var accessor = timeFormatter.parse("2018-01-01T00:00:00.000Z")
    var defaultDate = Date.from(Instant.from(accessor))!!

    fun customerOutput(id: String) =
        CustomerOutput().apply {
            this.id = id
            name = "name_$id"
            createdAt = defaultDate
            lastUpdatedAt = defaultDate
        }

    fun mapOutput(id: String, customerId: String) =
        MapOutput().apply {
            this.id = id
            this.customerId = customerId
            name = "name_$id"
            svgPath = "svgPath_$id"
            width = 1F
            height = 1F
            createdAt = defaultDate
            lastUpdatedAt = defaultDate
        }

    fun routerOutput(id: String, mapId: String) =
        RouterOutput().apply {
            this.id = id
            this.mapId = mapId
            createdAt = defaultDate
            lastUpdatedAt = defaultDate
        }

    fun areaOutput(id: String, mapId: String) =
        AreaOutput().apply {
            this.id = id
            this.mapId = mapId
            createdAt = defaultDate
            lastUpdatedAt = defaultDate
        }

    fun coordinateOutput(id: String, routerId: String? = null, areaId: String? = null, trackingPointId: String? = null) =
        CoordinateOutput().apply {
            this.id = id
            this.routerId = routerId
            this.areaId = areaId
            this.trackingPointId = trackingPointId
            x = 1F
            y = 1F
            createdAt = defaultDate
            lastUpdatedAt = defaultDate
        }

    fun trackedDevice(id: String, mapId: String) =
        TrackedDeviceOutput().apply {
            this.id = id
            this.mapId = mapId
            createdAt = defaultDate
            lastUpdatedAt = defaultDate
        }

    fun trackingPoint(id: String, trackedDeviceId: String) =
        TrackingPointOutput().apply {
            this.id = id
            this.trackedDeviceId = trackedDeviceId
            timestamp = defaultDate
            createdAt = defaultDate
            lastUpdatedAt = defaultDate
        }
}