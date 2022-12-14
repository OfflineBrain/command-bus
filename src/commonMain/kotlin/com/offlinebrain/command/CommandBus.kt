package com.offlinebrain.command

import kotlin.reflect.KClass

open class CommandBus {
    private val handlers = mutableMapOf<KClass<out Command>, suspend (Command) -> CommandResult>()

    fun register(commandHandler: CommandHandler) {
        commandHandler
            .registeredCommands
            .forEach { (command, handler) ->
                if (command in handlers) {
                    throw IllegalStateException("Command ${command.simpleName} is already registered")
                } else {
                    handlers[command] = handler
                }
            }
    }

    suspend fun send(command: Command): CommandResult {
        return handlers[command::class]?.invoke(command) ?: Failure("No handler for command ${command::class}")
    }

    suspend fun sendMany(commands: Iterable<Command>): CommandResult {
        return commands.map { send(it) }.reduce { acc, result -> acc + result }
    }
}