package at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModelError
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidationError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("CanSaveUpdateHashtagModelCheckerImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CanSaveUpdateHashtagModelCheckerImplTest {

    private val canSaveChecker: CanSaveUpdateHashtagModelChecker =
        CanSaveUpdateHashtagModelCheckerImpl()

    data class CanSaveTestCase(
        val testName: String,
        val errorModel: UpdateHashtagModelError,
        val expected: Boolean,
    ) {
        override fun toString(): String = testName
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideScenarios")
    fun `invoke should return true only if name error is null`(testCase: CanSaveTestCase) {
        // WHEN
        val actualCanSave = canSaveChecker(testCase.errorModel)

        // THEN
        assertEquals(testCase.expected, actualCanSave)
    }

    fun provideScenarios(): Stream<CanSaveTestCase> = Stream.of(
        CanSaveTestCase(
            testName = "GIVEN name error is null, THEN can save is true",
            errorModel = UpdateHashtagModelError(name = null),
            expected = true
        ),
        CanSaveTestCase(
            testName = "GIVEN name has an error, THEN can save is false",
            errorModel = UpdateHashtagModelError(name = HashtagNameValidationError.EMPTY),
            expected = false
        )
    )
}