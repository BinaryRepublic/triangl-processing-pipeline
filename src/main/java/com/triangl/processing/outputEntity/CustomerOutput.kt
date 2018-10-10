package com.triangl.processing.outputEntity

import java.io.Serializable

class CustomerOutput : Serializable {

    lateinit var id: String

    var name: String? = null

    var createdAt: String? = null

    var lastUpdatedAt: String? = null
}