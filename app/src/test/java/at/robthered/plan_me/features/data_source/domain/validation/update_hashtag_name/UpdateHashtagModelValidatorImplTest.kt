package at.robthered.plan_me.features.data_source.domain.validation.update_hashtag_name

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModelError
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
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("UpdateHashtagModelValidatorImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class UpdateHashtagModelValidatorImplTest {

    @RelaxedMockK
    private lateinit var hashtagNameValidator: HashtagNameValidator

    private lateinit var updateHashtagModelValidator: UpdateHashtagModelValidator

    @BeforeEach
    fun setUp() {
        clearMocks(hashtagNameValidator)
        updateHashtagModelValidator = UpdateHashtagModelValidatorImpl(hashtagNameValidator)
    }

    data class ValidatorTestCase(
        val testName: String,
        val inputName: String,
        val nameValidatorResult: AppResult<String, HashtagNameValidationError>,
        val expectedError: UpdateHashtagModelError,
    ) {
        override fun toString(): String = testName
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideValidationScenarios")
    fun `it should correctly map the name validation result`(testCase: ValidatorTestCase) {
        // Arrange
        every { hashtagNameValidator(testCase.inputName) } returns testCase.nameValidatorResult

        // Act
        val updateHashtagModel = UpdateHashtagModel(name = testCase.inputName)
        val actualError = updateHashtagModelValidator(updateHashtagModel)

        // Assert
        assertEquals(testCase.expectedError, actualError)

        // Verify
        verify(exactly = 1) { hashtagNameValidator(testCase.inputName) }
    }

    fun provideValidationScenarios(): Stream<ValidatorTestCase> = Stream.of(
        ValidatorTestCase(
            testName = "GIVEN name is valid, THEN no error is returned",
            inputName = "gueltigerHashtag",
            nameValidatorResult = AppResult.Success("gueltigerHashtag"),
            expectedError = UpdateHashtagModelError(name = null)
        ),

        ValidatorTestCase(
            testName = "GIVEN name is invalid, THEN name error is returned",
            inputName = "",
            nameValidatorResult = AppResult.Error(HashtagNameValidationError.EMPTY),
            expectedError = UpdateHashtagModelError(name = HashtagNameValidationError.EMPTY)
        )
    )
}