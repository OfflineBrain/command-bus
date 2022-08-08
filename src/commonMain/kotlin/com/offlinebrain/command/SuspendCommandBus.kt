package com.offlinebrain.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce

class SuspendCommandBus(private val coroutineScope: CoroutineScope) : CommandBus() {

    fun sendAsync(command: Command): Deferred<CommandResult> {
        return coroutineScope.async { send(command) }
    }

    fun sendManyAsync(commands: List<Command>): Deferred<CommandResult> {
        return coroutineScope.async {
            commands.asFlow()
                .buffer(commands.size)
                .map { sendAsync(it) }
                .map { it.await() }
                .reduce { accumulator, value -> accumulator + value }
        }
    }
}