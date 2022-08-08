package com.offlinebrain.command

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

    @Test
    fun shouldHandleCommand() {
        val commandBus = CommandBus()
        commandBus.register(TestHandler())
        val result = commandBus.send(TestCommand())

        assertIs<Success>(result)
    }

    @Test
    fun shouldHandleManyCommands() {
        val commandBus = CommandBus()
        commandBus.register(TestHandler())
        val result = commandBus.sendMany(listOf(TestCommand(), TestCommand()))

        assertIs<Success>(result)
    }

    @Test
    fun shouldHandleManyCommandsWithReduce() {
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