package com.antonioleiva.coroutines.cdi

import com.antonioleiva.coroutines.UseCaseAskPin
import com.antonioleiva.coroutines.UseCaseSimulation
import com.antonioleiva.coroutines.UseCaseSuspended
import org.koin.dsl.module.module

val domainModule = module {
    factory { UseCaseSimulation() }
    factory { UseCaseSuspended() }
    factory { UseCaseAskPin() }
}
