package com.remainder.app.domain.usecase.action

import com.remainder.app.testutil.FakeActionRepository
import com.remainder.app.testutil.FakeAlarmScheduler
import com.remainder.app.testutil.sampleAction
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddActionUseCaseTest {

    private lateinit var actionRepository: FakeActionRepository
    private lateinit var alarmScheduler: FakeAlarmScheduler
    private lateinit var useCase: AddActionUseCase

    @Before
    fun setUp() {
        actionRepository = FakeActionRepository()
        alarmScheduler = FakeAlarmScheduler()
        useCase = AddActionUseCase(actionRepository, alarmScheduler)
    }

    @Test
    fun invoke_persistsActionAndSchedulesAlarmWithAssignedId() = runTest {
        val id = useCase(sampleAction())

        val saved = actionRepository.currentActions.single()
        assertEquals(id, saved.id)
        assertTrue(alarmScheduler.scheduledActionIds.contains(id))
    }

    @Test
    fun invoke_setsCreatedAndUpdatedTimestamps() = runTest {
        val id = useCase(sampleAction())

        val saved = actionRepository.currentActions.single { it.id == id }
        assertEquals(saved.createdAt, saved.updatedAt)
    }
}
