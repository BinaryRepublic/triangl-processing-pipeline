package com.triangl.processing.outputEntity

import java.io.Serializable

class CoordinateOutput: Serializable {

    lateinit var id: String

    var x: Float? = null

    var y: Float? = null

    var createdAt: String? = null

    var lastUpdatedAt: String? = null
}