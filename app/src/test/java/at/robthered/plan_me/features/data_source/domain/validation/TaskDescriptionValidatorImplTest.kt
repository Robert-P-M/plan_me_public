package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.AppResult
import io.kotest.core.annotation.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

@DisplayName("TaskDescriptionValidatorImplTest Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskDescriptionValidatorImplTest {

    private val validator: TaskDescriptionValidator = TaskDescriptionValidatorImpl()


    @ParameterizedTest(name = "[{index}} Given description \"{0}\", the result is {1}")
    @MethodSource("provideInvalidDescriptionCases", "provideValidDescriptionCases")
    fun `validateDescription should return expected result`(
        description: String?,
        expectedResult: AppResult<String?, TaskDescriptionValidationError>,
    ) {
        val actualResult = validator(description)

        assertEquals(expected = expectedResult, actual = actualResult)
    }

    fun provideInvalidDescriptionCases(): Stream<Arguments> = Stream.of(
        Arguments.of("", AppResult.Error(TaskDescriptionValidationError.TOO_SHORT)),
        Arguments.of("", AppResult.Error(TaskDescriptionValidationError.TOO_SHORT)),
        Arguments.of("  ", AppResult.Error(TaskDescriptionValidationError.TOO_SHORT)),
        Arguments.of("ab", AppResult.Error(TaskDescriptionValidationError.TOO_SHORT)),
        Arguments.of(
            "a".repeat(TaskDescriptionValidationError.MAX_LENGTH + 1), AppResult.Error(
                TaskDescriptionValidationError.OVERFLOW
            )
        )
    )

    fun provideValidDescriptionCases(): Stream<Arguments> = Stream.of(
        Arguments.of(
            null,
            AppResult.Success(data = null)
        ),
        Arguments.of(
            "a".repeat(TaskDescriptionValidationError.MIN_LENGTH),
            AppResult.Success(data = "a".repeat(TaskDescriptionValidationError.MIN_LENGTH))
        ),
        Arguments.of(
            "a".repeat(TaskDescriptionValidationError.MAX_LENGTH),
            AppResult.Success(data = "a".repeat(TaskDescriptionValidationError.MAX_LENGTH))
        ),
        Arguments.of("  A valid description", AppResult.Success("A valid description")),
    )

}