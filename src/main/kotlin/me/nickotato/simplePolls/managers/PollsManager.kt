package me.nickotato.simplePolls.managers

import me.nickotato.simplePolls.SimplePolls
import me.nickotato.simplePolls.model.Poll
import org.bukkit.scheduler.BukkitRunnable
import java.time.LocalDateTime

object PollsManager {
    val polls = mutableListOf<Poll>()
    val expiredPolls = mutableListOf<Poll>()
    private var nextId = 0

    fun createPoll(question: String, options: List<String>, durationHours: Long) {
        val endsAt = durationHours.let { LocalDateTime.now().plusHours(durationHours) }

        val poll = Poll(
            id = nextId++,
            question = question,
            options = options.associateWith { 0 }.toMutableMap(),
            endsAt = endsAt,
        )

        polls.add(poll)
    }

    fun beginRunningPerSecond() {
        object : BukkitRunnable() {
            override fun run() {
                checkIfPollsExpired()
            }
        }.runTaskTimer(SimplePolls.instance, 0L, 20L)
    }

    private fun checkIfPollsExpired() {
        val now = LocalDateTime.now()
        for (poll in polls) {
            if (poll.endsAt.isBefore(now)) {
                expiredPolls.add(poll)
                polls.remove(poll)
            }
        }
    }
}