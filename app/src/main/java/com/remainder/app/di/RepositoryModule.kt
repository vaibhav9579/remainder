package com.remainder.app.di

import com.remainder.app.data.repository.ActionRepositoryImpl
import com.remainder.app.data.repository.CategoryRepositoryImpl
import com.remainder.app.data.repository.SettingsRepositoryImpl
import com.remainder.app.domain.repository.ActionRepository
import com.remainder.app.domain.repository.CategoryRepository
import com.remainder.app.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindActionRepository(impl: ActionRepositoryImpl): ActionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
