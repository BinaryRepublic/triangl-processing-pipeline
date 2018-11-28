package com.triangl.processing.outputEntity

import com.triangl.processing.repository.RepositoryEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class MapOutput: RepositoryEntity {

    @Column(name = "id")
    override lateinit var id: String

    @Column(name = "customerId")
    lateinit var customerId: String

    @Column(name = "name")
    var name: String? = null

    @Column(name = "svgPath")
    var svgPath: String? = null

    @Column(name = "width")
    var width: Float? = null

    @Column(name = "height")
    var height: Float? = null

    @Column(name = "createdAt")
    var createdAt: Date? = null

    @Column(name = "lastUpdatedAt")
    var lastUpdatedAt: Date? = null

    override fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "id" to id,
            "customerId" to customerId,
            "name" to name,
            "svgPath" to svgPath,
            "width" to width,
            "height" to height,
            "createdAt" to createdAt,
            "lastUpdatedAt" to lastUpdatedAt
        )
    }

    override fun getForeignKeyClearClause() =
        "customerId=\"$customerId\""
}
