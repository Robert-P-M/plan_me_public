package at.robthered.plan_me.features.update_section_title_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModelError
import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidationError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("CanSaveUpdateSectionTitleCheckerImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CanSaveUpdateSectionTitleCheckerImplTest {

    private val canSaveChecker: CanSaveUpdateSectionTitleChecker =
        CanSaveUpdateSectionTitleCheckerImpl()

    data class CanSaveTestCase(
        val testName: String,
        val errorModel: UpdateSectionTitleModelError,
        val expected: Boolean,
    ) {
        override fun toString(): String = testName
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideScenarios")
    fun `invoke should return true only if title error is null`(testCase: CanSaveTestCase) {
        // WHEN
        val actualCanSave = canSaveChecker(testCase.errorModel)

        // THEN
        assertEquals(testCase.expected, actualCanSave)
    }

    fun provideScenarios(): Stream<CanSaveTestCase> = Stream.of(
        CanSaveTestCase(
            testName = "GIVEN title error is null, THEN can save is true",
            errorModel = UpdateSectionTitleModelError(title = null),
            expected = true
        ),
        CanSaveTestCase(
            testName = "GIVEN title has an error, THEN can save is false",
            errorModel = UpdateSectionTitleModelError(title = SectionTitleValidationError.TOO_SHORT),
            expected = false
        )
    )
}