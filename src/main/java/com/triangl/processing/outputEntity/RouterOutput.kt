package com.triangl.processing.outputEntity

import java.io.Serializable

class RouterOutput: Serializable {

    lateinit var id: String

    lateinit var coordinateId: String

    lateinit var mapId: String

    var location: CoordinateOutput? = null

    var lastUpdatedAt: String? = null

    var createdAt: String? = null
}