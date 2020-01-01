package com.antonioleiva.coroutines

import android.app.Application
import com.antonioleiva.coroutines.cdi.domainModule
import com.antonioleiva.coroutines.cdi.viewModelModule
import org.koin.standalone.StandAloneContext.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(listOf(viewModelModule, domainModule),logger= KoinLogger())
    }
}
