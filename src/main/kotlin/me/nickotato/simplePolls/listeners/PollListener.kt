package me.nickotato.simplePolls.listeners

import me.nickotato.simplePolls.events.PollCreatedEvent
import me.nickotato.simplePolls.events.PollFinishedEvent
import me.nickotato.simplePolls.managers.PollsManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PollListener: Listener {
    @EventHandler
    fun onCreate(event: PollCreatedEvent) {
        val poll = event.poll

        Bukkit.broadcast(Component.text("§3------------------------------"))
        val pollLine = Component.text("§3${poll.question} ")
            .append(
                Component.text("§b/poll")
                    .clickEvent(ClickEvent.runCommand("/poll"))
                    .hoverEvent(HoverEvent.showText(Component.text("§7Click to vote")))
            )
            .append(Component.text("§3 to vote!"))
        Bukkit.broadcast(pollLine)
        Bukkit.broadcast(Component.text("§3------------------------------"))
    }

    @EventHandler
    fun onFinish(event: PollFinishedEvent) {
        val poll = event.poll

        val winner = PollsManager.getOptionWithMostVotes(poll)

        Bukkit.broadcast(Component.text("§6[Poll Results] §e${poll.question} §7→ Winner: §b$winner"))
    }
}