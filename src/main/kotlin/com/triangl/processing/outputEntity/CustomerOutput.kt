package com.triangl.processing.outputEntity

import com.triangl.processing.repository.RepositoryEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class CustomerOutput : RepositoryEntity {

    @Column(name = "id")
    override lateinit var id: String

    @Column(name = "name")
    var name: String? = null

    @Column(name = "createdAt")
    var createdAt: Date? = null

    @Column(name = "lastUpdatedAt")
    var lastUpdatedAt: Date? = null

    override fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "id" to id,
            "name" to name,
            "createdAt" to createdAt,
            "lastUpdatedAt" to lastUpdatedAt
        )
    }

    override fun getForeignKeyClearClause(): String? =
        null
}