package at.robthered.plan_me.features.common.presentation.helper

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.uiModels.AppUiEvent
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger
import at.robthered.plan_me.features.data_source.presentation.ext.toUiText
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("UseCaseOperatorImplTest Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UseCaseOperatorImplTest {

    @MockK
    private lateinit var appUiEventDispatcher: AppUiEventDispatcher

    @MockK
    private lateinit var appLogger: AppLogger

    private lateinit var useCaseOperator: UseCaseOperator

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCaseOperator = UseCaseOperatorImpl(appUiEventDispatcher, appLogger)
    }

    @Test
    @DisplayName("invoke() should dispatch Loading, Success, call action, and finally dispatch null on a successful use case")
    fun `successful use case`() = runTest {
        // ARRANGE
        val successData = "Erfolgsdaten"
        val loadingUiText = UiText.DynamicString("Lade...")
        val successUiText = UiText.DynamicString("Erfolg!")
        val successfulUseCase: suspend () -> AppResult<String, RoomDatabaseError> =
            { AppResult.Success(successData) }
        val onSuccessAction = mockk<suspend (String) -> Unit>(relaxed = true)

        coEvery { appUiEventDispatcher.dispatch(any()) } just Runs
        coEvery { appLogger.e(any(), any(), any()) } just Runs

        // ACT
        useCaseOperator(
            loadingStatus = loadingUiText,
            successMessageProvider = { successUiText },
            onSuccessAction = onSuccessAction,
            useCase = successfulUseCase
        )

        // ASSERT
        coVerify(ordering = Ordering.ORDERED) {
            appUiEventDispatcher.dispatch(AppUiEvent.Loading(loadingUiText))
            appUiEventDispatcher.dispatch(AppUiEvent.Success(successUiText))
            appUiEventDispatcher.dispatch(null)
        }
        coVerify(exactly = 1) { onSuccessAction.invoke(successData) }
    }

    @Test
    @DisplayName("invoke() should dispatch Loading, Error, and finally dispatch null when use case returns AppResult.Error")
    fun `failing use case with AppResult Error`() = runTest {
        // ARRANGE
        val domainError = RoomDatabaseError.UNKNOWN
        val loadingUiText = UiText.DynamicString("Lade...")
        val failingUseCase: suspend () -> AppResult<Unit, RoomDatabaseError> =
            { AppResult.Error(domainError) }
        val onSuccessAction = mockk<suspend (Unit) -> Unit>()

        coEvery { appUiEventDispatcher.dispatch(any()) } just Runs
        coEvery { appLogger.e(any(), any(), any()) } just Runs

        // ACT
        useCaseOperator(
            loadingStatus = loadingUiText,
            successMessageProvider = { UiText.DynamicString("Wird nie aufgerufen") },
            onSuccessAction = onSuccessAction,
            useCase = failingUseCase
        )

        // ASSERT
        coVerify(ordering = Ordering.ORDERED) {
            appUiEventDispatcher.dispatch(AppUiEvent.Loading(loadingUiText))
            appUiEventDispatcher.dispatch(AppUiEvent.Error(domainError.toUiText()))
            appUiEventDispatcher.dispatch(null)
        }

        coVerify(exactly = 0) { onSuccessAction.invoke(Unit) }
    }

    @Test
    @DisplayName("invoke() should dispatch Loading, UNKNOWN Error, and finally dispatch null when use case throws an exception")
    fun `use case throws unexpected exception`() = runTest {
        // ARRANGE
        val exception = RuntimeException("Datenbank explodiert!")
        val loadingUiText = UiText.DynamicString("Lade...")
        val explodingUseCase: suspend () -> AppResult<Unit, RoomDatabaseError> =
            { throw exception }

        coEvery { appUiEventDispatcher.dispatch(any()) } just Runs
        coEvery { appLogger.e(any(), any(), any()) } just Runs

        // ACT
        useCaseOperator(
            loadingStatus = loadingUiText,
            successMessageProvider = { UiText.DynamicString("Wird nie aufgerufen") },
            useCase = explodingUseCase
        )

        // ASSERT
        coVerify(ordering = Ordering.ORDERED) {
            appUiEventDispatcher.dispatch(AppUiEvent.Loading(loadingUiText))
            appUiEventDispatcher.dispatch(AppUiEvent.Error(RoomDatabaseError.UNKNOWN.toUiText()))
            appUiEventDispatcher.dispatch(null)
        }

        coVerify { appLogger.e("UseCaseOperator", any(), exception) }
    }

}