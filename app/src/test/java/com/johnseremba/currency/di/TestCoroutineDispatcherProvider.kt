package com.johnseremba.currency.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineDispatcherProvider(
    override val io: CoroutineDispatcher = StandardTestDispatcher(),
    override val default: CoroutineDispatcher = StandardTestDispatcher()
) : CoroutineDispatcherProvider()
