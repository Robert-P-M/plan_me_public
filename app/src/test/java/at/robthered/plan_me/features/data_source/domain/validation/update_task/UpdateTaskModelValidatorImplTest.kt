package at.robthered.plan_me.features.data_source.domain.validation.update_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModelError
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
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("UpdateTaskModelValidatorImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class UpdateTaskModelValidatorImplTest {

    @RelaxedMockK
    private lateinit var taskTitleValidator: TaskTitleValidator

    @RelaxedMockK
    private lateinit var taskDescriptionValidator: TaskDescriptionValidator

    private lateinit var updateTaskModelValidator: UpdateTaskModelValidator

    @BeforeEach
    fun setUp() {
        clearMocks(taskTitleValidator, taskDescriptionValidator)
        updateTaskModelValidator = UpdateTaskModelValidatorImpl(
            taskTitleValidator,
            taskDescriptionValidator
        )
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideValidationScenarios")
    fun `it should correctly aggregate results from child validators`(
        testName: String,
        inputModel: UpdateTaskModel,
        titleResult: AppResult<String, TaskTitleValidationError>,
        descriptionResult: AppResult<String?, TaskDescriptionValidationError>,
        expectedError: UpdateTaskModelError,
    ) {
        // Arrange
        every { taskTitleValidator.invoke(inputModel.title) } returns titleResult
        every { taskDescriptionValidator.invoke(inputModel.description) } returns descriptionResult

        // Act
        val actualError = updateTaskModelValidator(inputModel)

        // Assert
        assertEquals(expectedError, actualError)

        // Verify
        verify(exactly = 1) { taskTitleValidator.invoke(inputModel.title) }
        verify(exactly = 1) { taskDescriptionValidator.invoke(inputModel.description) }
    }


    fun provideValidationScenarios(): Stream<Arguments> = Stream.of(
        Arguments.of(
            "GIVEN title and description are valid, THEN no errors",
            UpdateTaskModel(title = "Valid Title", description = "Valid Description"),
            AppResult.Success("Valid Title"),
            AppResult.Success("Valid Description"),
            UpdateTaskModelError(title = null, description = null)
        ),

        Arguments.of(
            "GIVEN only title is invalid, THEN title error",
            UpdateTaskModel(title = "", description = "Valid Description"),
            AppResult.Error(TaskTitleValidationError.EMPTY),
            AppResult.Success("Valid Description"),
            UpdateTaskModelError(title = TaskTitleValidationError.EMPTY, description = null)
        ),

        Arguments.of(
            "GIVEN only description is invalid, THEN description error",
            UpdateTaskModel(title = "Valid Title", description = "a"),
            AppResult.Success("Valid Title"),
            AppResult.Error(TaskDescriptionValidationError.TOO_SHORT),
            UpdateTaskModelError(
                title = null,
                description = TaskDescriptionValidationError.TOO_SHORT
            )
        ),

        Arguments.of(
            "GIVEN both are invalid, THEN both errors",
            UpdateTaskModel(title = "", description = "a"),
            AppResult.Error(TaskTitleValidationError.EMPTY),
            AppResult.Error(TaskDescriptionValidationError.TOO_SHORT),
            UpdateTaskModelError(
                title = TaskTitleValidationError.EMPTY,
                description = TaskDescriptionValidationError.TOO_SHORT
            )
        ),

        Arguments.of(
            "GIVEN description is null, THEN no description error",
            UpdateTaskModel(title = "Valid Title", description = null),
            AppResult.Success("Valid Title"),
            AppResult.Success(null),
            UpdateTaskModelError(title = null, description = null)
        )
    )
}