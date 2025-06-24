package at.robthered.plan_me.features.data_source.domain.validation.add_section

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModel
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModelError
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
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("AddSectionModelValidator Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class AddSectionModelValidatorImplTest {

    @RelaxedMockK
    private lateinit var sectionTitleValidator: SectionTitleValidator

    private lateinit var addSectionModelValidator: AddSectionModelValidatorImpl

    @BeforeEach
    fun setUp() {
        clearMocks(sectionTitleValidator)
        addSectionModelValidator = AddSectionModelValidatorImpl(sectionTitleValidator)
    }

    @ParameterizedTest(name = "[{index}] Given title \"{0}\", the underlying validator returns {1}")
    @MethodSource("provideValidationScenarios")
    fun `it should correctly map the title validation result`(
        inputTitle: String,
        titleValidatorResult: AppResult<String, SectionTitleValidationError>,
        expectedError: AddSectionModelError,
    ) {
        // Arrange
        every { sectionTitleValidator(inputTitle) } returns titleValidatorResult

        // Act
        val addSectionModel = AddSectionModel(title = inputTitle)
        val actualError = addSectionModelValidator(addSectionModel)

        // Assert
        assertEquals(expectedError, actualError)

        // Verify
        verify(exactly = 1) { sectionTitleValidator(inputTitle) }
    }


    fun provideValidationScenarios(): Stream<Arguments> = Stream.of(
        Arguments.of(
            "Ein gültiger Titel",
            AppResult.Success("Ein gültiger Titel"),
            AddSectionModelError(title = null)
        ),
        Arguments.of(
            "A",
            AppResult.Error(SectionTitleValidationError.TOO_SHORT),
            AddSectionModelError(title = SectionTitleValidationError.TOO_SHORT)
        ),
        Arguments.of(
            "",
            AppResult.Error(SectionTitleValidationError.EMPTY),
            AddSectionModelError(title = SectionTitleValidationError.EMPTY)
        ),
        Arguments.of(
            "a".repeat(100),
            AppResult.Error(SectionTitleValidationError.OVERFLOW),
            AddSectionModelError(title = SectionTitleValidationError.OVERFLOW)
        )
    )

}