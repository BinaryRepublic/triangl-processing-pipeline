package com.triangl.processing.outputEntity

import java.io.Serializable

class TrackedDeviceOutput: Serializable {

    lateinit var id: String

    lateinit var mapId: String

    var createdAt: String? = null

    var lastUpdatedAt: String? = null
}