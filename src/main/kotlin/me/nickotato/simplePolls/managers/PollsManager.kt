package me.nickotato.simplePolls.managers

import me.nickotato.simplePolls.SimplePolls
import me.nickotato.simplePolls.data.PollDataStorage
import me.nickotato.simplePolls.model.Poll
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.time.LocalDateTime
import java.util.UUID

object PollsManager {
    val polls = PollDataStorage.loadAllPolls()   //mutableListOf<Poll>()
    val expiredPolls = PollDataStorage.loadAllPolls(true)   //mutableListOf<Poll>()

    val continuousPlayTime = mutableMapOf<UUID, Long>()

    private var nextId = 1

    init {
        val highestActive = polls.maxOfOrNull { it.id } ?: 0
        val highestExpired = expiredPolls.maxOfOrNull { it.id } ?: 0

        nextId = maxOf(highestActive, highestExpired) + 1
    }


//    fun createPoll(question: String, options: List<String>, durationSeconds: Long) {
//        val endsAt = durationSeconds.let { LocalDateTime.now().plusSeconds(durationSeconds) }
//
//        val poll = Poll(
//            id = nextId++,
//            question = question,
//            options = options.associateWith { 0 }.toMutableMap(),
//            endsAt = endsAt,
//        )
//
//        polls.add(poll)
//    }

    fun createPoll(
        question: String,
        options: List<String>,
        durationSeconds: Long,
        anonymous: Boolean,
        minimumUnlockTime: Long,
    ) {
        val endsAt = durationSeconds.let { LocalDateTime.now().plusSeconds(durationSeconds) }

        val poll = Poll(
            id = nextId++,
            question = question,
            options = options.associateWith { 0 }.toMutableMap(),
            anonymous = anonymous,
            endsAt = endsAt,
            minimumUnlockTime = minimumUnlockTime,
        )

        polls.add(poll)
    }

    fun beginRepeatingTasks() {
        object : BukkitRunnable() {
            override fun run() {
                checkIfPollsExpired()
                updatePlayerContinuousTime()
            }
        }.runTaskTimer(SimplePolls.instance, 0L, 20L)

        object : BukkitRunnable() {
            override fun run() {
                save()
            }
        }.runTaskTimer(SimplePolls.instance, 0L, 20 * 20)
    }

    private fun checkIfPollsExpired() {
        val now = LocalDateTime.now()

        val iterator = polls.iterator()

        while (iterator.hasNext()) {
            val poll = iterator.next()

            if (poll.endsAt.isBefore(now)) {

                expiredPolls.add(poll)

                iterator.remove()

                val file = File(SimplePolls.instance.dataFolder, "polldata/${poll.id}.yml")
                if (file.exists()) file.delete()
            }
        }
    }

    private fun updatePlayerContinuousTime() {

        val onlinePlayers = Bukkit.getOnlinePlayers()
        val onlineUUIDs = onlinePlayers.map { it.uniqueId }.toSet()

        continuousPlayTime.keys.removeIf { it !in onlineUUIDs }

        for (player in onlinePlayers) {
            val uuid = player.uniqueId

            continuousPlayTime[uuid] =
                continuousPlayTime.getOrDefault(uuid, 0L) + 1L
        }
    }

    fun setPlayersAnswer(poll: Poll, player: Player, choice: String) {
        val uuid = player.uniqueId.toString()

        // already voted
        if (poll.votedPlayers.contains(uuid)) {
            return
        }

        if (!canVote(player, poll)) {
            player.sendMessage("§cYou have not played long enough to vote in this poll.")
            return
        }

        poll.votedPlayers.add(uuid)

        if (!poll.anonymous) {
            poll.votes[uuid] = choice
        }

        poll.options[choice] =
            poll.options.getOrDefault(choice, 0) + 1
    }

//    private fun calculateOptionsVotes(poll: Poll) {
//        for (option in poll.options.keys) {
//            poll.options[option] = 0
//        }
//
//        for ((_, votedOption) in poll.votes) {
//            poll.options[votedOption] = poll.options.getOrDefault(votedOption, 0) + 1
//        }
//    }

    fun getOptionWithMostVotes(poll: Poll): String {
        if (poll.options.isEmpty()) return "None"

        val top = poll.options.maxByOrNull { it.value } ?: return "None"
        return top.key
    }

    fun save() {
        PollDataStorage.saveAllPolls(false, polls)
        PollDataStorage.saveAllPolls(true, expiredPolls)
    }

    fun canVote(player: Player, poll: Poll): Boolean {
        val required = poll.minimumUnlockTime

        if (required <= 0) return true

        val playtimeSeconds =
            continuousPlayTime[player.uniqueId] ?: 0L

        return playtimeSeconds >= required
    }

    fun getContinuousPlayTime(player: Player): Long {
        return continuousPlayTime[player.uniqueId] ?: 0
    }
}
