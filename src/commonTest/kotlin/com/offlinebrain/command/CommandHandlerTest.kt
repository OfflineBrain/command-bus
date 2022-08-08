package com.offlinebrain.command

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class CommandHandlerTest {
    @Test
    fun shouldSaveCommand() {
        val commandHandler = CommandHandler()

        class TestCommand : Command

        val testHandlerFunction: suspend (TestCommand) -> CommandResult = { _: TestCommand -> Success }

        commandHandler.on(TestCommand::class, testHandlerFunction)

        val actual = commandHandler.registeredCommands[TestCommand::class]
        assertNotNull(actual)
        assertSame(testHandlerFunction, actual as suspend (TestCommand) -> CommandResult)
    }

    @Test
    fun shouldSaveAllCommands() {
        val commandHandler = CommandHandler()

        class TestCommand : Command
        class TestCommandSecond : Command

        val testHandlerFunction: suspend (TestCommand) -> CommandResult = { _: TestCommand -> Success }
        val testHandlerFunctionSecond: suspend (TestCommandSecond) -> CommandResult = { _: TestCommandSecond -> Success }

        commandHandler.on(TestCommand::class, testHandlerFunction)
        commandHandler.on(TestCommandSecond::class, testHandlerFunctionSecond)

        val actual = commandHandler.registeredCommands[TestCommand::class]
        assertNotNull(actual)
        assertSame(testHandlerFunction, actual as suspend (TestCommand) -> CommandResult)

        val actualSecond = commandHandler.registeredCommands[TestCommandSecond::class]
        assertNotNull(actualSecond)
        assertSame(testHandlerFunctionSecond, actualSecond as suspend (TestCommandSecond) -> CommandResult)
    }

    @Test
    fun shouldNotOverrideHandler() {
        val commandHandler = CommandHandler()

        class TestCommand : Command

        val testHandlerFunction = { _: TestCommand -> Success }
        val testHandlerFunctionSecond = { _: TestCommand -> Failure("") }

        commandHandler.on(TestCommand::class, testHandlerFunction)
        assertFailsWith<IllegalStateException> { commandHandler.on(TestCommand::class, testHandlerFunctionSecond) }
    }
}