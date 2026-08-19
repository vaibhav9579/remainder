package com.remainder.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.remainder.app.data.local.dao.ActionDao
import com.remainder.app.data.local.dao.CategoryDao
import com.remainder.app.data.local.database.DefaultCategories
import com.remainder.app.data.local.database.MIGRATION_1_2
import com.remainder.app.data.local.database.RemainderDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRemainderDatabase(
        @ApplicationContext context: Context,
        categoryDaoProvider: Provider<CategoryDao>,
    ): RemainderDatabase {
        return Room.databaseBuilder(context, RemainderDatabase::class.java, "remainder.db")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        categoryDaoProvider.get().insertAll(DefaultCategories.seed())
                    }
                }
            })
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideActionDao(database: RemainderDatabase): ActionDao = database.actionDao()

    @Provides
    fun provideCategoryDao(database: RemainderDatabase): CategoryDao = database.categoryDao()
}
