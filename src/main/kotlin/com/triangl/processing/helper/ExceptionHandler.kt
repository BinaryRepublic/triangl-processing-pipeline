package com.triangl.processing.helper

class ExceptionHandler {

    companion object {

        fun throwClearNotSupported(table: String) {
            throw error("CLEAR or APPLY_AND_CLEAR is not supported for $table")
        }
    }
}