package com.remainder.app.presentation.action

import androidx.lifecycle.SavedStateHandle
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.usecase.action.AddActionUseCase
import com.remainder.app.domain.usecase.action.GetActionByIdUseCase
import com.remainder.app.domain.usecase.action.UpdateActionUseCase
import com.remainder.app.domain.usecase.category.GetCategoriesUseCase
import com.remainder.app.domain.usecase.settings.GetSettingsUseCase
import com.remainder.app.testutil.FakeActionRepository
import com.remainder.app.testutil.FakeAlarmScheduler
import com.remainder.app.testutil.FakeCategoryRepository
import com.remainder.app.testutil.FakeSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditActionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var actionRepository: FakeActionRepository
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var alarmScheduler: FakeAlarmScheduler

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        actionRepository = FakeActionRepository()
        categoryRepository = FakeCategoryRepository()
        settingsRepository = FakeSettingsRepository()
        alarmScheduler = FakeAlarmScheduler()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(actionId: Long? = null): AddEditActionViewModel {
        val savedStateHandle = if (actionId != null) {
            SavedStateHandle(mapOf("actionId" to actionId))
        } else {
            SavedStateHandle()
        }
        return AddEditActionViewModel(
            savedStateHandle = savedStateHandle,
            getActionById = GetActionByIdUseCase(actionRepository),
            getCategories = GetCategoriesUseCase(categoryRepository),
            getSettings = GetSettingsUseCase(settingsRepository),
            addAction = AddActionUseCase(actionRepository, alarmScheduler),
            updateAction = UpdateActionUseCase(actionRepository, alarmScheduler),
        )
    }

    @Test
    fun save_withBlankTitle_setsTitleErrorAndDoesNotSave() {
        val viewModel = createViewModel()

        viewModel.save {}

        assertEquals("Title is required", viewModel.uiState.value.titleError)
        assertTrue(actionRepository.currentActions.isEmpty())
    }

    @Test
    fun save_withPastDateTime_setsDateTimeErrorAndDoesNotSave() {
        val viewModel = createViewModel()
        viewModel.onTitleChange("Call client")
        viewModel.onDateChange(LocalDate.now().minusDays(1))

        viewModel.save {}

        assertNotNull(viewModel.uiState.value.dateTimeError)
        assertTrue(actionRepository.currentActions.isEmpty())
    }

    @Test
    fun save_withValidInput_persistsActionAndInvokesCallback() {
        val viewModel = createViewModel()
        viewModel.onTitleChange("Call client")
        viewModel.onDateChange(LocalDate.now().plusDays(1))

        var savedCalled = false
        viewModel.save { savedCalled = true }

        assertTrue(savedCalled)
        assertEquals(1, actionRepository.currentActions.size)
        assertEquals("Call client", actionRepository.currentActions.single().title)
    }

    @Test
    fun newAction_seedsReminderOffsetFromSettingsDefault() = runTest {
        settingsRepository.setDefaultReminderOffset(ReminderOffset.DAY_1)

        val viewModel = createViewModel()

        assertEquals(ReminderOffset.DAY_1, viewModel.uiState.value.reminderOffset)
    }
}
