package me.nickotato.simplePolls.events

import me.nickotato.simplePolls.model.Poll
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PollFinishedEvent(
    val poll: Poll
): Event() {
    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}