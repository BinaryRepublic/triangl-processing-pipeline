package com.triangl.processing.inputEntity

import java.util.*

class AreaInput: InputEntity() {

    var id: String? = null

    var vertices: List<CoordinateInput>? = null

    var createdAt: Date? = null

    var lastUpdatedAt: Date? = null
}