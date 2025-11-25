package me.nickotato.simplePolls.data

import me.nickotato.simplePolls.SimplePolls
import me.nickotato.simplePolls.model.Poll
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.time.LocalDateTime

object PollDataStorage {
    private var dataFolder = File(SimplePolls.instance.dataFolder, "polldata")
    private var dataFolder2 = File(SimplePolls.instance.dataFolder, "expiredpolldata")

    init {
        if (!dataFolder.exists()) dataFolder.mkdirs()
        if (!dataFolder2.exists()) dataFolder2.mkdirs()
    }

    private fun savePollData(expired: Boolean = false, poll: Poll) {
        val file = when (expired) {
            true -> File(dataFolder2, "${poll.id}.yml")
            false -> File(dataFolder, "${poll.id}.yml")
        }
        val config = YamlConfiguration()

        config.set("id", poll.id.toString())
        config.set("question", poll.question)
        config.createSection("options", poll.options)
        config.createSection("votes", poll.votes)
        config.set("createdAt", poll.createdAt.toString())
        config.set("endsAt", poll.endsAt.toString())

        config.save(file)
    }

    fun saveAllPolls(expired: Boolean = false, polls: MutableList<Poll>) {
        if (polls.isEmpty()) return
        for (poll in polls) {
            savePollData(expired, poll)
        }
    }

    private fun loadPollData(id: Int, expired: Boolean = false): Poll? {
        val file = if (expired)
            File(dataFolder2, "$id.yml")
        else
            File(dataFolder, "$id.yml")

        if (!file.exists()) return null

        val config = YamlConfiguration.loadConfiguration(file)
        val question = config.getString("question") ?: return null
        val options = config.getConfigurationSection("options")
            ?.getValues(false)
            ?.mapValues { it.value as Int }
            ?.toMutableMap()
            ?:mutableMapOf()

        val votes = config.getConfigurationSection("votes")
            ?.getValues(false)
            ?.mapValues { it.value as String }
            ?.toMutableMap()
            ?: mutableMapOf()

        val createdAtStr = config.getString("createdAt") ?: return null
        val endsAtStr = config.getString("endsAt") ?: return null

        val createdAt = LocalDateTime.parse(createdAtStr)
        val endsAt = LocalDateTime.parse(endsAtStr)


        return Poll(
            id = id,
            question = question,
            options = options,
            votes = votes,
            createdAt = createdAt,
            endsAt = endsAt
        )
    }

    fun loadAllPolls(expired: Boolean = false): MutableList<Poll> {
        val folder = if (expired) dataFolder2 else dataFolder
        val polls = mutableListOf<Poll>()
        val files = folder.listFiles { f -> f.name.endsWith(".yml") } ?: return polls

        for (file in files) {
            val id = file.nameWithoutExtension.toIntOrNull() ?: continue
            val poll = loadPollData(id, expired)
            if (poll != null) polls.add(poll)
        }

        return polls
    }
}