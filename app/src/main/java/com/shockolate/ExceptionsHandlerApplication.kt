package com.shockolate

import android.app.Application
import com.shockolate.utils.CustomExceptionHandler

class ExceptionsHandlerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(CustomExceptionHandler())
    }
}