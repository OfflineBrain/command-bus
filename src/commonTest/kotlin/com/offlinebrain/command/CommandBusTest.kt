package com.offlinebrain.command

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class CommandBusTest {
    @Test
    fun shouldNotRegisterMultipleHandlersForCommand() {
        val commandBus = CommandBus()
        commandBus.register(TestHandler())
        assertFailsWith<IllegalStateException> { commandBus.register(TestHandlerWithDuplicate()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldHandleCommand() = runTest {
        val commandBus = CommandBus()
        commandBus.register(TestHandler())
        val result = commandBus.send(TestCommand())

        assertIs<Success>(result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldHandleManyCommands() = runTest {
        val commandBus = CommandBus()
        commandBus.register(TestHandler())
        val result = commandBus.sendMany(listOf(TestCommand(), TestCommand()))

        assertIs<Success>(result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldHandleManyCommandsWithReduce() = runTest {
        val commandBus = CommandBus()
        commandBus.register(TestHandler())
        commandBus.register(TestFailHandler())
        val result = commandBus.sendMany(listOf(TestCommand(), TestFailCommand()))

        assertIs<Failure>(result)
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

    class TestHandlerWithDuplicate : CommandHandler() {
        init {
            on(TestCommand::class) {
                println("TestCommand2")
                Success
            }
        }
    }

    class TestCommand : Command
    class TestFailCommand : Command
}