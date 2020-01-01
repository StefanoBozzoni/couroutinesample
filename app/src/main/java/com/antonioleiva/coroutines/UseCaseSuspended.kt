package com.antonioleiva.coroutines

import java.util.*

class UseCaseSuspended {

    suspend fun execute():String {
        Thread.sleep(1000)
        val rnds = (0..20).random()
        return rnds.toString()
    }
}