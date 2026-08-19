package com.remainder.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.remainder.app.data.local.dao.ActionDao
import com.remainder.app.data.local.dao.CategoryDao
import com.remainder.app.data.local.entity.ActionEntity
import com.remainder.app.data.local.entity.CategoryEntity

@Database(
    entities = [ActionEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class RemainderDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao
    abstract fun categoryDao(): CategoryDao
}
