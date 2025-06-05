package com.emermelada.artcenter

import android.app.Application
import com.emermelada.artcenter.di.PreferencesRepositoryEntryPoint
import com.emermelada.artcenter.utils.SessionManager
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.EntryPointAccessors

/**
 * Clase Application principal de la app, configurada con Hilt para inyección de dependencias.
 *
 * Al iniciarse, obtiene el PreferencesRepository a través de un EntryPoint de Hilt
 * y sincroniza el SessionManager con los datos almacenados en DataStore.
 */
@HiltAndroidApp
class App : Application() {

    /**
     * Método de ciclo de vida llamado al crear la aplicación.
     *
     * - Obtiene el EntryPoint para acceder a [PreferencesRepository].
     * - Recupera la instancia de [PreferencesRepository].
     * - Llama a [SessionManager.syncWithDataStore] para inicializar el SessionManager
     *   con los valores persistidos en DataStore (token, id y rol de usuario).
     */
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
