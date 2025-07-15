package at.robthered.plan_me.features.add_section_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModelError
import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidationError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("CanSaveAddSectionChecker Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CanSaveAddSectionCheckerImplTest {

    private val canSaveChecker: CanSaveAddSectionChecker = CanSaveAddSectionCheckerImpl()

    @ParameterizedTest(name = "[{index}] Given error model with title={1}, canSave should be {2}")
    @MethodSource("provideErrorScenarios")
    fun `invoke should return true only when title error is null`(
        description: String,
        errorModel: AddSectionModelError,
        expectedCanSave: Boolean,
    ) {
        // Act
        val actualCanSave = canSaveChecker(errorModel)

        // Assert
        assertEquals(expectedCanSave, actualCanSave)
    }

    fun provideErrorScenarios(): Stream<Arguments> = Stream.of(
        Arguments.of(
            "Title is valid",
            AddSectionModelError(title = null),
            true
        ),
        Arguments.of(
            "Title is empty",
            AddSectionModelError(title = SectionTitleValidationError.EMPTY),
            false
        ),
        Arguments.of(
            "Title is too short",
            AddSectionModelError(title = SectionTitleValidationError.TOO_SHORT),
            false
        )
    )

}