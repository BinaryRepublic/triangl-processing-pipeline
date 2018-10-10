package com.triangl.processing.repository

interface RepositoryInterface<T> {

    fun isExisting (id: T): Boolean

    fun create (data: T)

    fun update (data: T)

    fun delete (data: T)
}