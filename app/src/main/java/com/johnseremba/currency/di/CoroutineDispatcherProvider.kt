package com.johnseremba.currency.di

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers

open class CoroutineDispatcherProvider @Inject constructor() {
    open val io = Dispatchers.IO
    open val default = Dispatchers.Default
}
