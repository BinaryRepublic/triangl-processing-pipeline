package com.triangl.processing.outputEntity

import java.io.Serializable

class MapOutput: Serializable {

    lateinit var id: String

    lateinit var customerId: String

    var name: String? = null

    var svgPath: String? = null

    var width: String? = null

    var height: String? = null

    var createdAt: String? = null

    var lastUpdatedAt: String? = null
}
