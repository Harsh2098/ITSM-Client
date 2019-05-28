package com.hmproductions.itsmclient.dagger

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @Provides
    @ITSMApplicationScope
    fun context() = context
}