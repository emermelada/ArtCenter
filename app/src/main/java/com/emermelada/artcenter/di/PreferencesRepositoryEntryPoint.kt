package com.emermelada.artcenter.di

import com.emermelada.artcenter.data.repositories.PreferencesRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * EntryPoint de Hilt para exponer el [PreferencesRepository] a componentes que no admiten inyección directa.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface PreferencesRepositoryEntryPoint {

    /**
     * Proporciona la instancia de [PreferencesRepository] gestionada por Hilt.
     *
     * @return Instancia singleton de [PreferencesRepository].
     */
    fun preferencesRepository(): PreferencesRepository
}
