package at.robthered.plan_me.features.data_source.domain.validation.add_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModel
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModelError
import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidationError
import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidator
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidationError
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidator
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


@DisplayName("AddTaskModelValidatorImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class AddTaskModelValidatorImplTest {

    @RelaxedMockK
    private lateinit var taskTitleValidator: TaskTitleValidator

    @RelaxedMockK
    private lateinit var taskDescriptionValidator: TaskDescriptionValidator

    private lateinit var addTaskModelValidator: AddTaskModelValidator

    @BeforeEach
    fun setUp() {
        clearMocks(taskTitleValidator, taskDescriptionValidator)
        addTaskModelValidator = AddTaskModelValidatorImpl(
            taskTitleValidator = taskTitleValidator,
            taskDescriptionValidator = taskDescriptionValidator
        )
    }

    data class ValidatorTestCase(
        val testName: String,
        val inputModel: AddTaskModel,
        val titleResult: AppResult<String, TaskTitleValidationError>,
        val descriptionResult: AppResult<String?, TaskDescriptionValidationError>,
        val expectedError: AddTaskModelError,
    ) {
        override fun toString(): String = testName
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideValidationScenarios")
    fun `it should correctly map validation results from its dependencies`(
        testCase: ValidatorTestCase,
    ) {
        // ARRANGE
        every { taskTitleValidator(testCase.inputModel.title) } returns testCase.titleResult
        every { taskDescriptionValidator(testCase.inputModel.description) } returns testCase.descriptionResult

        // ACT
        val actualError = addTaskModelValidator(testCase.inputModel)

        // ASSERT
        assertEquals(testCase.expectedError, actualError)

        // VERIFY
        verify(exactly = 1) { taskTitleValidator(testCase.inputModel.title) }
        verify(exactly = 1) { taskDescriptionValidator(testCase.inputModel.description) }
    }

    fun provideValidationScenarios(): Stream<ValidatorTestCase> = Stream.of(
        ValidatorTestCase(
            testName = "GIVEN title and description are valid, THEN no errors",
            inputModel = AddTaskModel(title = "Valid title", description = "Valid description"),
            titleResult = AppResult.Success("Valid title"),
            descriptionResult = AppResult.Success("Valid description"),
            expectedError = AddTaskModelError(title = null, description = null)
        ),
        ValidatorTestCase(
            testName = "GIVEN only title is invalid, THEN title error",
            inputModel = AddTaskModel(title = "A", description = "Valid description"),
            titleResult = AppResult.Error(TaskTitleValidationError.TOO_SHORT),
            descriptionResult = AppResult.Success("Valid description"),
            expectedError = AddTaskModelError(
                title = TaskTitleValidationError.TOO_SHORT,
                description = null
            )
        ),
        ValidatorTestCase(
            testName = "GIVEN only description is invalid, THEN description error",
            inputModel = AddTaskModel(title = "Valid title", description = "B"),
            titleResult = AppResult.Success("Valid title"),
            descriptionResult = AppResult.Error(TaskDescriptionValidationError.TOO_SHORT),
            expectedError = AddTaskModelError(
                title = null,
                description = TaskDescriptionValidationError.TOO_SHORT
            )
        ),
        ValidatorTestCase(
            testName = "GIVEN both are invalid, THEN both errors",
            inputModel = AddTaskModel(title = "", description = "B"),
            titleResult = AppResult.Error(TaskTitleValidationError.EMPTY),
            descriptionResult = AppResult.Error(TaskDescriptionValidationError.TOO_SHORT),
            expectedError = AddTaskModelError(
                title = TaskTitleValidationError.EMPTY,
                description = TaskDescriptionValidationError.TOO_SHORT
            )
        ),
        ValidatorTestCase(
            testName = "GIVEN description is null (valid case), THEN no description error",
            inputModel = AddTaskModel(title = "Valid title", description = null),
            titleResult = AppResult.Success("Valid title"),
            descriptionResult = AppResult.Success(null),
            expectedError = AddTaskModelError(title = null, description = null)
        )
    )
}