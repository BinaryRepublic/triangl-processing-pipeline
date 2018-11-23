package com.triangl.processing.repository

import java.io.Serializable

interface RepositoryEntity: Serializable {

    var id: String

    fun toHashMap(): HashMap<String, Any?>

    fun getForeignKeyClearClause(): String?
}