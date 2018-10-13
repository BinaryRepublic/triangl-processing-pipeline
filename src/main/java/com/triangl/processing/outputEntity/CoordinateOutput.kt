package com.triangl.processing.outputEntity

import com.triangl.processing.repository.RepositoryEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class CoordinateOutput: RepositoryEntity {

    @Column(name = "id")
    override lateinit var id: String

    @Column(name = "x")
    var x: Float? = null

    @Column(name = "y")
    var y: Float? = null

    @Column(name = "createdAt")
    var createdAt: Date? = null

    @Column(name = "lastUpdatedAt")
    var lastUpdatedAt: Date? = null

    override fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "id" to id,
            "x" to x,
            "y" to y,
            "createdAt" to createdAt,
            "lastUpdatedAt" to lastUpdatedAt
        )
    }
}