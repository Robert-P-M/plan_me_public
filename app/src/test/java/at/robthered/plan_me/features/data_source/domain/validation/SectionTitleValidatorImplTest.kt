package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.AppResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("SectionTitleValidatorImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SectionTitleValidatorImplTest {


    private val validator: SectionTitleValidator = SectionTitleValidatorImpl()


    @ParameterizedTest(name = "[{index}] Given title \"{0}\", the result is {1}")
    @MethodSource("provideInvalidTitleCases", "provideValidTitleCases") // Nutzt beide Provider
    fun `validateTitle should return expected result`(
        title: String,
        expectedResult: AppResult<String, SectionTitleValidationError>,
    ) {
        // Act
        val actualResult = validator(title)

        // Assert
        assertEquals(expectedResult, actualResult)
    }

    fun provideInvalidTitleCases(): Stream<Arguments> = Stream.of(
        Arguments.of("", AppResult.Error(SectionTitleValidationError.EMPTY)),
        Arguments.of("  ", AppResult.Error(SectionTitleValidationError.EMPTY)),
        Arguments.of("ab", AppResult.Error(SectionTitleValidationError.TOO_SHORT)),
        Arguments.of(
            "a".repeat(SectionTitleValidationError.MAX_LENGTH + 1),
            AppResult.Error(SectionTitleValidationError.OVERFLOW)
        )
    )

    fun provideValidTitleCases(): Stream<Arguments> = Stream.of(
        Arguments.of(
            "a".repeat(SectionTitleValidationError.MIN_LENGTH),
            AppResult.Success("a".repeat(SectionTitleValidationError.MIN_LENGTH))
        ),
        Arguments.of(
            "a".repeat(SectionTitleValidationError.MAX_LENGTH),
            AppResult.Success("a".repeat(SectionTitleValidationError.MAX_LENGTH))
        ),
        Arguments.of("  Ein valider Titel  ", AppResult.Success("Ein valider Titel"))
    )


}