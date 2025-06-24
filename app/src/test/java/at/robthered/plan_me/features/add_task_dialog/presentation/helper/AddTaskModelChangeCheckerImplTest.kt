package at.robthered.plan_me.features.add_task_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("AddTaskModelChangeCheckerImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddTaskModelChangeCheckerImplTest {

    private val changeChecker: AddTaskModelChangeChecker = AddTaskModelChangeCheckerImpl()

    data class ModelChangeTestCase(
        val testName: String,
        val initialModel: AddTaskModel,
        val currentModel: AddTaskModel,
        val expected: Boolean,
    ) {
        override fun toString(): String {
            return testName
        }
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideModelChangeScenarios")

    fun `invoke should return true only if values are different`(
        testCase: ModelChangeTestCase,
    ) {
        // WHEN
        val actualHasChanged = changeChecker(testCase.initialModel, testCase.currentModel)

        // THEN
        assertEquals(testCase.expected, actualHasChanged)
    }


    fun provideModelChangeScenarios(): Stream<ModelChangeTestCase> = Stream.of(
        ModelChangeTestCase(
            testName = "Scenario 1 -> Title, description, priorityEnum did not change, expected result is 'false'",
            AddTaskModel(
                title = "First Title",
                description = null,
                priorityEnum = PriorityEnum.LOW
            ),
            AddTaskModel(
                title = "First Title",
                description = null,
                priorityEnum = PriorityEnum.LOW
            ),
            false
        ),
        ModelChangeTestCase(
            testName = "Scenario 2 -> Title did change - description and priorityEnum did not change, expected result is 'true'",
            AddTaskModel(
                title = "First Titl",
                description = null,
                priorityEnum = PriorityEnum.LOW
            ),
            AddTaskModel(
                title = "First Title",
                description = null,
                priorityEnum = PriorityEnum.LOW
            ),
            true
        ),
        ModelChangeTestCase(
            testName = "Scenario 2 -> Title and description did not change - priorityEnum did  change, expected result is 'true'",
            AddTaskModel(
                title = "First Title",
                description = null,
                priorityEnum = PriorityEnum.HIGH
            ),
            AddTaskModel(
                title = "First Title",
                description = null,
                priorityEnum = PriorityEnum.LOW
            ),
            true
        ),
        ModelChangeTestCase(
            testName = "Scenario 2 -> Title and priorityEnum did not change - description did change, expected result is 'true'",
            AddTaskModel(
                title = "First Title",
                description = null,
                priorityEnum = PriorityEnum.LOW
            ),
            AddTaskModel(
                title = "First Title",
                description = "Description",
                priorityEnum = PriorityEnum.LOW
            ),
            true
        ),

        )
}