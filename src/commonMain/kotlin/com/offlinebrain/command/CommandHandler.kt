package com.offlinebrain.command

import kotlin.reflect.KClass

open class CommandHandler {
    val registeredCommands: MutableMap<KClass<out Command>, suspend (Command) -> CommandResult> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    fun <T : Command> on(command: KClass<T>, handler: suspend (T) -> CommandResult) {
        if (command in registeredCommands) {
            throw IllegalStateException("Command ${command.simpleName} is already registered")
        }
        registeredCommands[command] = handler as suspend (Command) -> CommandResult
    }

    inline fun <reified T : Command> on(noinline handler:  suspend (T) -> CommandResult) = on(T::class, handler)
}