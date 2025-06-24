package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.AppResult
import io.kotest.core.spec.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

@DisplayName("TaskTitleValidatorImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskTitleValidatorImplTest {

    private val validator: TaskTitleValidator = TaskTitleValidatorImpl()

    @ParameterizedTest(name = "[{index}} Given title \"{0}\", the result is {1}")
    @MethodSource("provideInvalidTitleCases", "provideValidTitleCases")
    fun `validateTitle should return expected result`(
        title: String,
        expectedResult: AppResult<String, TaskTitleValidationError>,
    ) {
        val actualResult = validator(title)

        assertEquals(expected = expectedResult, actual = actualResult)
    }

    fun provideInvalidTitleCases(): Stream<Arguments> = Stream.of(
        Arguments.of("", AppResult.Error(TaskTitleValidationError.EMPTY)),
        Arguments.of("  ", AppResult.Error(TaskTitleValidationError.EMPTY)),
        Arguments.of("ab", AppResult.Error(TaskTitleValidationError.TOO_SHORT)),
        Arguments.of(
            "a".repeat(TaskTitleValidationError.MAX_LENGTH + 1), AppResult.Error(
                TaskTitleValidationError.OVERFLOW
            )
        )
    )

    fun provideValidTitleCases(): Stream<Arguments> = Stream.of(
        Arguments.of(
            "a".repeat(TaskTitleValidationError.MIN_LENGTH),
            AppResult.Success(data = "a".repeat(TaskTitleValidationError.MIN_LENGTH))
        ),
        Arguments.of(
            "a".repeat(TaskTitleValidationError.MAX_LENGTH),
            AppResult.Success(data = "a".repeat(TaskTitleValidationError.MAX_LENGTH))
        ),
        Arguments.of("  A valid title", AppResult.Success("A valid title")),
    )


}