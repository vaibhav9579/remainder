package com.remainder.app.data.local.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.remainder.app.data.local.database.RemainderDatabase
import com.remainder.app.data.local.entity.ActionEntity
import com.remainder.app.data.local.entity.CategoryEntity
import com.remainder.app.domain.model.Priority
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.RepeatType
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
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class ActionDaoTest {

    private lateinit var database: RemainderDatabase
    private lateinit var actionDao: ActionDao
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, RemainderDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        actionDao = database.actionDao()
        categoryDao = database.categoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun entity(
        title: String = "Call client",
        notes: String? = null,
        scheduledDate: LocalDate = LocalDate.of(2026, 6, 15),
        categoryId: Long? = null,
        isCompleted: Boolean = false,
    ) = ActionEntity(
        title = title,
        notes = notes,
        scheduledDate = scheduledDate,
        scheduledTime = LocalTime.of(10, 30),
        reminderOffset = ReminderOffset.AT_TIME,
        repeatType = RepeatType.NEVER,
        categoryId = categoryId,
        priority = Priority.MEDIUM,
        isCompleted = isCompleted,
        completedAt = if (isCompleted) Instant.EPOCH else null,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH,
    )

    @Test
    fun insertAndGetById_returnsSameAction() = runBlocking {
        val id = actionDao.insert(entity(title = "Pay bill"))

        val loaded = actionDao.getActionById(id).first()

        assertEquals("Pay bill", loaded?.title)
    }

    @Test
    fun getPendingActions_excludesCompleted() = runBlocking {
        actionDao.insert(entity(title = "Pending one", isCompleted = false))
        actionDao.insert(entity(title = "Done one", isCompleted = true))

        val pending = actionDao.getPendingActions().first()

        assertEquals(1, pending.size)
        assertEquals("Pending one", pending.first().title)
    }

    @Test
    fun getCompletedActions_onlyReturnsCompleted() = runBlocking {
        actionDao.insert(entity(title = "Pending one", isCompleted = false))
        actionDao.insert(entity(title = "Done one", isCompleted = true))

        val completed = actionDao.getCompletedActions().first()

        assertEquals(1, completed.size)
        assertEquals("Done one", completed.first().title)
    }

    @Test
    fun getActionsForDate_filtersByScheduledDate() = runBlocking {
        actionDao.insert(entity(title = "On target date", scheduledDate = LocalDate.of(2026, 6, 15)))
        actionDao.insert(entity(title = "Other date", scheduledDate = LocalDate.of(2026, 6, 16)))

        val results = actionDao.getActionsForDate(LocalDate.of(2026, 6, 15)).first()

        assertEquals(1, results.size)
        assertEquals("On target date", results.first().title)
    }

    @Test
    fun searchActions_matchesTitleOrNotes() = runBlocking {
        actionDao.insert(entity(title = "Call dentist", notes = null))
        actionDao.insert(entity(title = "Unrelated", notes = "remember dentist supplies"))
        actionDao.insert(entity(title = "Completely different"))

        val results = actionDao.searchActions("dentist").first()

        assertEquals(2, results.size)
    }

    @Test
    fun searchActions_matchesCategoryName() = runBlocking {
        val categoryId = categoryDao.insert(CategoryEntity(name = "Finance", icon = "finance", createdAt = Instant.EPOCH))
        actionDao.insert(entity(title = "Pay rent", categoryId = categoryId))
        actionDao.insert(entity(title = "Unrelated action", categoryId = null))

        val results = actionDao.searchActions("Finance").first()

        assertEquals(1, results.size)
        assertEquals("Pay rent", results.first().title)
    }

    @Test
    fun deletingCategory_setsActionsCategoryIdToNull() = runBlocking {
        val categoryId = categoryDao.insert(CategoryEntity(name = "Work", icon = "work", createdAt = Instant.EPOCH))
        val actionId = actionDao.insert(entity(title = "Ship feature", categoryId = categoryId))
        val category = CategoryEntity(id = categoryId, name = "Work", icon = "work", createdAt = Instant.EPOCH)

        categoryDao.delete(category)

        val action = actionDao.getActionById(actionId).first()
        assertNull(action?.categoryId)
    }

    @Test
    fun update_changesStoredFields() = runBlocking {
        val id = actionDao.insert(entity(title = "Original title"))
        val original = actionDao.getActionById(id).first()!!

        actionDao.update(original.copy(title = "Updated title", isCompleted = true))

        val updated = actionDao.getActionById(id).first()
        assertEquals("Updated title", updated?.title)
        assertTrue(updated?.isCompleted == true)
    }

    @Test
    fun delete_removesAction() = runBlocking {
        val id = actionDao.insert(entity())
        val saved = actionDao.getActionById(id).first()!!

        actionDao.delete(saved)

        assertNull(actionDao.getActionById(id).first())
    }
}
