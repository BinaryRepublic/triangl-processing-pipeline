package com.triangl.processing.repository

import com.triangl.processing.outputEntity.TrackingPointOutput

class TrackingPointRepository : RepositoryInterface<TrackingPointOutput> {

    override fun isExisting (id: TrackingPointOutput): Boolean {
        return true
    }

    override fun create (data: TrackingPointOutput) {
        System.out.printf("create trackingPoint ${data.id}")
    }

    override fun update (data: TrackingPointOutput) {
        System.out.printf("update trackingPoint ${data.id}")
    }

    override fun delete (data: TrackingPointOutput) {
        System.out.printf("delete trackingPoint ${data.id}")
    }
}