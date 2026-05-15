package me.nickotato.simplePolls.data

import me.nickotato.simplePolls.SimplePolls
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

object SessionDataStorage {
    private val file by lazy {
        File(SimplePolls.instance.dataFolder, "sessions.yml")
    }

    fun saveSession(joinTimes: Map<UUID, Long>) {
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        val config = YamlConfiguration()

        config.set("shutdown-time", System.currentTimeMillis())

        for ((uuid, joinedAt) in joinTimes) {
            config.set("sessions.$uuid", joinedAt)
        }

        config.save(file)
    }

    fun loadSessions(): Pair<Long, Map<UUID, Long>> {
        if (!file.exists()) {
            return Pair(0L, emptyMap())
        }

        val config = YamlConfiguration.loadConfiguration(file)

        val shutdownTime = config.getLong("shutdown-time", 0L)

        val sessionsSection = config.getConfigurationSection("sessions")
            ?: return Pair(shutdownTime, emptyMap())

        val sessions = mutableMapOf<UUID, Long>()

        for (key in sessionsSection.getKeys(false)) {
            try {
                val uuid = UUID.fromString(key)
                val joinedAt = sessionsSection.getLong(key)

                sessions[uuid] = joinedAt
            } catch (_: IllegalArgumentException) {
                // invalid UUID in file :<
            }
        }

        return Pair(shutdownTime, sessions)
    }
}