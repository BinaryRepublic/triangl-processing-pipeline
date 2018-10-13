package com.triangl.processing.inputEntity

import java.io.Serializable
import java.util.*

class CustomerInput : Serializable {

    var id: String? = null

    var name: String? = null

    var maps: List<MapInput>? = null

    var createdAt: Date? = null

    var lastUpdatedAt: Date? = null
}