package com.triangl.processing.repository

import com.triangl.processing.outputEntity.TrackedDeviceOutput

class TrackedDeviceRepository : RepositoryInterface<TrackedDeviceOutput> {

    override fun isExisting (id: TrackedDeviceOutput): Boolean {
        return true
    }

    override fun create (data: TrackedDeviceOutput) {
        System.out.printf("create trackedDevice ${data.id}")
    }

    override fun update (data: TrackedDeviceOutput) {
        System.out.printf("update trackedDevice ${data.id}")
    }

    override fun delete (data: TrackedDeviceOutput) {
        System.out.printf("delete trackedDevice ${data.id}")
    }
}