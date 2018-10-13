package com.triangl.processing.outputEntity

import com.triangl.processing.repository.RepositoryEntity
import java.util.*
import javax.persistence.Column

class RouterOutput: RepositoryEntity {

    @Column(name = "id")
    override lateinit var id: String

    @Column(name = "coordinateId")
    lateinit var coordinateId: String

    @Column(name = "mapId")
    lateinit var mapId: String

    @Column(name = "createdAt")
    var createdAt: Date? = null

    @Column(name = "lastUpdatedAt")
    var lastUpdatedAt: Date? = null

    override fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "id" to id,
            "coordinateId" to coordinateId,
            "mapId" to mapId,
            "createdAt" to createdAt,
            "lastUpdatedAt" to lastUpdatedAt
        )
    }
}