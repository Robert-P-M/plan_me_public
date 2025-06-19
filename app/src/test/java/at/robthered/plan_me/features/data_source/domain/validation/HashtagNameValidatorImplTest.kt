package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.AppResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("HashtagNameValidatorImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HashtagNameValidatorImplTest {

    private val validator: HashtagNameValidator = HashtagNameValidatorImpl()

    @ParameterizedTest(name = "[{index}] Given hashtag \"{0}\", the result is {1}")
    @MethodSource("provideInvalidHashtagCases", "provideValidHashtagCases")
    fun `validate should return expected result for given hashtag name`(
        hashtagName: String,
        expectedResult: AppResult<String, HashtagNameValidationError>,
    ) {
        // Act
        val actualResult = validator(hashtagName)

        // Assert
        assertEquals(expectedResult, actualResult)
    }

    fun provideInvalidHashtagCases(): Stream<Arguments> = Stream.of(
        Arguments.of("", AppResult.Error(HashtagNameValidationError.EMPTY)),
        Arguments.of("   ", AppResult.Error(HashtagNameValidationError.EMPTY)),
        Arguments.of(
            "a".repeat(HashtagNameValidationError.MIN_LENGTH - 1),
            AppResult.Error(HashtagNameValidationError.TOO_SHORT)
        ),
        Arguments.of(
            "a".repeat(HashtagNameValidationError.MAX_LENGTH + 1),
            AppResult.Error(HashtagNameValidationError.OVERFLOW)
        )
    )

    fun provideValidHashtagCases(): Stream<Arguments> = Stream.of(
        Arguments.of(
            "a".repeat(HashtagNameValidationError.MIN_LENGTH),
            AppResult.Success("a".repeat(HashtagNameValidationError.MIN_LENGTH))
        ),
        Arguments.of(
            "a".repeat(HashtagNameValidationError.MAX_LENGTH),
            AppResult.Success("a".repeat(HashtagNameValidationError.MAX_LENGTH))
        ),
        Arguments.of(
            "  kotlin-dev  ",
            AppResult.Success("kotlin-dev")
        ),
        Arguments.of(
            "android",
            AppResult.Success("android")
        )
    )
}