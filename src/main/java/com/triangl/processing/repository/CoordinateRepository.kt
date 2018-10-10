package com.triangl.processing.repository

import com.triangl.processing.outputEntity.CoordinateOutput

class CoordinateRepository : RepositoryInterface<CoordinateOutput> {

    override fun isExisting (id: CoordinateOutput): Boolean {
        return true
    }

    override fun create (data: CoordinateOutput) {
        System.out.printf("create coordinate ${data.id}")
    }

    override fun update (data: CoordinateOutput) {
        System.out.printf("update coordinate ${data.id}")
    }

    override fun delete (data: CoordinateOutput) {
        System.out.printf("delete coordinate ${data.id}")
    }
}