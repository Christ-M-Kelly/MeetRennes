package com.meetrennes.app

import android.app.Application
import com.meetrennes.app.di.appModule
import org.koin.android.ext.koin.androidContext

class MeetRennesApp : Application(){
    override fun onCreate(){
        super.onCreate()

        org.koin.core.context.startKoin {
            androidContext(this@MeetRennesApp)
            modules(appModule)
        }

    }
}