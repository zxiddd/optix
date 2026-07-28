package com.zaddy.optix

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OptixApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
