package at.robthered.plan_me.features.data_source.domain.test_helpers

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.allMocksModule
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import io.mockk.clearMocks
import io.mockk.coEvery
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

/**
 * An abstract base class for unit tests that require a Koin environment with MockK.
 *
 * This class handles the boilerplate setup for Koin, including module management
 * and the setup and teardown of mocks. Subclasses are expected to provide
 * a specific [useCaseModule] for the class under test and a list of mocks
 * via [getMocks] to be cleared before each test.
 *
 * It also provides optional, predefined mock behaviors for commonly used services
 * like [SafeDatabaseResultCall] and [TransactionProvider], which can be enabled
 * via boolean flags.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseKoinTest : KoinTest {

    /**
     * The Koin module specific to the use case being tested.
     * Subclasses must override this to provide the factory for the class under test
     * and its direct dependencies.
     */
    protected abstract val useCaseModule: Module

    /**
     * Provides an array of all mock objects used in the concrete test class.
     * These mocks will be cleared before each test runs.
     *
     * @return An array of `Any` containing all MockK mocks to be cleared.
     */
    protected abstract fun getMocks(): Array<Any>

    /**
     * Set to `true` in a subclass to enable the default mock behavior for [SafeDatabaseResultCall].
     * When enabled, any call to `safeDatabaseResultCall.invoke()` will simply execute the
     * lambda block passed to it, effectively bypassing the database call wrapper.
     */
    protected open val mockSafeDatabaseResultCall: Boolean = false

    /**
     * Set to `true` in a subclass to enable the default mock behavior for [TransactionProvider].
     * When enabled, any call to `transactionProvider.runAsTransaction()` will simply execute the
     * lambda block passed to it, effectively bypassing the transaction wrapper.
     */
    protected open val mockTransactionProvider: Boolean = false

    // Common dependencies that can be used by subclasses.

    protected val transactionProvider: TransactionProvider by inject()
    protected val safeDatabaseResultCall: SafeDatabaseResultCall by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.Companion.create {
        // Combines the shared mock module with the specific use case module.
        modules(
            allMocksModule,
            useCaseModule
        )
    }

    /**
     * A hook for subclasses to provide additional, test-specific mocking or setup logic.
     * This function is called at the end of [baseSetUp].
     * It is empty by default, so subclasses only need to override it when needed.
     */
    protected open fun additionalSetUp() { }

    /**
     * Sets up the test environment before each test execution.
     * This function clears all mocks provided by [getMocks] and sets up
     * the conditional, predefined mock behaviors.
     */
    @BeforeEach
    fun baseSetUp() {
        val mocksToClear = getMocks()

        // Clear mocks only if the array is not empty, as clearMocks requires at least one mock.
        if (mocksToClear.isNotEmpty()) {
            val firstMock = mocksToClear.first()

            // This is the correct invocation for the MockK API, passing the first mock
            // separately and the rest as a vararg. `answers` are not cleared.
            val otherMocks = mocksToClear.drop(1).toTypedArray()
            clearMocks(firstMock, *otherMocks, answers = false)
        }

        // Enable default mock for SafeDatabaseResultCall if requested.
        if (mockSafeDatabaseResultCall) {
            coEvery { safeDatabaseResultCall.invoke<Unit>(any(), any()) } coAnswers {
                // The 'coAnswers' block gets the original lambda passed to the function
                // at argument index 1 and executes it.
                val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(1)
                block()
            }
        }

        // Enable default mock for TransactionProvider if requested.
        if (mockTransactionProvider) {
            coEvery { transactionProvider.runAsTransaction<AppResult<Unit, RoomDatabaseError>>(any()) } coAnswers {
                // The 'coAnswers' block gets the original lambda passed to the function
                // at argument index 0 and executes it.
                val block = arg<suspend () -> AppResult<Unit, RoomDatabaseError>>(0)
                block()
            }
        }

        // Call the 'Hook' for additional test setup
        additionalSetUp()
    }
}