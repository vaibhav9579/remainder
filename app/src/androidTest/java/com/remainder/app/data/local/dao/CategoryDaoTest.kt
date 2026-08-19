package com.remainder.app.data.local.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.remainder.app.data.local.database.DefaultCategories
import com.remainder.app.data.local.database.RemainderDatabase
import com.remainder.app.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var database: RemainderDatabase
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, RemainderDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        categoryDao = database.categoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAll_seedsDefaultCategories() = runBlocking {
        categoryDao.insertAll(DefaultCategories.seed())

        val categories = categoryDao.getAllCategories().first()

        assertEquals(6, categories.size)
        assertTrue(categories.any { it.name == "Work" })
        assertTrue(categories.any { it.name == "Other" })
    }

    @Test
    fun getAllCategories_ordersByName() = runBlocking {
        categoryDao.insert(CategoryEntity(name = "Zebra", icon = "z", createdAt = Instant.EPOCH))
        categoryDao.insert(CategoryEntity(name = "Apple", icon = "a", createdAt = Instant.EPOCH))

        val categories = categoryDao.getAllCategories().first()

        assertEquals(listOf("Apple", "Zebra"), categories.map { it.name })
    }

    @Test
    fun update_changesStoredName() = runBlocking {
        val id = categoryDao.insert(CategoryEntity(name = "Old name", icon = "icon", createdAt = Instant.EPOCH))
        val original = categoryDao.getCategoryById(id).first()!!

        categoryDao.update(original.copy(name = "New name"))

        assertEquals("New name", categoryDao.getCategoryById(id).first()?.name)
    }

    @Test
    fun delete_removesCategory() = runBlocking {
        val id = categoryDao.insert(CategoryEntity(name = "Temporary", icon = "icon", createdAt = Instant.EPOCH))
        val saved = categoryDao.getCategoryById(id).first()!!

        categoryDao.delete(saved)

        assertNull(categoryDao.getCategoryById(id).first())
    }
}
