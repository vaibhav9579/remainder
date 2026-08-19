package com.remainder.app.domain.usecase.action

import com.remainder.app.domain.model.RepeatType
import com.remainder.app.testutil.FakeActionRepository
import com.remainder.app.testutil.FakeAlarmScheduler
import com.remainder.app.testutil.sampleAction
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CompleteActionUseCaseTest {

    private lateinit var actionRepository: FakeActionRepository
    private lateinit var alarmScheduler: FakeAlarmScheduler
    private lateinit var useCase: CompleteActionUseCase

    @Before
    fun setUp() {
        actionRepository = FakeActionRepository()
        alarmScheduler = FakeAlarmScheduler()
        useCase = CompleteActionUseCase(actionRepository, alarmScheduler)
    }

    @Test
    fun nonRecurringAction_marksCompletedAndCancelsAlarm() = runTest {
        val id = actionRepository.addAction(sampleAction(repeatType = RepeatType.NEVER))
        val saved = actionRepository.currentActions.first { it.id == id }

        useCase(saved)

        val updated = actionRepository.currentActions.first { it.id == id }
        assertTrue(updated.isCompleted)
        assertNotNull(updated.completedAt)
        assertTrue(alarmScheduler.cancelledActionIds.contains(id))
        assertFalse(alarmScheduler.scheduledActionIds.contains(id))
    }

    @Test
    fun recurringAction_rollsToNextOccurrenceAndStaysPending() = runTest {
        val id = actionRepository.addAction(
            sampleAction(repeatType = RepeatType.DAILY, scheduledDate = LocalDate.of(2026, 6, 15)),
        )
        val saved = actionRepository.currentActions.first { it.id == id }

        useCase(saved)

        val updated = actionRepository.currentActions.first { it.id == id }
        assertFalse(updated.isCompleted)
        assertNull(updated.completedAt)
        assertEquals(LocalDate.of(2026, 6, 16), updated.scheduledDate)
        assertTrue(alarmScheduler.scheduledActionIds.contains(id))
        assertFalse(alarmScheduler.cancelledActionIds.contains(id))
    }
}
