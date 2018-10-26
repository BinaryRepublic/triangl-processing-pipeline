package com.triangl.processing.inputEntity

import java.util.*

class TrackingPointInput : InputEntity() {

    var id: String? = null

    var deviceId: String? = null

    var location: CoordinateInput? = null

    var timestamp: Date? = null

    var createdAt: Date? = null

    var lastUpdatedAt: Date? = null
}