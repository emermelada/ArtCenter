package com.emermelada.artcenter.di

import android.content.Context
import com.emermelada.artcenter.data.repositories.AuthRepository
import com.emermelada.artcenter.data.repositories.CategoriesRepository
import com.emermelada.artcenter.data.repositories.CommentRepository
import com.emermelada.artcenter.data.repositories.PreferencesRepository
import com.emermelada.artcenter.data.repositories.PublicationRepository
import com.emermelada.artcenter.data.repositories.SubcategoriesRepository
import com.emermelada.artcenter.data.repositories.UserRepository
import com.emermelada.artcenter.data.repositories.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt que provee instancias singleton de los repositorios de la aplicación.
 *
 * Cada método marcado con @Provides retorna una instancia que podrá ser inyectada en
 * componentes que dependan de ella.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Proporciona una instancia singleton de [PreferencesRepository].
     *
     * @param context Contexto de la aplicación usado para obtener el DataStore de preferencias.
     * @return Instancia configurada de [PreferencesRepository].
     */
    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository {
        return PreferencesRepository(context.dataStore)
    }

    /**
     * Proporciona una instancia singleton de [AuthRepository].
     *
     * @return Instancia configurada de [AuthRepository].
     */
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    /**
     * Proporciona una instancia singleton de [CategoriesRepository].
     *
     * @return Instancia configurada de [CategoriesRepository].
     */
    @Provides
    @Singleton
    fun provideCategoriesRepository(): CategoriesRepository {
        return CategoriesRepository()
    }

    /**
     * Proporciona una instancia singleton de [SubcategoriesRepository].
     *
     * @return Instancia configurada de [SubcategoriesRepository].
     */
    @Provides
    @Singleton
    fun provideSubcategoriesRepository(): SubcategoriesRepository {
        return SubcategoriesRepository()
    }

    /**
     * Proporciona una instancia singleton de [UserRepository].
     *
     * @return Instancia configurada de [UserRepository].
     */
    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    /**
     * Proporciona una instancia singleton de [PublicationRepository].
     *
     * @return Instancia configurada de [PublicationRepository].
     */
    @Provides
    @Singleton
    fun providePublicationRepository(): PublicationRepository {
        return PublicationRepository()
    }

    /**
     * Proporciona una instancia singleton de [CommentRepository].
     *
     * @return Instancia configurada de [CommentRepository].
     */
    @Provides
    @Singleton
    fun provideCommentRepository(): CommentRepository {
        return CommentRepository()
    }
}
