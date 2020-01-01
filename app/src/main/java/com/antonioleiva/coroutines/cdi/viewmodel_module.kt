package com.antonioleiva.coroutines.cdi

import com.antonioleiva.coroutines.PinViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { PinViewModel(get(),get(),get()) }
}
