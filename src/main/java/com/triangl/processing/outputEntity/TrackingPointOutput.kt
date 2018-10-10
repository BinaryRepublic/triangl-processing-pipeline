package com.triangl.processing.outputEntity

import java.io.Serializable

class TrackingPointOutput: Serializable {

    lateinit var id: String

    lateinit var trackedDeviceId: String

    lateinit var coordinateId: String

    var createdAt: String? = null

    var lastUpdatedAt: String? = null
}