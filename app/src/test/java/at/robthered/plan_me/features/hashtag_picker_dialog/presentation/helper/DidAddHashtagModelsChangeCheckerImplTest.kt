package at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("DidAddHashtagModelsChangeCheckerImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DidAddHashtagModelsChangeCheckerImplTest {

    private val changeChecker: DidAddHashtagModelsChangeChecker =
        DidAddHashtagModelsChangeCheckerImpl()

    data class DidAddHashtagModelChangeCheckerTestCase(
        val testName: String,
        val initialList: List<UiHashtagModel>,
        val currentList: List<UiHashtagModel>,
        val expected: Boolean,
    ) {
        override fun toString(): String {
            return testName
        }
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideModelChangeScenarios")
    fun `invoke should return true only if titles are different`(
        testCase: DidAddHashtagModelChangeCheckerTestCase,
    ) {
        // Act
        val actualHasChanged = changeChecker(testCase.initialList, testCase.currentList)

        // Assert
        assertEquals(testCase.expected, actualHasChanged)
    }

    fun provideModelChangeScenarios(): Stream<DidAddHashtagModelChangeCheckerTestCase> = Stream.of(
        DidAddHashtagModelChangeCheckerTestCase(
            testName = "Given an empty initial list and empty current list, Then didChange should be false",
            initialList = emptyList(),
            currentList = emptyList(),
            expected = false
        ),
        DidAddHashtagModelChangeCheckerTestCase(
            testName = "Given an initial list with one hashtag, current list is empty. Then didChange should be true",
            initialList = listOf<UiHashtagModel>(
                UiHashtagModel.ExistingHashtagModel(
                    hashtagId = 1L,
                    name = "Existing hashtag"
                )
            ),
            currentList = emptyList(),
            expected = true
        ),
        DidAddHashtagModelChangeCheckerTestCase(
            testName = "Given an empty initial list, current list is not empty. Then didChange should be true",
            initialList = emptyList(),
            currentList = listOf<UiHashtagModel>(
                UiHashtagModel.ExistingHashtagModel(
                    hashtagId = 1L,
                    name = "Existing hashtag"
                ),
                UiHashtagModel.FoundHashtagModel(
                    hashtagId = 2L,
                    name = "Found hashtag"
                )
            ),
            expected = true
        ),
        DidAddHashtagModelChangeCheckerTestCase(
            testName = "Given and not empty initial list, currentList has one element extra. Then didChange should be true",
            initialList = listOf<UiHashtagModel>(
                UiHashtagModel.ExistingHashtagModel(
                    hashtagId = 1L,
                    name = "Existing hashtag"
                ),
                UiHashtagModel.FoundHashtagModel(
                    hashtagId = 2L,
                    name = "Found hashtag"
                )
            ),
            currentList = listOf<UiHashtagModel>(
                UiHashtagModel.ExistingHashtagModel(
                    hashtagId = 1L,
                    name = "Existing hashtag"
                ),
                UiHashtagModel.FoundHashtagModel(
                    hashtagId = 2L,
                    name = "Found hashtag"
                ),
                UiHashtagModel.NewHashTagModel(
                    index = 2,
                    name = "New Hashtag"
                )
            ),
            expected = true
        ),

        )

}