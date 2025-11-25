package me.nickotato.simplePolls.model

import java.time.LocalDateTime

data class Poll(
    val id: Int,
    var question: String,
    val options: MutableMap<String, Int>, // Option Text, Amount of Votes
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var endsAt: LocalDateTime,
    val votes: MutableMap<String, String> = mutableMapOf(), //Player UUID, Option they voted for.
)