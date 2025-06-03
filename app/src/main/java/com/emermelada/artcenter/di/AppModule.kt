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
import java.util.prefs.Preferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository {
        return PreferencesRepository(context.dataStore)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository{
        return AuthRepository()
    }

    @Provides
    @Singleton
    fun provideCategoriesRepository(): CategoriesRepository{
        return CategoriesRepository()
    }

    @Provides
    @Singleton
    fun provideSubcategoriesRepository(): SubcategoriesRepository {
        return SubcategoriesRepository()
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    @Provides
    @Singleton
    fun providePublicationRepository(): PublicationRepository {
        return PublicationRepository()
    }

    @Provides
    @Singleton
    fun provideCommentRepository(): CommentRepository {
        return CommentRepository()
    }
}