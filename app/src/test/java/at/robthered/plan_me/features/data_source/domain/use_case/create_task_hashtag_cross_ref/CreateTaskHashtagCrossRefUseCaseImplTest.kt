package at.robthered.plan_me.features.data_source.domain.use_case.create_task_hashtag_cross_ref

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper.AddHashtagHelper
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("CreateTaskHashtagCrossRefUseCaseImpl Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateTaskHashtagCrossRefUseCaseImplTest {

    @MockK
    private lateinit var addHashtagHelper: AddHashtagHelper
    @MockK
    private lateinit var safeDatabaseResultCall: SafeDatabaseResultCall
    @MockK
    private lateinit var localTaskHashtagsCrossRefRepository: LocalTaskHashtagsCrossRefRepository
    @MockK
    private lateinit var clock: Clock
    @MockK
    private lateinit var transactionProvider: TransactionProvider

    private lateinit var createTaskHashtagCrossRefUseCase: CreateTaskHashtagCrossRefUseCase

    @Suppress("UNCHECKED_CAST")
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(
            addHashtagHelper,
            safeDatabaseResultCall,
            localTaskHashtagsCrossRefRepository,
            clock,
            transactionProvider
        )
        createTaskHashtagCrossRefUseCase = CreateTaskHashtagCrossRefUseCaseImpl(
            addHashtagHelper, safeDatabaseResultCall, localTaskHashtagsCrossRefRepository, clock
        )

        coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
            block()
        }
        coEvery { transactionProvider.runAsTransaction<AppResult<Unit, RoomDatabaseError>>(any()) } coAnswers {
            val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(0)
            block()
        }
    }

    @Nested
    @DisplayName("GIVEN different hashtag list compositions")
    inner class HashtagListScenarios {

        /**
         * GIVEN the input list contains only new hashtags.
         * WHEN the use case is invoked.
         * THEN it should call the addHashtagHelper and insert cross-refs with the new IDs.
         */
        @Test
        @DisplayName("WHEN only new hashtags are provided THEN should create them and insert cross-refs")
        fun `handles only new hashtags`() = runTest {
            // GIVEN
            val taskId = 1L
            val newHashtags = listOf(
                UiHashtagModel.NewHashTagModel(name = "new1"),
                UiHashtagModel.NewHashTagModel(name = "new2")
            )
            val newIds = listOf(10L, 11L)

            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { addHashtagHelper.invoke(newHashtags) } returns newIds
            coEvery { localTaskHashtagsCrossRefRepository.insert(any<List<TaskHashtagsCrossRefModel>>()) } returns emptyList()

            val crossRefSlot = slot<List<TaskHashtagsCrossRefModel>>()

            // WHEN
            val result = createTaskHashtagCrossRefUseCase.invoke(taskId, newHashtags)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 1) { addHashtagHelper.invoke(newHashtags) }
            coVerify(exactly = 1) { localTaskHashtagsCrossRefRepository.insert(capture(crossRefSlot)) }

            val capturedCrossRefs = crossRefSlot.captured
            assertThat(capturedCrossRefs).hasSize(2)
            assertThat(capturedCrossRefs.map { it.hashtagId }).isEqualTo(newIds)
        }

        /**
         * GIVEN the input list contains only existing hashtags.
         * WHEN the use case is invoked.
         * THEN it should only insert cross-refs with existing IDs and not call the helper.
         */
        @Test
        @DisplayName("WHEN only existing hashtags are provided THEN should only insert cross-refs")
        fun `handles only existing hashtags`() = runTest {
            // GIVEN
            val taskId = 1L
            val existingHashtags =
                listOf(UiHashtagModel.ExistingHashtagModel(hashtagId = 20L, name = "existing1"))

            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { localTaskHashtagsCrossRefRepository.insert(any<List<TaskHashtagsCrossRefModel>>()) } returns emptyList()

            val crossRefSlot = slot<List<TaskHashtagsCrossRefModel>>()

            // WHEN
            val result = createTaskHashtagCrossRefUseCase.invoke(taskId, existingHashtags)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 0) { addHashtagHelper.invoke(any<List<UiHashtagModel.NewHashTagModel>>()) }
            coVerify(exactly = 1) { localTaskHashtagsCrossRefRepository.insert(capture(crossRefSlot)) }

            val capturedCrossRefs = crossRefSlot.captured
            assertThat(capturedCrossRefs).hasSize(1)
            assertThat(capturedCrossRefs.first().hashtagId).isEqualTo(20L)
        }

        /**
         * GIVEN the input list is a mix of new and existing hashtags.
         * WHEN the use case is invoked.
         * THEN it should handle both paths correctly, calling insert twice.
         */
        @Test
        @DisplayName("WHEN a mixed list is provided THEN should handle both cases")
        fun `handles mixed list of hashtags`() = runTest {
            // GIVEN
            val taskId = 1L
            val newHashtags = listOf(UiHashtagModel.NewHashTagModel(name = "new"))
            val existingHashtags =
                listOf(UiHashtagModel.ExistingHashtagModel(hashtagId = 30L, name = "existing"))
            val mixedList = newHashtags + existingHashtags
            val newIds = listOf(15L)

            every { clock.now() } returns Instant.DISTANT_PAST
            coEvery { addHashtagHelper.invoke(newHashtags) } returns newIds
            coEvery { localTaskHashtagsCrossRefRepository.insert(any<List<TaskHashtagsCrossRefModel>>()) } returns emptyList()

            val capturedLists = mutableListOf<List<TaskHashtagsCrossRefModel>>()

            // WHEN
            val result = createTaskHashtagCrossRefUseCase.invoke(taskId, mixedList)

            // THEN
            assertThat(result).isInstanceOf(AppResult.Success::class.java)
            coVerify(exactly = 1) { addHashtagHelper.invoke(newHashtags) }
            coVerify(exactly = 2) { localTaskHashtagsCrossRefRepository.insert(capture(capturedLists)) }

            val firstInsert = capturedLists[0]
            val secondInsert = capturedLists[1]
            assertThat(firstInsert).hasSize(1)
            assertThat(firstInsert.first().hashtagId).isEqualTo(15L)

            assertThat(secondInsert).hasSize(1)
            assertThat(secondInsert.first().hashtagId).isEqualTo(30L)
        }
    }
}