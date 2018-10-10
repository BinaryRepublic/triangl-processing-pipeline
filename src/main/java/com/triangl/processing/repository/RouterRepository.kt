package com.triangl.processing.repository

import com.triangl.processing.outputEntity.RouterOutput

class RouterRepository : RepositoryInterface<RouterOutput> {

    override fun isExisting (id: RouterOutput): Boolean {
        return true
    }

    override fun create (data: RouterOutput) {
        System.out.printf("create router ${data.id}")
    }

    override fun update (data: RouterOutput) {
        System.out.printf("update router ${data.id}")
    }

    override fun delete (data: RouterOutput) {
        System.out.printf("delete router ${data.id}")
    }
}