package com.offlinebrain.command

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs

class SuspendCommandBusTest {
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun shouldHandleCommand(): TestResult {
        return runTest {
            val commandBus = AsyncCommandBus(GlobalScope)
            commandBus.register(TestHandler())
            val result = commandBus.sendAsync(TestCommand()).await()

            assertIs<Success>(result)
        }
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun shouldHandleManyCommands(): TestResult {
        return runTest {
            val commandBus = AsyncCommandBus(GlobalScope)
            commandBus.register(TestHandler())
            val result = commandBus.sendManyAsync(listOf(TestCommand(), TestCommand())).await()

            assertIs<Success>(result)
        }
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun shouldHandleManyCommandsWithReduce(): TestResult {
        return runTest {
            val commandBus = AsyncCommandBus(GlobalScope)
            commandBus.register(TestHandler())
            commandBus.register(TestFailHandler())
            val result = commandBus.sendManyAsync(listOf(TestCommand(), TestFailCommand())).await()

            assertIs<Failure>(result)
        }
    }

    class TestHandler : CommandHandler() {
        init {
            on(TestCommand::class) {
                println("TestCommand")
                Success
            }
        }
    }

    class TestFailHandler : CommandHandler() {
        init {
            on(TestFailCommand::class) {
                println("TestFailCommand")
                Failure("failure")
            }
        }
    }

    class TestCommand : Command
    class TestFailCommand : Command
}
