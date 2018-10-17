package com.triangl.processing.inputEntity

import java.util.*

class CustomerInput : InputEntity() {

    var id: String? = null

    var name: String? = null

    var maps: List<MapInput>? = null

    var createdAt: Date? = null

    var lastUpdatedAt: Date? = null
}