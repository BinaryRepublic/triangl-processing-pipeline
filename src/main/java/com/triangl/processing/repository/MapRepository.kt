package com.triangl.processing.repository

import com.triangl.processing.outputEntity.MapOutput

class MapRepository : RepositoryInterface<MapOutput> {

    override fun isExisting (id: MapOutput): Boolean {
        return true
    }

    override fun create (data: MapOutput) {
        System.out.printf("create map ${data.id}")
    }

    override fun update (data: MapOutput) {
        System.out.printf("update map ${data.id}")
    }

    override fun delete (data: MapOutput) {
        System.out.printf("delete map ${data.id}")
    }
}