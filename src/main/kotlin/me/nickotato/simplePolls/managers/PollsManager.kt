package me.nickotato.simplePolls.managers

import me.nickotato.simplePolls.SimplePolls
import me.nickotato.simplePolls.data.PollDataStorage
import me.nickotato.simplePolls.data.SessionDataStorage
import me.nickotato.simplePolls.events.PollCreatedEvent
import me.nickotato.simplePolls.events.PollFinishedEvent
import me.nickotato.simplePolls.model.Poll
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.time.LocalDateTime
import java.util.UUID

object PollsManager {

    private const val SESSION_GRACE_MS = 5 * 60 * 1000L

    val polls = PollDataStorage.loadAllPolls()   //mutableListOf<Poll>()
    val expiredPolls = PollDataStorage.loadAllPolls(true)   //mutableListOf<Poll>()
    private val joinTimes = mutableMapOf<UUID, Long>()

    private var nextId = 1

    init {
        val highestActive = polls.maxOfOrNull { it.id } ?: 0
        val highestExpired = expiredPolls.maxOfOrNull { it.id } ?: 0

        nextId = maxOf(highestActive, highestExpired) + 1
    }

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

        Bukkit.getPluginManager().callEvent(PollCreatedEvent(poll))
    }

    fun beginRepeatingTasks() {
        object : BukkitRunnable() {
            override fun run() {
                checkIfPollsExpired()
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

            if (poll.endsAt.isBefore(now)) handleExpiredPolls(iterator, poll)
        }
    }

    private fun handleExpiredPolls(iterator: MutableIterator<Poll>, poll: Poll) {
        expiredPolls.add(poll)
        iterator.remove()
        deletePollFile(poll)
        Bukkit.getPluginManager().callEvent(PollFinishedEvent(poll))
    }

    private fun deletePollFile(poll: Poll) {
        val file = File(SimplePolls.instance.dataFolder, "polldata/${poll.id}.yml")
        if (file.exists()) file.delete()
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

        if (required <= 0 || !poll.playTimeRequirements) return true

        return getContinuousPlayTime(player) >= required
    }

    fun getContinuousPlayTime(player: Player): Long {
//        return continuousPlayTime[player.uniqueId] ?: 0

        val joinedAt = joinTimes[player.uniqueId] ?: return 0L
        return (System.currentTimeMillis() - joinedAt) / 1000

//        val x = player.getStatistic(Statistic.TOTAL_WORLD_TIME)
//        player.lookAt <-- no clue this existed.
    }

    fun startSession(uuid: UUID) {
        joinTimes.putIfAbsent(uuid, System.currentTimeMillis())
    }

    fun removeJoinTime(uuid: UUID) {
        joinTimes.remove(uuid)
    }

    fun saveSessions() {
        SessionDataStorage.saveSession(joinTimes)
    }

    fun restoreSessions(online: Collection<Player>) {
        val (shutdownTime, sessions) = SessionDataStorage.loadSessions()

        if (shutdownTime <= 0) return

        val downtime = System.currentTimeMillis() - shutdownTime

        if (downtime <= SESSION_GRACE_MS) {
            joinTimes.putAll(sessions)
        }

//        if (downtime <= SESSION_GRACE_MS) {
//            file.delete()
//        }

        online.forEach {
            joinTimes.putIfAbsent(it.uniqueId, System.currentTimeMillis())
        }
    }
}
