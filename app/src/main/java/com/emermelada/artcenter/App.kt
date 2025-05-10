package com.emermelada.artcenter

import android.app.Application
import com.emermelada.artcenter.di.PreferencesRepositoryEntryPoint
import com.emermelada.artcenter.utils.SessionManager
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.EntryPointAccessors

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val entryPoint = EntryPointAccessors.fromApplication(
            this,
            PreferencesRepositoryEntryPoint::class.java
        )
        val preferencesRepository = entryPoint.preferencesRepository()

        SessionManager.syncWithDataStore(preferencesRepository)
    }
}
