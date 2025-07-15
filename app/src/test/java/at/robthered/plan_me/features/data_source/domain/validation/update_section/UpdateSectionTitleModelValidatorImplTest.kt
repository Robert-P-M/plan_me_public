package at.robthered.plan_me.features.data_source.domain.validation.update_section

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModelError
import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidationError
import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidator
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

@DisplayName("UpdateSectionTitleModelValidatorImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class UpdateSectionTitleModelValidatorImplTest {

    @RelaxedMockK
    private lateinit var sectionTitleValidator: SectionTitleValidator

    private lateinit var updateSectionTitleModelValidator: UpdateSectionTitleModelValidatorImpl

    @BeforeEach
    fun setUp() {
        clearMocks(sectionTitleValidator)
        updateSectionTitleModelValidator =
            UpdateSectionTitleModelValidatorImpl(sectionTitleValidator)
    }

    data class ValidatorTestCase(
        val testName: String,
        val inputTitle: String,
        val titleValidatorResult: AppResult<String, SectionTitleValidationError>,
        val expectedError: UpdateSectionTitleModelError,
    ) {
        override fun toString(): String = testName
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideValidationScenarios")
    fun `it should correctly map the title validation result`(testCase: ValidatorTestCase) {
        // Arrange
        every { sectionTitleValidator(testCase.inputTitle) } returns testCase.titleValidatorResult

        // Act
        val updateSectionTitleModel = UpdateSectionTitleModel(title = testCase.inputTitle)
        val actualError = updateSectionTitleModelValidator(updateSectionTitleModel)

        // Assert
        assertEquals(testCase.expectedError, actualError)

        // Verify
        verify(exactly = 1) { sectionTitleValidator(testCase.inputTitle) }
    }

    fun provideValidationScenarios(): Stream<ValidatorTestCase> = Stream.of(
        ValidatorTestCase(
            testName = "GIVEN title is valid, THEN no error is returned",
            inputTitle = "A valid title",
            titleValidatorResult = AppResult.Success("A valid title"),
            expectedError = UpdateSectionTitleModelError(title = null)
        ),
        ValidatorTestCase(
            testName = "GIVEN title is invalid, THEN title error is returned",
            inputTitle = "",
            titleValidatorResult = AppResult.Error(SectionTitleValidationError.EMPTY),
            expectedError = UpdateSectionTitleModelError(title = SectionTitleValidationError.EMPTY)
        )
    )
}