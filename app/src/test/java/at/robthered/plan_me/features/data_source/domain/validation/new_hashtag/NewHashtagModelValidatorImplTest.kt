package at.robthered.plan_me.features.data_source.domain.validation.new_hashtag


import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModelError
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidationError
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidator
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

@DisplayName("NewHashtagModelValidatorImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class NewHashtagModelValidatorImplTest {

    @RelaxedMockK
    private lateinit var hashtagNameValidator: HashtagNameValidator

    private lateinit var newHashtagModelValidator: NewHashtagModelValidatorImpl

    @BeforeEach
    fun setUp() {
        clearMocks(hashtagNameValidator)
        newHashtagModelValidator = NewHashtagModelValidatorImpl(hashtagNameValidator)
    }

    @ParameterizedTest(name = "[{index}] Given name \"{0}\", the underlying validator returns an error of type {2}")
    @MethodSource("provideValidationScenarios")
    fun `it should correctly map the name validation result`(
        inputName: String,
        nameValidatorResult: AppResult<String, HashtagNameValidationError>,
        expectedError: NewHashtagModelError,
        resultName: String,
    ) {
        // Arrange
        every { hashtagNameValidator(inputName) } returns nameValidatorResult

        // Act
        val newHashtagModel = UiHashtagModel.NewHashTagModel(name = inputName)
        val actualError = newHashtagModelValidator(newHashtagModel)

        // Assert
        assertEquals(expectedError, actualError)

        // Verify
        verify(exactly = 1) { hashtagNameValidator(inputName) }
    }

    fun provideValidationScenarios(): Stream<Arguments> = Stream.of(
        Arguments.of(
            "gueltigerHashtag",
            AppResult.Success("gueltigerHashtag"),
            NewHashtagModelError(name = null),
            "Success"
        ),
        Arguments.of(
            "",
            AppResult.Error(HashtagNameValidationError.EMPTY),
            NewHashtagModelError(name = HashtagNameValidationError.EMPTY),
            "EMPTY Error"
        ),
        Arguments.of(
            "a",
            AppResult.Error(HashtagNameValidationError.TOO_SHORT),
            NewHashtagModelError(name = HashtagNameValidationError.TOO_SHORT),
            "TOO_SHORT Error"
        ),
        Arguments.of(
            "a".repeat(HashtagNameValidationError.MAX_LENGTH + 1),
            AppResult.Error(HashtagNameValidationError.OVERFLOW),
            NewHashtagModelError(name = HashtagNameValidationError.OVERFLOW),
            "OVERFLOW Error"
        )
    )
}