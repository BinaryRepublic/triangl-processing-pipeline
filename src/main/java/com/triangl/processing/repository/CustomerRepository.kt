package com.triangl.processing.repository

import com.triangl.processing.outputEntity.CustomerOutput

class CustomerRepository : RepositoryInterface<CustomerOutput> {

    override fun isExisting (id: CustomerOutput): Boolean {
        return true
    }

    override fun create (data: CustomerOutput) {
        System.out.printf("create customer ${data.id}")
    }

    override fun update (data: CustomerOutput) {
        System.out.printf("update customer ${data.id}")
    }

    override fun delete (data: CustomerOutput) {
        System.out.printf("delete customer ${data.id}")
    }
}