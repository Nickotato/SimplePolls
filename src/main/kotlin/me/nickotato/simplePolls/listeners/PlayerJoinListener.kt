package me.nickotato.simplePolls.listeners

import me.nickotato.simplePolls.managers.PollsManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId.toString()

        val unreadCount = PollsManager.polls.count { poll ->
            !poll.votes.contains(uuid)
        }

        if (unreadCount > 0) {
            player.sendMessage("§aYou have §e$unreadCount §aactive poll(s) you haven't voted on yet.")
            player.sendMessage("§7Use §3/poll §7to vote.")
        }
    }
}
