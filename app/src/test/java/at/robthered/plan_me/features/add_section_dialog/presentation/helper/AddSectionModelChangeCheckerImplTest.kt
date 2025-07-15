package at.robthered.plan_me.features.add_section_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("AddSectionModelChangeChecker Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddSectionModelChangeCheckerImplTest {

    private val changeChecker: AddSectionModelChangeChecker = AddSectionModelChangeCheckerImpl()

    @ParameterizedTest(name = "[{index}] Initial: ''{0}'' -> Current: ''{1}'' | Changed: {2}")
    @MethodSource("provideModelChangeScenarios")
    fun `invoke should return true only if titles are different`(
        initialModel: AddSectionModel,
        currentModel: AddSectionModel,
        expectedHasChanged: Boolean,
    ) {
        // Act
        val actualHasChanged = changeChecker(initialModel, currentModel)

        // Assert
        assertEquals(expectedHasChanged, actualHasChanged)
    }

    fun provideModelChangeScenarios(): Stream<Arguments> = Stream.of(
        Arguments.of(
            AddSectionModel(title = "Gleicher Titel"),
            AddSectionModel(title = "Gleicher Titel"),
            false
        ),
        Arguments.of(
            AddSectionModel(title = ""),
            AddSectionModel(title = ""),
            false
        ),
        Arguments.of(
            AddSectionModel(title = "Initialer Titel"),
            AddSectionModel(title = "Geänderter Titel"),
            true
        ),
        Arguments.of(
            AddSectionModel(title = "Titel"),
            AddSectionModel(title = "titel"),
            true
        ),
        Arguments.of(
            AddSectionModel(title = "Titel"),
            AddSectionModel(title = " Titel "),
            true
        ),
        Arguments.of(
            AddSectionModel(title = ""),
            AddSectionModel(title = "Neuer Titel"),
            true
        ),
        Arguments.of(
            AddSectionModel(title = "Alter Titel"),
            AddSectionModel(title = ""),
            true
        )
    )

}