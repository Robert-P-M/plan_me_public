package at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModelError
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidationError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("CanSaveNewHashtagModelCheckerImpl Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CanSaveNewHashtagModelCheckerImplTest {

    private val canSaveChecker: CanSaveNewHashtagModelChecker = CanSaveNewHashtagModelCheckerImpl()

    @ParameterizedTest(name = "[{index}] {0}, canSave should be {2}")
    @MethodSource("provideScenarios")
    fun `invoke should return true only when name error is null`(
        testDescription: String,
        errorModel: NewHashtagModelError,
        expectedCanSave: Boolean,
    ) {
        // Act
        val actualCanSave = canSaveChecker(errorModel)

        // Assert
        assertEquals(expectedCanSave, actualCanSave)
    }

    fun provideScenarios(): Stream<Arguments> = Stream.of(
        Arguments.of(
            "GIVEN name error is null",
            NewHashtagModelError(name = null),
            true
        ),
        Arguments.of(
            "GIVEN name has an EMPTY error",
            NewHashtagModelError(name = HashtagNameValidationError.EMPTY),
            false
        ),
        Arguments.of(
            "GIVEN name has a TOO_SHORT error",
            NewHashtagModelError(name = HashtagNameValidationError.TOO_SHORT),
            false
        )
    )

}