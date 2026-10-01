package com.wellio.app

import android.app.Application
import com.google.firebase.FirebaseApp

class WellioApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (FirebaseApp.getApps(this).isNotEmpty()) AppCheckInstaller.install()
    }
}
