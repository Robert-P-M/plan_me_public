package at.robthered.plan_me.features.update_section_title_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("UpdateSectionTitleModelChangeCheckerImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateSectionTitleModelChangeCheckerImplTest {

    private val changeChecker: UpdateSectionTitleModelChangeChecker =
        UpdateSectionTitleModelChangeCheckerImpl()

    data class ChangeCheckerTestCase(
        val testName: String,
        val initialModel: UpdateSectionTitleModel,
        val currentModel: UpdateSectionTitleModel,
        val expected: Boolean,
    ) {
        override fun toString(): String = testName
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideScenarios")
    fun `invoke should return true only if titles are different`(testCase: ChangeCheckerTestCase) {
        // WHEN
        val actual = changeChecker(
            initialModel = testCase.initialModel,
            currentModel = testCase.currentModel
        )

        // THEN
        assertEquals(testCase.expected, actual)
    }

    fun provideScenarios(): Stream<ChangeCheckerTestCase> = Stream.of(
        ChangeCheckerTestCase(
            testName = "GIVEN titles are identical, THEN should return false",
            initialModel = UpdateSectionTitleModel(title = "Same Title"),
            currentModel = UpdateSectionTitleModel(title = "Same Title"),
            expected = false
        ),
        ChangeCheckerTestCase(
            testName = "GIVEN titles are both empty, THEN should return false",
            initialModel = UpdateSectionTitleModel(title = ""),
            currentModel = UpdateSectionTitleModel(title = ""),
            expected = false
        ),
        ChangeCheckerTestCase(
            testName = "GIVEN titles are different, THEN should return true",
            initialModel = UpdateSectionTitleModel(title = "Initial Title"),
            currentModel = UpdateSectionTitleModel(title = "Updated Title"),
            expected = true
        ),
        ChangeCheckerTestCase(
            testName = "GIVEN titles differ only by case, THEN should return true",
            initialModel = UpdateSectionTitleModel(title = "Title"),
            currentModel = UpdateSectionTitleModel(title = "title"),
            expected = true
        ),
        ChangeCheckerTestCase(
            testName = "GIVEN titles differ by whitespace, THEN should return true",
            initialModel = UpdateSectionTitleModel(title = "Title"),
            currentModel = UpdateSectionTitleModel(title = " Title "),
            expected = true
        )
    )
}