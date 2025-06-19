package at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("DidUpdateHashtagModelChangeCheckerImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DidUpdateHashtagModelChangeCheckerImplTest {

    private val changeChecker: DidUpdateHashtagModelChangeChecker =
        DidUpdateHashtagModelChangeCheckerImpl()

    data class ChangeCheckerTestCase(
        val testName: String,
        val initialModel: UpdateHashtagModel,
        val currentModel: UpdateHashtagModel,
        val expected: Boolean,
    ) {
        override fun toString(): String = testName
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideScenarios")
    fun `invoke should return true only if names are different`(testCase: ChangeCheckerTestCase) {
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
            testName = "GIVEN names are identical, THEN should return false",
            initialModel = UpdateHashtagModel(name = "kotlin"),
            currentModel = UpdateHashtagModel(name = "kotlin"),
            expected = false
        ),
        ChangeCheckerTestCase(
            testName = "GIVEN names are both empty, THEN should return false",
            initialModel = UpdateHashtagModel(name = ""),
            currentModel = UpdateHashtagModel(name = ""),
            expected = false
        ),
        ChangeCheckerTestCase(
            testName = "GIVEN names are different, THEN should return true",
            initialModel = UpdateHashtagModel(name = "android"),
            currentModel = UpdateHashtagModel(name = "jetpack"),
            expected = true
        ),
        ChangeCheckerTestCase(
            testName = "GIVEN names differ only by case, THEN should return true",
            initialModel = UpdateHashtagModel(name = "Compose"),
            currentModel = UpdateHashtagModel(name = "compose"),
            expected = true
        ),
        ChangeCheckerTestCase(
            testName = "GIVEN names differ by whitespace, THEN should return true",
            initialModel = UpdateHashtagModel(name = "KMP"),
            currentModel = UpdateHashtagModel(name = " KMP "),
            expected = true
        )
    )
}