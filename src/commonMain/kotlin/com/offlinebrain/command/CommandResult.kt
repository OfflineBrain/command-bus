package com.offlinebrain.command

sealed class CommandResult {
    var messages: List<Pair<String, Throwable?>> = listOf()
        internal set

    abstract operator fun plus(result: CommandResult): CommandResult
}

object Success : CommandResult() {
    override fun plus(result: CommandResult): CommandResult = when (result) {
        is Success -> Success
        is Failure -> {
            Failure(result.message).also { it.messages = messages + result.messages }
        }
    }
}

data class Failure(val message: String, private val reason: Throwable? = null) : CommandResult() {
    init {
        messages = listOf(message to reason)
    }

    override fun plus(result: CommandResult): CommandResult = when (result) {
        is Success -> this
        is Failure -> {
            Failure(message + "; " + result.message, null).also { it.messages = messages + result.messages }
        }
    }
}


