package com.triangl.processing.inputEntity

import java.util.*

class MapInput : InputEntity() {

    var id: String? = null

    var name: String? = null

    var svgPath: String? = null

    var size: CoordinateInput? = null

    var areas: List<AreaInput>? = null

    var router: List<RouterInput>? = null

    var createdAt: Date? = null

    var lastUpdatedAt: Date? = null
}