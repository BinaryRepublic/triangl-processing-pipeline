package com.triangl.processing.inputEntity

import java.io.Serializable

class CustomerInput : Serializable {

    var id: String? = null

    var name: String? = null

    var maps: List<MapInput>? = null

    var deleted: Boolean? = null

    var lastUpdatedAt: String? = null

    var createdAt: String? = null
}