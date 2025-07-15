package at.robthered.plan_me.features.data_source.domain.use_case.load_update_hashtag_model

import app.cash.turbine.test
import at.robthered.plan_me.features.data_source.data.local.mapper.toUpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import at.robthered.plan_me.features.data_source.domain.test_helpers.BaseKoinTest
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@DisplayName("LoadUpdateHashtagModelUseCaseImpl Tests")
class LoadUpdateHashtagModelUseCaseImplTest: BaseKoinTest() {

    private val localHashtagRepository: LocalHashtagRepository by inject()
    override fun getMocks(): Array<Any> {
        return arrayOf(
            localHashtagRepository
        )
    }

    override val useCaseModule: Module
        get() = module {
            factoryOf(::LoadUpdateHashtagModelUseCaseImpl){
                bind<LoadUpdateHashtagModelUseCase>()
            }
        }

    private val loadUpdateHashtagModelUseCase: LoadUpdateHashtagModelUseCase by inject()


    @Nested
    @DisplayName("GIVEN the hashtag exists in repository")
    inner class HashtagFound {
        /**
         * GIVEN the repository returns a flow containing a valid HashtagModel.
         * WHEN the use case is invoked.
         * THEN it should emit the correctly mapped UpdateHashtagModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should map and emit the correct UpdateHashtagModel")
        fun `maps existing hashtag to UpdateHashtagModel`() = runTest {
            // GIVEN
            val hashtagId = 1L
            val now = Clock.System.now()
            val hashtagFromRepo = HashtagModel(
                hashtagId = hashtagId,
                name = "My Hashtag",
                createdAt = now,
                updatedAt = now
            )
            val expectedUpdateModel = hashtagFromRepo.toUpdateHashtagModel()

            every { localHashtagRepository.get(hashtagId) } returns flowOf(hashtagFromRepo)

            // WHEN
            val resultFlow = loadUpdateHashtagModelUseCase.invoke(hashtagId)

            // THEN
            resultFlow.test {
                val emittedItem = awaitItem()
                assertThat(emittedItem).isEqualTo(expectedUpdateModel)
                awaitComplete()
            }
        }
    }

    @Nested
    @DisplayName("GIVEN the hashtag does not exist in repository")
    inner class HashtagNotFound {
        /**
         * GIVEN the repository returns a flow containing null.
         * WHEN the use case is invoked.
         * THEN it should emit a default, empty UpdateHashtagModel.
         */
        @Test
        @DisplayName("WHEN invoked THEN should emit a default UpdateHashtagModel")
        fun `emits default model when hashtag not found`() = runTest {
            // GIVEN
            val hashtagId = 404L
            val expectedDefaultModel = UpdateHashtagModel()

            every { localHashtagRepository.get(hashtagId) } returns flowOf(null)

            // WHEN
            val resultFlow = loadUpdateHashtagModelUseCase.invoke(hashtagId)

            // THEN
            resultFlow.test {
                val emittedItem = awaitItem()
                assertThat(emittedItem).isEqualTo(expectedDefaultModel)
                awaitComplete()
            }
        }
    }
}