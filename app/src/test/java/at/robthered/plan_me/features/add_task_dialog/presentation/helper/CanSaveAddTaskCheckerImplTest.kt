package at.robthered.plan_me.features.add_task_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModelError
import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidationError
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidationError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("CanSaveAddTaskCheckerImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CanSaveAddTaskCheckerImplTest {

    private val canSaveChecker: CanSaveAddTaskChecker = CanSaveAddTaskCheckerImpl()

    data class CanSaveAddTaskTestCase(
        val testName: String,
        val modelError: AddTaskModelError,
        val expected: Boolean,
    ) {
        override fun toString(): String {
            return testName
        }
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideErrorScenarios")

    fun `invoke should return true only if all error fields are null`(
        testCase: CanSaveAddTaskTestCase,
    ) {
        // WHEN
        val actualCanSave = canSaveChecker(testCase.modelError)

        // THEN
        assertEquals(testCase.expected, actualCanSave)
    }

    fun provideErrorScenarios(): Stream<CanSaveAddTaskTestCase> = Stream.of(
        CanSaveAddTaskTestCase(
            testName = "GIVEN no errors, THEN can save is true",
            modelError = AddTaskModelError(title = null, description = null),
            expected = true
        ),
        CanSaveAddTaskTestCase(
            testName = "GIVEN only a title error, THEN can save is false",
            modelError = AddTaskModelError(
                title = TaskTitleValidationError.EMPTY,
                description = null
            ),
            expected = false
        ),
        CanSaveAddTaskTestCase(
            testName = "GIVEN only a description error, THEN can save is false",
            modelError = AddTaskModelError(
                title = null,
                description = TaskDescriptionValidationError.TOO_SHORT
            ),
            expected = false
        ),
        CanSaveAddTaskTestCase(
            testName = "GIVEN both title and description have errors, THEN can save is false",
            modelError = AddTaskModelError(
                title = TaskTitleValidationError.OVERFLOW,
                description = TaskDescriptionValidationError.OVERFLOW
            ),
            expected = false
        )
    )

}