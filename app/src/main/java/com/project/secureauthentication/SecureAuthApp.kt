package com.project.secureauthentication

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import com.project.secureauthentication.di.authFeatureModule
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SecureAuthApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                authFeatureModule
            )
            androidContext(this@SecureAuthApp)
        }
        init()
    }
    private fun init() {
        // [START appcheck_initialize]
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
        // [END appcheck_initialize]
    }

}